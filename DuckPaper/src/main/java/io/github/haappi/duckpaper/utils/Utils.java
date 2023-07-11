package io.github.haappi.duckpaper.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.haappi.duckpaper.DuckPaper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.haappi.duckpaper.DuckPaper.httpClient;

public class Utils {
    public static BukkitTask scheduleNextTick(Runnable task) {
        return Bukkit.getScheduler().runTask(DuckPaper.getInstance(), task);
    }

    public static BukkitTask scheduleNextTickAsync(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(DuckPaper.getInstance(), task);
    }

    public static BukkitTask scheduleNextTick(Runnable task, boolean async) {
        if (async) {
            return scheduleNextTickAsync(task);
        } else {
            return scheduleNextTick(task);
        }
    }

    public static BukkitTask runTaskLater(Runnable task, long ticks) {
        return Bukkit.getScheduler().runTaskLater(DuckPaper.getInstance(), task, ticks);
    }

    public static BukkitTask runTaskLater(Runnable task, int ms) {
        return runTaskLater(task, (long) ms / 50);
    }

    public static BukkitTask runTaskLater(Runnable task, int ms, boolean async) {
        if (async) {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(DuckPaper.getInstance(), task, (long) ms / 50L);
        }
        return runTaskLater(task, (long) ms / 50);
    }

    public static String titleCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static byte[] stringToByteArray(String message, String delim) {
        String[] msg = message.split(delim);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String s : msg) {
            out.writeUTF(s);
        }

        return out.toByteArray();
    }

    public static byte[] stringToByteArray(String message) {
        return stringToByteArray(message, ";");
    }

    public static String createQueryString(HashMap<String, Object> params) {
        StringBuilder queryString = new StringBuilder("&");
        params.put("key", Config.API_KEY);
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String value = URLEncoder.encode(sanitizeQueryParam(entry.getValue().toString()), StandardCharsets.UTF_8);
                queryString.append(key).append("=").append(value).append("&");
            }
            if (!queryString.isEmpty()) {
                queryString.deleteCharAt(queryString.length() - 1); // Remove trailing "&"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryString.toString();
    }

    public static String sanitizeQueryParam(Object value) {
        return value.toString().replaceAll("[^a-zA-Z0-9-]", ""); // only allow alphanumeric characters and dashes
    }

    public static List<Object> performHttpRequest(HttpEntityEnclosingRequestBase request) {
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());
            JSONObject object = new JSONObject(stringResponse);

            return List.of(statusCode, object);
        } catch (IOException ignored) {
            return List.of(500, new JSONObject().put("detail", "Internal server error"));
        }
    }

    public static List<Object> performHttpRequest(HttpDelete delete) {
        try {
            HttpResponse response = httpClient.execute(delete);
            int statusCode = response.getStatusLine().getStatusCode();
            String stringResponse = EntityUtils.toString(response.getEntity());
            JSONObject object = new JSONObject(stringResponse);

            return List.of(statusCode, object);
        } catch (IOException ignored) {
            return List.of(500, new JSONObject().put("detail", "Internal server error"));
        }
    }

    public static String[] removeFirstElement(String[] array) {
        if (array == null || array.length == 0) {
            return array;
        }

        String[] newArray = new String[array.length - 1];

        System.arraycopy(array, 1, newArray, 0, newArray.length);

        return newArray;
    }

}
