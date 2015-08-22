package org.komine.watson.nlc.sample;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.entity.StringEntity;
import org.komine.watson.nlc.sample.models.NLClassifierInputPayload;
import org.komine.watson.nlc.sample.models.NLClassifierOutputPayload;
import org.komine.watson.nlc.sample.rest.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TextClassifier extends AbstractNLCRestClient {

    private static final String BASE_URL = "https://gateway.watsonplatform.net/natural-language-classifier/api";
    private static final String API_PATH = "/v1/classifiers";
    private static final String METHOD = "POST";

    private static final String USERNAME_OPTION = "u", USERNAME_OPTION_LONG = "username";
    private static final String PASSWORD_OPTION = "p", PASSWORD_OPTION_LONG = "password";
    private static final String CLASSIFIERIF_OPTION = "d", CLASSIFIERIF_OPTION_LONG = "id";
    private static final String INPUTTEXT_OPTION = "t", INPUTTEXT_OPTION_LONG = "text";

    public static void main(String args[]) {
        Option usernameOption = createOption(USERNAME_OPTION, USERNAME_OPTION_LONG, true,
                "The username for NLC service", true, USERNAME_OPTION_LONG);
        Option passwordOption = createOption(PASSWORD_OPTION, PASSWORD_OPTION_LONG, true,
                "The password for NLC service", true, PASSWORD_OPTION_LONG);
        Option classifierIdOption = createOption(CLASSIFIERIF_OPTION, CLASSIFIERIF_OPTION_LONG, true,
                "The Classifier ID available in NLC service", true, CLASSIFIERIF_OPTION_LONG);
        Option inputTextOption = createOption(INPUTTEXT_OPTION, INPUTTEXT_OPTION_LONG, true, "A text to be classified",
                true, INPUTTEXT_OPTION_LONG);

        final Options options = buildOptions(usernameOption, passwordOption, classifierIdOption, inputTextOption);

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
        final String classifierId = cmd.getOptionValue(CLASSIFIERIF_OPTION);
        final String inputText = cmd.getOptionValue(INPUTTEXT_OPTION);

        try {
            String url = BASE_URL + API_PATH + "/" + classifierId + "/classify";

            NLClassifierInputPayload input = new NLClassifierInputPayload(inputText);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(input);
            System.out.println(json);

            StringEntity entity = new StringEntity(json, "UTF-8");
            entity.setContentType("application/json; charset=UTF-8");
            String result = RestClient.makeRestRequest(METHOD, url, username, password, null, entity);
            System.out.println("Result: " + result);
            NLClassifierOutputPayload output = mapper.readValue(result, NLClassifierOutputPayload.class);

            System.out.println(output.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
