package su.pathos.initializer.service.bundle;

import org.springframework.stereotype.Service;
import su.pathos.initializer.cfg.ServerConfig;
import su.pathos.initializer.service.bundle.files.BundleFileGenerator;
import su.pathos.initializer.service.bundle.util.ZipArchiveWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BundleService {
    private final List<BundleFileGenerator> generators;
    private final ZipArchiveWriter archives;

    public BundleService(List<BundleFileGenerator> generators, ZipArchiveWriter archives) {
        this.generators = List.copyOf(generators);
        this.archives = archives;
    }

    public Map<String, String> files(ServerConfig config) throws IOException {
        var files = new LinkedHashMap<String, String>();
        for (var generator : generators) {
            for (var file : generator.generate(config).entrySet()) {
                if (files.putIfAbsent(file.getKey(), file.getValue()) != null) {
                    throw new IllegalStateException("Duplicate bundle file: " + file.getKey());
                }
            }
        }
        return files;
    }

    public byte[] zip(ServerConfig config) throws IOException {
        return archives.write(config.name, files(config));
    }
}
