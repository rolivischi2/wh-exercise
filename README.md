
## Running the app
Since this is a Spring Boot project using Maven,
the regular set-up steps apply.

```bash
mvn clean compile
```

When running, a CLI option for the input file has to
be provided.

```bash
mvn spring-boot:run -Drun.arguments=--i=src/main/resources/input.txt
```


## Answer to question II
"What are the hashes of valid messages?"

Valid hashes encapsulate information about the sender
and recipient as well as the cryptographic nonce.

A valid hash has to have a valid nonce provided.
This nonce could be a unique nonce or a random one.
The attacker has no idea of knowing if a nonce has been used
or not.

The attacker also does not know the scheme of the hash.
i.e. They have no idea what info goes into the hashing
function and in which order.

This is based on the same idea as the Hashcash algorithm
used in Bitcoin mining and Email Spam aversion.

The idea is to shift the responsibility to the sender's
side by making the creation of desirable hashes
significantly more difficult than verifying valid hashes.

Recalculating the hash to fit a certain  format,
by means of different nonces
takes significant amount of time. This is how the
algorithm works as intended.

A spammer will not be computing a valid nonce for each message
since they intend to send thousands of emails per second.
