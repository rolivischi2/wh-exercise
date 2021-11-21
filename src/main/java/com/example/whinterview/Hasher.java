package com.example.whinterview;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Hasher {

    public static String messageSha256ToBase64(Message message) throws NoSuchAlgorithmException {
        //`Base64 ( SHA256-Hash ( from-email + nonce + to-email + nonce + message body ))
        String hashString = message.getFrom() + message.getNonce() + message.getTo() + message.getNonce() + message.getBody();
        byte[] b = hashString.getBytes(StandardCharsets.UTF_8);
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        digester.update(b);
        return Base64.getEncoder().encodeToString(digester.digest());
    }


}
