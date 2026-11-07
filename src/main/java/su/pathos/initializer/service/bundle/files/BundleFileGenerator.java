package su.pathos.initializer.service.bundle.files;

import su.pathos.initializer.cfg.ServerConfig;

import java.io.IOException;
import java.util.Map;

public interface BundleFileGenerator {
    Map<String, String> generate(ServerConfig config) throws IOException;
}
