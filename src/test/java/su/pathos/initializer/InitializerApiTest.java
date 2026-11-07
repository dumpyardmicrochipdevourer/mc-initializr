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
            .andReturn().getResponse().getContentAsString();
        var catalog = json.readTree(response);
        var config = json.readTree(preview("{}").get("server.json").asText());
        assertThat(config).isEqualTo(catalog.get("defaults"));
        assertThat(catalog.get("engines").isEmpty()).isFalse();
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
    @CsvSource({"name,../world", "engine,unknown", "version,unknown"})
    void validation(String field, String value) throws Exception {
        mvc.perform(post("/api/preview").contentType("application/json")
                .content(json.writeValueAsString(Map.of(field, value))))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors." + field).exists());
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
