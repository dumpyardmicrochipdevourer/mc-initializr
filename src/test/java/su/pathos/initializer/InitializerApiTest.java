package su.pathos.initializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InitializerApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void catalog() throws Exception {
        var response = mvc.perform(get("/api/catalog")).andExpect(status().isOk())
            .andExpect(jsonPath("$.versionDetails[0].javaVersions.PAPER").value(25))
            .andReturn().getResponse().getContentAsString();
        var catalog = json.readTree(response);
        var config = json.readTree(preview("{}").get("server.json").asText());
        assertThat(config).isEqualTo(catalog.get("defaults"));
        assertThat(catalog.get("engines").isEmpty()).isFalse();
        var memory = catalog.get("memoryOptions");
        assertThat(memory.size()).isEqualTo(63);
        assertThat(memory.get(0).asDouble()).isEqualTo(1);
        assertThat(memory.get(1).asDouble()).isEqualTo(1.5);
        assertThat(memory.get(62).asDouble()).isEqualTo(32);
        assertThat(catalog.get("versionDetails").size()).isEqualTo(catalog.get("versions").size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"none", "local", "s3"})
    void bundle(String backup) throws Exception {
        var body = json.writeValueAsString(Map.of(
            "name", "test-world", "backup", backup, "motd", "Bob's $HOME", "eula", true
        ));
        var response = mvc.perform(post("/api/generate").contentType("application/json").content(body))
            .andExpect(status().isOk()).andExpect(content().contentType("application/zip"))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"test-world.zip\""))
            .andReturn().getResponse();
        var files = json.createObjectNode();
        try (var zip = new ZipInputStream(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            for (var entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                assertThat(entry.getName()).startsWith("test-world/");
                files.put(entry.getName().substring("test-world/".length()),
                    new String(zip.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        assertThat(files).isEqualTo(preview(body));
        var compose = files.get("compose.yaml").asText();
        assertThat(compose).contains("Bob''s $$HOME");
        if (backup.equals("none")) assertThat(compose).doesNotContain("backup:");
        else assertThat(compose).contains("BACKUP_METHOD: " + (backup.equals("s3") ? "restic" : "tar"));
    }

    @ParameterizedTest
    @CsvSource({"name,../world", "engine,unknown", "version,unknown", "memory,0.5", "memory,32.5",
        "viewDistance,1", "viewDistance,25", "simulationDistance,2", "simulationDistance,33", "worldType,unknown", "operators,bad name",
        "spawnProtection,-1", "spawnProtection,1025"})
    void validation(String field, String value) throws Exception {
        mvc.perform(post("/api/preview").contentType("application/json")
                .content(json.writeValueAsString(Map.of(field, value))))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors." + field).exists());
    }

    @ParameterizedTest
    @CsvSource({"1.17.1,false", "1.18,true", "26.3,true"})
    void distances(String version, boolean simulationSupported) throws Exception {
        var files = preview(json.writeValueAsString(Map.of(
            "engine", "VANILLA", "version", version, "viewDistance", 12, "simulationDistance", 6)));
        var compose = files.get("compose.yaml").asText();
        assertThat(compose).contains("VIEW_DISTANCE: '12'");
        if (simulationSupported) assertThat(compose).contains("SIMULATION_DISTANCE: '6'");
        else assertThat(compose).doesNotContain("SIMULATION_DISTANCE");
        assertThat(json.readTree(files.get("server.json").asText()).get("simulationDistance").asInt()).isEqualTo(6);
    }

    @ParameterizedTest
    @CsvSource({"1.16.5,large_biomes,largeBiomes", "1.19,large_biomes,minecraft:large_biomes",
        "26.3,normal,minecraft:normal", "1.2.5,flat,flat"})
    void world(String version, String type, String levelType) throws Exception {
        var files = preview(json.writeValueAsString(Map.of(
            "engine", "VANILLA", "version", version, "seed", "Bob's $HOME", "worldType", type,
            "pvp", false, "hardcore", false, "operators", "Steve,Alex", "spawnProtection", 0)));
        assertThat(files.get("compose.yaml").asText()).contains(
            "SEED: 'Bob''s $$HOME'", "LEVEL_TYPE: '" + levelType + "'", "PVP: 'false'", "OPS: 'Steve,Alex'");
        assertThat(json.readTree(files.get("server.json").asText()).get("seed").asText()).isEqualTo("Bob's $HOME");
    }

    @Test
    void hardcore() throws Exception {
        var compose = preview("{\"hardcore\":true,\"spawnProtection\":8}").get("compose.yaml").asText();
        assertThat(compose).contains("HARDCORE: 'true'", "SPAWN_PROTECTION: '8'");
    }

    @ParameterizedTest
    @CsvSource({"1,1024", "1.5,1536", "32,32768"})
    void memory(double gib, int mib) throws Exception {
        var files = preview(json.writeValueAsString(Map.of("memory", gib)));
        assertThat(files.get("compose.yaml").asText()).contains("MEMORY: '" + mib + "M'");
        assertThat(json.readTree(files.get("server.json").asText()).get("memory").asDouble()).isEqualTo(gib);
    }

    @ParameterizedTest
    @CsvSource({"VANILLA,1.16.5,8", "PAPER,1.16.5,16", "VANILLA,1.17.1,16",
        "VANILLA,1.18.2,17", "VANILLA,1.20.5,21", "VANILLA,1.2.5,8",
        "PAPER,1.12.2,11", "PAPER,1.8.8,8", "PAPER,26.3,25"})
    void runtime(String engine, String version, int java) throws Exception {
        var files = preview(json.writeValueAsString(Map.of("engine", engine, "version", version)));
        assertThat(files.get("compose.yaml").asText()).contains("itzg/minecraft-server:java" + java + "\n");
    }

    @ParameterizedTest
    @CsvSource({"PAPER,1.8.9", "FABRIC,1.12.2"})
    void unsupportedVersion(String engine, String version) throws Exception {
        mvc.perform(post("/api/preview").contentType("application/json")
                .content(json.writeValueAsString(Map.of("engine", engine, "version", version))))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.versionSupported").exists());
    }

    @Test
    void malformedJson() throws Exception {
        mvc.perform(post("/api/preview").contentType("application/json").content("{broken"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isString())
            .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void eula() throws Exception {
        mvc.perform(post("/api/generate").contentType("application/json").content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.eula").exists());
    }

    private JsonNode preview(String body) throws Exception {
        var response = mvc.perform(post("/api/preview").contentType("application/json").content(body))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(response);
    }
}
