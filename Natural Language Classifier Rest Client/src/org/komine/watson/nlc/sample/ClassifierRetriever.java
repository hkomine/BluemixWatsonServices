package org.komine.watson.nlc.sample;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.komine.watson.nlc.sample.rest.RestClient;

public class ClassifierRetriever extends AbstractNLCRestClient {

    private static final String BASE_URL = "https://gateway.watsonplatform.net/natural-language-classifier/api";
    private static final String API_PATH = "/v1/classifiers";
    private static final String METHOD = "GET";

    private static final String USERNAME_OPTION = "u", USERNAME_OPTION_LONG = "username";
    private static final String PASSWORD_OPTION = "p", PASSWORD_OPTION_LONG = "password";

    public static void main(String args[]) {
        Option usernameOption = createOption(USERNAME_OPTION, USERNAME_OPTION_LONG, true,
                "The username for NLC service", true, USERNAME_OPTION_LONG);
        Option passwordOption = createOption(PASSWORD_OPTION, PASSWORD_OPTION_LONG, true,
                "The password for NLC service", true, PASSWORD_OPTION_LONG);

        final Options options = buildOptions(usernameOption, passwordOption);

        CommandLine cmd;
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(120, "java " + TextClassifierFromCSV.class.getName(), null, options, null);
            return;
        }

        final String username = cmd.getOptionValue(USERNAME_OPTION);
        final String password = cmd.getOptionValue(PASSWORD_OPTION);

        try {
            String url = BASE_URL + API_PATH;
            String result = RestClient.makeRestRequest(METHOD, url, username, password, null, null);
            System.out.println("Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
