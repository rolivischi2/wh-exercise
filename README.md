x
x
# Assessment Java 102

Welcome to the willhaben Java assessment. To solve the problems ahead you can use
any frameworks, libraries, etc. you want, as long as your code is written in Java.

You can use any external (online) resources as long as you can explain your solution
(e.g. [stackoverflow](https://stackoverflow.com/) is allowed).

The assessment consists of several parts, each depending on the previous one, so you
have to solve them in order.

Once you have finished the last part, please send your the source code
to your instructor.

## Requirements

* use one of the following build tools: `maven` or `gradle`
* your project should have the standard source code layout and work with the standard build command of your build tool
* adhere to the same coding quality standards as in a real production project
* design meaningful interfaces for your classes

## Part 1 - parsing

Write a program, that given a file name, reads that file line by line and finds all
messages in that file that have the following format:

A Message consists of four header fields (From, To, Stamp, Nonce), in that order,
followed by a colon `:`, a blank (space) character, the header value and a newline character.

After the headers, an empty line and the message body follows.

The message ends with a single `.` character followed by a newline.

```
From: <email-address><newline>
To: <email-address><newline>
Stamp: <0-9A-Za-z+/=><newline>
Nonce: <0-9><newline>
<newline>
<message body (any text, maximum 6 lines, including newline character '\n')>
.<newline>
```

This is a valid message:
```
From: bob@example.com
To: alice@example.com
Stamp: 123+4/A=
Nonce: 3432

Hello Alice.
This is a test message with lines in the message body.
Your friend,
Bob
.
```

The message body as Java string looks like this:
`Hello Alice.\nThis is a test message with lines in the message body.\nYour friend,\nBob\n`

This is a invalid message:
```
From: xxxxx #invalid email
To: alice@example.com
Hello Alice. # no newline between header and text, missing stamp
This is a test message with lines in the message body.
Your friend,
Bob
.
```

## Part 2

For each message, compute the following:

`Base64 ( SHA256-Hash ( from-email + nonce + to-email + nonce + message body ))

Use UTF-8 encoding for string to byte conversion. The message body does include newlines (siggle `\n` character), but not the 
message terminating line only containing the dot `.\n`.

Discard each message where the computed hash does not match the hash in the `Stamp` header. 

Use this file as an input to your programm. What are the hashes of valid messages?

## Part 3 (bonus)

For each valid message, where the `Stamp` header does NOT start with the string `0000`,
find a nonce that, when recomputing the hash, does.


### Data


From: bob@example.com
To: alice@example.com
Stamp: 0000JJTueU2utLQnAA0NIZ3NQZwGaskSQFkviXQyJsU=
Nonce: 24830964

Hello Alice.
I found some exciting new thing called bitcoin. Have you heard about it?
Your friend,
Bob
.

----

From: alice@example.com
To: bob@example.com
Stamp: 0000f/fd+9Omj/DAQsaKtsmGLLnZYZIJVdw8ie5XEqo=
Nonce: 38385307

Hello Bob,
> I found some exciting new thing called bitcoin. Have you heard about it?

I read something about it, sounds like a scam to me.
Yours, Alice
.

----


From: SPAMBOT@example.com
To: alice@example.com
Stamp: 0000E6UQCwLezm6KX4NCYtpeSGMvXa2Bjo6/enwk5I=
Nonce: 0000

Hello Alice.
DO YOU WANT TO MAKE MONEY FAST: CLICK HERE!

XOXO SPAMBOT
.

----

From: bob@example.com
To: alice@example.com
Stamp: nzRhw63S0vO3QuyGEyuEYupgNS4pj4PP5wucpjYnHOI=
Nonce: 96477

Hello Alice.
> I found some exciting new thing called bitcoin. Have you heard about it?
>> I read something about it, sounds like a scam to me.

but... all the fancy crypto! I must be good!
Bob
.

----

From: alice@example.com
To: bob@example.com
Stamp: uCmo9nd5RLUiELC/hBC+MX8M1zTuCxBwtUvy09MqYIg=
Nonce: 44774

Hello Bob,
> but... all the fancy crypto! I must be good!

Don't be fooled, Bob. You will loose all your money if you dont get out soon enough.
Yours, Alice
.
