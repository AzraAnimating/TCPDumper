package de.azraanimating.tcpdumper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;

public class Config {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String unitToTrigger = "mbit";
    public double bandWidth = 10.0;
    public int cooldownToNextDumpMS = 10000;
    public String tcpDumpDuration = "10m";
    public String webhookURLs = "insertWebhook";

    public Config toFile(final File file) throws Exception {
        file.mkdirs();
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        Files.writeString(file.toPath(), Config.gson.toJson(this, Config.class));
        return this;
    }

    public static Config fromFile(final File file) throws Exception {
        if (file.exists()) {
            return Config.fromString(Files.readString(file.toPath()));
        } else {
            return new Config().toFile(file);
        }
    }

    public static Config fromString(final String jsonText) throws Exception {
        return Config.gson.fromJson(jsonText, Config.class);
    }

}