package io.github.haappi.duckvelocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
