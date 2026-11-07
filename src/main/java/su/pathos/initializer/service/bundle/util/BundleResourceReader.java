package su.pathos.initializer.service.bundle.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BundleResourceReader {
    public String read(String name) throws IOException {
        try (var stream = new ClassPathResource("bundle/" + name).getInputStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
