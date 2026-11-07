package su.pathos.initializer.web;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import su.pathos.initializer.cfg.ServerConfig;
import su.pathos.initializer.service.bundle.BundleService;
import su.pathos.initializer.service.catalog.ServerCatalogService;
import su.pathos.initializer.web.dto.CatalogResponse;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InitializerController {
    private final BundleService bundles;
    private final ServerCatalogService catalog;

    public InitializerController(BundleService bundles, ServerCatalogService catalog) {
        this.bundles = bundles;
        this.catalog = catalog;
    }

    @GetMapping("/catalog")
    public CatalogResponse catalog() {
        return catalog.catalog();
    }

    @PostMapping("/preview")
    public Map<String, String> preview(@Valid @RequestBody ServerConfig config) throws IOException {
        return bundles.files(config);
    }

    @PostMapping(value = "/generate", produces = "application/zip")
    public ResponseEntity<byte[]> generate(@Validated(ServerConfig.Generation.class) @RequestBody ServerConfig config) throws IOException {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + config.name + ".zip\"")
            .cacheControl(CacheControl.noStore()).body(bundles.zip(config));
    }
}
