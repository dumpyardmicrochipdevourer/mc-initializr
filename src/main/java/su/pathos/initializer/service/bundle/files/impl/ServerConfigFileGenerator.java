package su.pathos.initializer.service.bundle.files.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import su.pathos.initializer.cfg.ServerConfig;
import su.pathos.initializer.service.bundle.files.BundleFileGenerator;

import java.io.IOException;
import java.util.Map;

@Component
@Order(2)
public class ServerConfigFileGenerator implements BundleFileGenerator {
    private final ObjectMapper json;

    public ServerConfigFileGenerator(ObjectMapper json) {
        this.json = json;
    }

    @Override
    public Map<String, String> generate(ServerConfig config) throws IOException {
        return Map.of("server.json", json.writerWithDefaultPrettyPrinter()
            .writeValueAsString(config) + "\n");
    }
}
