package io.github.haappi.ducksmp.utils;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class Encryption {

    public Encryption() {
        throw new RuntimeException("Unable to load a static class.");
    }

    public static String encrypt(final String strToEncrypt) {
        setKey();
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: ");
            e.printStackTrace();
        }
        return null;
    }    private static SecretKeySpec secretKey = setKey();

    public static SecretKeySpec setKey() {
        MessageDigest sha;
        Plugin plugin = Bukkit.getPluginManager().getPlugin(DuckSMP.getInstance().getName());
        if (plugin == null) {
            Logger.getAnonymousLogger().severe("Could not find plugin!");
            return null;
        }
        final String myKey = plugin.getConfig().getString("secretKeyIP");
        if (myKey == null) {
            Bukkit.getLogger().severe("No secret key set in config.yml! Set it via " +
                    "opening the config file and setting the 'secretKey' to a string of your choosing.");
            Bukkit.getLogger().severe("DuckSMP: Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return null;
        }
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            secretKey = keySpec;
            return keySpec;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }




}
