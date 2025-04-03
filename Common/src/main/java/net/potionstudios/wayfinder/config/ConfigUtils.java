package net.potionstudios.wayfinder.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.potionstudios.wayfinder.PlatformHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigUtils {

    /** The Gson instance for the Config Loader. */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads or Creates a config file
     *
     * @param clazz      The class of the config file.
     * @return The config file.
     */
    public static <T> T loadConfig(@NotNull Class<T> clazz) {
        try {
            Path configPath = PlatformHandler.PLATFORM_HANDLER.configPath().resolve("wayfiinder.json");
            T value = clazz.getConstructor().newInstance();

            if (Files.notExists(configPath)) Files.createDirectories(configPath.getParent());
            else if (Files.exists(configPath)) value = GSON.fromJson(Files.newBufferedReader(configPath), clazz);

            Files.writeString(configPath, GSON.toJson(value));
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.", e);
        }
    }

    public record CommentValue<T>(String comment, T value) {
        public static <T> CommentValue<T> of(String comment, T value) {
            return new CommentValue<>(comment, value);
        }
    }
}
