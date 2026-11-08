package su.pathos.initializer.service.bundle.files.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import su.pathos.initializer.cfg.ServerConfig;
import su.pathos.initializer.service.bundle.files.BundleFileGenerator;
import su.pathos.initializer.service.bundle.util.BundleResourceReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Component
@Order(3)
public class ReadmeFileGenerator implements BundleFileGenerator {
    private final BundleResourceReader resources;

    public ReadmeFileGenerator(BundleResourceReader resources) {
        this.resources = resources;
    }

    @Override
    public Map<String, String> generate(ServerConfig config) throws IOException {
        String readme = resources.read("README.md")
            .replace("@@NAME@@", config.name)
            .replace("@@PORT@@", Integer.toString(config.port))
            .replace("@@MEMORY@@", BigDecimal.valueOf(config.memory + 2).stripTrailingZeros().toPlainString())
            .replace("@@BACKUP@@", config.backup.id());
        return Map.of("README.md", readme);
    }
}
