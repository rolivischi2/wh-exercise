package com.example.whinterview;

public enum MessagePart {

    FROM("From:"),
    TO("To:"),
    STAMP("Stamp:"),
    NONCE("Nonce:");

    public final String value;

    MessagePart(String value){
        this.value = value;
    }

}
