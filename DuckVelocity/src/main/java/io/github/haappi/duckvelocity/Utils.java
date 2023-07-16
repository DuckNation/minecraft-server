package io.github.haappi.duckvelocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.haappi.duckvelocity.DuckVelocity.httpClient;

public class Utils {

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
        StringBuilder queryString = new StringBuilder();
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
            System.out.println(object);

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

    public static Duration parseTime(String input) {
        Pattern pattern = Pattern.compile("(\\d+d)?(\\d+hr)?(\\d+m)?");
        Matcher matcher = pattern.matcher(input);

        int days = 0;
        int hours = 0;
        int minutes = 0;

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                days = Integer.parseInt(matcher.group(1).replaceAll("d", ""));
            }
            if (matcher.group(2) != null) {
                hours = Integer.parseInt(matcher.group(2).replaceAll("hr", ""));
            }
            if (matcher.group(3) != null) {
                minutes = Integer.parseInt(matcher.group(3).replaceAll("m", ""));
            }
        }

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
    }

    public static long parseTimeToMillis(String input) {
        Duration duration = parseTime(input);

        return duration.toMillis();
    }

    public static List<String> getPlayersFromInvocation(SimpleCommand.Invocation invocation) {
        List<String> players = new ArrayList<>();
        for (Player player : DuckVelocity.getInstance().getProxy().getAllPlayers()) {
            if (player.getUsername().toLowerCase().startsWith(invocation.arguments()[0].toLowerCase())) {
                players.add(player.getUsername());
            }
        }
        return players;
    }

    public static String parseMillisToTime(long milliseconds) {
        // Convert milliseconds to a Duration object
        Duration duration = Duration.ofMillis(milliseconds);

        // Extract the individual components of the duration
        long days = duration.toDaysPart();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        // Build the human-friendly time format
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("day ");
        }
        if (hours > 0) {
            sb.append(hours).append("hour ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("minute ");
        }
        if (seconds > 0) {
            sb.append(seconds).append("second");
        }

        return sb.toString().trim();
    }

}
