package com.example.whinterview;


import lombok.*;


@Value
@With
@Builder
public class Message {

    String from;
    String to;
    String stamp;
    String nonce;
    String body;

    public boolean isValid(){
        return from != null && to != null && stamp != null && nonce != null && body != null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", stamp='" + stamp + '\'' +
                ", nonce=" + nonce +
                ", body='" + body + '\'' +
                '}';
    }

}
