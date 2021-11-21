package com.example.whinterview;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class Parser {

    private final Map<String, Message> messageStore = new HashMap<>();

    public void parseFile(String input) throws IOException, ApplicationException, NoSuchAlgorithmException {
        Path path = Paths.get(input);

        BufferedReader bufferedReader = Files.newBufferedReader(path);

        List<String> messageParts = new ArrayList<>();
        String nextLine;
        while((nextLine = bufferedReader.readLine()) != null){
            if(nextLine.matches("^\\.\\r?\\n?$")){
                handleMessage(messageParts);
                messageParts = new ArrayList<>();
                continue;
            }
            messageParts.add(nextLine);
        }

    }

    private void handleMessage(List<String> messageParts) throws ApplicationException, NoSuchAlgorithmException {
        // sometimes simple is best :)
        Message message = Message.builder()
                .body("")
                .build();
        for(int i = 0; i < messageParts.size(); i++){
            switch (i) {
                case 0, 1, 2, 3 -> message = handlePart(message, messageParts.get(i));
                case 4 -> handleEndOfHeader(messageParts.get(i));
                default -> message = message.withBody(message.getBody().concat(messageParts.get(i) + "\n"));
            }
        }
        if(!message.isValid()){
            throw new ApplicationException("Message is not valid! Missing headers!");
        }
        String hash = messageSha256ToBase64(message);
        if(hash.equals(message.getStamp())){
            messageStore.put(hash, message);
        }
    }

    private void handleEndOfHeader(String input) throws ApplicationException {
        if(input == null || !input.isBlank()){
            throw new ApplicationException("Missing empty line after header section!");
        }
    }

    public void outputMessages(){
        log.info("Store size: {}", this.messageStore.size());
        this.messageStore.forEach((key, value) -> log.info("MessageStore Element with Hash {} and {}", key, value));
    }


    private Message handlePart(Message message, String part) throws ApplicationException {
        if(part.startsWith(MessagePart.FROM.value)){
            return message.withFrom(returnValidEmailField(part));
        } else if (part.startsWith(MessagePart.TO.value)){
            return message.withTo(returnValidEmailField(part));
        } else if (part.startsWith(MessagePart.STAMP.value)){
            return message.withStamp(splitHeader(part));
        } else if(part.startsWith(MessagePart.NONCE.value)){
            return message.withNonce(splitHeader(part));
        }
        return message;
    }


    public String messageSha256ToBase64(Message message) throws NoSuchAlgorithmException {
        //`Base64 ( SHA256-Hash ( from-email + nonce + to-email + nonce + message body ))
        String hashString = message.getFrom() + message.getNonce() + message.getTo() + message.getNonce() + message.getBody();
        byte[] b = hashString.getBytes(StandardCharsets.UTF_8);
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        digester.update(b);
        return Base64.getEncoder().encodeToString(digester.digest());
    }

    private String  returnValidEmailField(String line) throws ApplicationException {
        String email = splitHeader(line);
        if(!EmailValidator.getInstance().isValid(email)){
            throw new ApplicationException("Invalid email address!");
        }
        return email;
    }

    private String splitHeader(String headerLine) throws ApplicationException {
        String[] parts = headerLine.split(":");
        if(parts.length < 2){
            throw new ApplicationException("Invalid header line in message");
        }
        return parts[1].trim();
    }
}
