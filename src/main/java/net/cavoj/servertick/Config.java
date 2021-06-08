package net.cavoj.servertick;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public final boolean requireOP;

    public Config(Path configPath) {
        if (!Files.exists(configPath)) {
            createDefault(configPath);
        }
        Toml toml = new Toml().read(configPath.toFile());
        this.requireOP = toml.getBoolean("requireOP");
    }

    private static void createDefault(Path configPath) {
        TomlWriter tw = new TomlWriter();
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("requireOP", true);
        try {
            tw.write(defaults, configPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
