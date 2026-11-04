package com.easykillmobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

final class EasyKillMobsConfig {

    private static final Path FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("easykillmobs.properties");

    private EasyKillMobsConfig() {
    }

    static void load() {

        Properties properties = new Properties();

        if (!Files.exists(FILE)) {
            save();
            return;
        }

        try (InputStream in = Files.newInputStream(FILE)) {

            properties.load(in);

            EasyKillMobsClient.setDelayMsFromConfig(
                    Integer.parseInt(
                            properties.getProperty("delayMs", "500")
                    )
            );

            EasyKillMobsClient.setEnabledFromConfig(
                    Boolean.parseBoolean(
                            properties.getProperty("enabled", "false")
                    )
            );

            EasyKillMobsClient.setPermanentlyDisabledFromConfig(
                    Boolean.parseBoolean(
                            properties.getProperty("permanentlyDisabled", "false")
                    )
            );

        } catch (IOException | NumberFormatException ignored) {
            // Use default settings if config is damaged.
        }
    }

    static void save() {

        Properties properties = new Properties();

        properties.setProperty(
                "delayMs",
                Integer.toString(EasyKillMobsClient.getDelayMs())
        );

        properties.setProperty(
                "enabled",
                Boolean.toString(EasyKillMobsClient.isEnabled())
        );

        properties.setProperty(
                "permanentlyDisabled",
                Boolean.toString(EasyKillMobsClient.isPermanentlyDisabled())
        );

        try {

            Files.createDirectories(FILE.getParent());

            try (OutputStream out = Files.newOutputStream(FILE)) {
                properties.store(out, "EasyKillMobs settings");
            }

        } catch (IOException ignored) {
            // Config saving failure should not crash the game.
        }
    }
}
