package com.example.whinterview;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MyAppRunner implements ApplicationRunner {

    private final static Set<String> allowedOptions = Set.of("i");
    private final Parser parser;

    @Override
    public void run(ApplicationArguments args) throws ApplicationException, IOException, NoSuchAlgorithmException {
        this.handleArguments(args);
        String inputPath = args.getOptionValues("i").get(0);

        parser.parseFile(inputPath);
        parser.outputMessages();
        parser.recomputeHashes();
        parser.outputMessages();
    }


    private void handleArguments(ApplicationArguments args) throws ApplicationException {
        if(!args.containsOption("i")) {
            throw new ApplicationException("Missing input argument!");
        }
        if(!allowedOptions.containsAll(args.getOptionNames()) || !args.getNonOptionArgs().isEmpty()){
            Set<String> options = new HashSet<>(args.getOptionNames());
            options.addAll(args.getNonOptionArgs());
            options.removeAll(allowedOptions);
            throw new ApplicationException(String.format("Illegal arguments : {%s}",
                    String.join(" ,", options)));
        }
    }
}