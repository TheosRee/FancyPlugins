package de.oliver.fancylib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/*
    From: https://gist.github.com/Jofkos/d0c469528b032d820f42
 */

public class UUIDFetcher {

    private static final ExtendedFancyLogger LOGGER = new ExtendedFancyLogger("UUIDFetcher");
    private static final String UUID_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/name/%s";
    private static final String NAME_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/%s";
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final Map<String, UUID> UUID_CACHE = new HashMap<>();
    private static final Map<UUID, String> NAME_CACHE = new HashMap<>();
    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    private String name;
    private UUID id;

    /**
     * Fetches the uuid asynchronously and passes it to the consumer.
     *
     * @param name   The name
     * @param action Do what you want to do with the uuid her
     */
    public static void getUUID(String name, Consumer<UUID> action) {
        POOL.execute(() -> action.accept(getUUID(name)));
    }


    /**
     * Fetches the uuid synchronously and returns it.
     *
     * @param name The name
     */
    public static UUID getUUID(String name) {
        name = name.toLowerCase(Locale.ROOT);
        if (UUID_CACHE.containsKey(name)) {
            return UUID_CACHE.get(name);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name)).openConnection();
            connection.setReadTimeout(5000);
            UUIDFetcher data = GSON.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            UUID_CACHE.put(name, data.id);
            NAME_CACHE.put(data.id, data.name);

            return data.id;
        } catch (Exception e) {
            LOGGER.error("Could not fetch UUID for name: " + name, ThrowableProperty.of(e));
            return null;
        }
    }

    /**
     * Fetches the name asynchronously and passes it to the consumer.
     *
     * @param uuid   The uuid
     * @param action Do what you want to do with the name her
     */
    public static void getName(UUID uuid, Consumer<String> action) {
        POOL.execute(() -> action.accept(getName(uuid)));
    }

    /**
     * Fetches the name synchronously and returns it.
     *
     * @param uuid The uuid
     * @return The name
     */
    public static String getName(UUID uuid) {
        if (NAME_CACHE.containsKey(uuid)) {
            return NAME_CACHE.get(uuid);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);
            UUIDFetcher currentNameData = GSON.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            UUID_CACHE.put(currentNameData.name.toLowerCase(Locale.ROOT), uuid);
            NAME_CACHE.put(uuid, currentNameData.name);

            return currentNameData.name;
        } catch (Exception e) {
            LOGGER.error("Could not fetch name for uuid: " + uuid, ThrowableProperty.of(e));
            return null;
        }
    }

}

class UUIDTypeAdapter extends TypeAdapter<UUID> {
    public static String fromUUID(UUID value) {
        return value.toString().replace("-", "");
    }

    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    @Override
    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(fromUUID(value));
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        return fromString(in.nextString());
    }
}
