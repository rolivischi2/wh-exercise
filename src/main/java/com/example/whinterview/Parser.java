package com.example.whinterview;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

    public void outputMessages(){
        log.info("Store size: {}", this.messageStore.size());
        this.messageStore.forEach((key, value) -> log.info("MessageStore Element with Hash {} and {}", key, value));
    }

    public void recomputeHashes() throws NoSuchAlgorithmException {
        List<Message> messages = this.messageStore.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("0000")).map(Map.Entry::getValue).collect(Collectors.toList());

        for(Message message: messages){
            long nonce = 1000000;
            String hash = message.getStamp();
            while(!hash.startsWith("0000")){
                nonce++;
                hash = Hasher.messageSha256ToBase64(message.withNonce(Long.toString(nonce)));
            }
            log.info("Recomputed hash for message: {} to {} with new nonce: {}",message.getStamp(), hash, nonce);
            this.messageStore.remove(message.getStamp());
            this.messageStore.put(hash, message.withNonce(Long.toString(nonce)).withStamp(hash));
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
        String hash = Hasher.messageSha256ToBase64(message);
        if(hash.equals(message.getStamp())){
            messageStore.put(hash, message);
        }
    }

    private void handleEndOfHeader(String input) throws ApplicationException {
        if (input == null || !input.isBlank()) {
            throw new ApplicationException("Missing empty line after header section!");
        }
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
