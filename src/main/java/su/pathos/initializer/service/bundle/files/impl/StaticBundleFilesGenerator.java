package su.pathos.initializer.service.bundle.files.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import su.pathos.initializer.cfg.ServerConfig;
import su.pathos.initializer.service.bundle.files.BundleFileGenerator;
import su.pathos.initializer.service.bundle.util.BundleResourceReader;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(1)
public class StaticBundleFilesGenerator implements BundleFileGenerator {
    private final BundleResourceReader resources;

    public StaticBundleFilesGenerator(BundleResourceReader resources) {
        this.resources = resources;
    }

    @Override
    public Map<String, String> generate(ServerConfig config) throws IOException {
        var files = new LinkedHashMap<String, String>();
        files.put("setup.sh", resources.read("setup.sh"));
        files.put("manage.sh", resources.read("manage.sh"));
        files.put("configure.py", resources.read("configure.py"));
        files.put("ansible/ansible.cfg", resources.read("ansible.cfg"));
        files.put("ansible/site.yml", resources.read("site.yml"));
        files.put("requirements.txt", "ansible-core==2.19.3\n");
        files.put(".env.example", "# setup.sh creates .env on the VPS with a random RCON password.\n# For S3, it also asks for RESTIC_REPOSITORY, RESTIC_PASSWORD, AWS_ACCESS_KEY_ID,\n# AWS_SECRET_ACCESS_KEY and AWS_DEFAULT_REGION. Never send those to the website.\n");
        return files;
    }
}
