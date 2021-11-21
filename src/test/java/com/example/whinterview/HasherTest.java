package com.example.whinterview;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

public class HasherTest {

    Parser parser = new Parser();

    static Stream<Arguments> arguments(){
        return Stream.of(
                Arguments.of(
                        Message.builder()
                                .body("""
                                        Hello Alice.
                                        I found some exciting new thing called bitcoin. Have you heard about it?
                                        Your friend,
                                        Bob
                                        """)
                                .from("bob@example.com")
                                .to("alice@example.com")
                                .nonce("24830964")
                                .stamp("0000JJTueU2utLQnAA0NIZ3NQZwGaskSQFkviXQyJsU=")
                                .build()),
                Arguments.of(
                        Message.builder()
                                .body("""
                                        Hello Bob,
                                        > I found some exciting new thing called bitcoin. Have you heard about it?

                                        I read something about it, sounds like a scam to me.
                                        Yours, Alice
                                        """)
                                .from("alice@example.com")
                                .to("bob@example.com")
                                .nonce("38385307")
                                .stamp("0000f/fd+9Omj/DAQsaKtsmGLLnZYZIJVdw8ie5XEqo=")
                                .build()),
                Arguments.of(
                        Message.builder()
                                .body("""
                                        Hello Alice.
                                        DO YOU WANT TO MAKE MONEY FAST: CLICK HERE!

                                        XOXO SPAMBOT
                                        """)
                                .from("SPAMBOT@example.com")
                                .to("alice@example.com")
                                .nonce("0000")
                                .stamp("0000E6UQCwLezm6KX4NCYtpeSGMvXa2Bjo6/enwk5I=")
                                .build()),
                Arguments.of(
                        Message.builder()
                                .body("""
                                        Hello Alice.
                                        > I found some exciting new thing called bitcoin. Have you heard about it?
                                        >> I read something about it, sounds like a scam to me.

                                        but... all the fancy crypto! I must be good!
                                        Bob
                                        """)
                                .from("bob@example.com")
                                .to("alice@example.com")
                                .nonce("96477")
                                .stamp("nzRhw63S0vO3QuyGEyuEYupgNS4pj4PP5wucpjYnHOI=")
                                .build()),
                Arguments.of(
                        Message.builder()
                                .body("""
                                        Hello Bob,
                                        > but... all the fancy crypto! I must be good!

                                        Don't be fooled, Bob. You will loose all your money if you dont get out soon enough.
                                        Yours, Alice
                                        """)
                                .from("alice@example.com")
                                .to("bob@example.com")
                                .nonce("44774")
                                .stamp("uCmo9nd5RLUiELC/hBC+MX8M1zTuCxBwtUvy09MqYIg=")
                                .build())
        );
    }


    @ParameterizedTest
    @MethodSource("arguments")
    void testHashAuthenticity(Message message) throws NoSuchAlgorithmException {
        Assertions.assertThat(message.getStamp()).isEqualTo(parser.messageSha256ToBase64(message));
    }

}
