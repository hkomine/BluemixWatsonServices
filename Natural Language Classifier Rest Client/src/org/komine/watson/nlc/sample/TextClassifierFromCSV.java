package org.komine.watson.nlc.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.entity.StringEntity;
import org.komine.watson.nlc.sample.models.NLClassifiedClass;
import org.komine.watson.nlc.sample.models.NLClassifierInputPayload;
import org.komine.watson.nlc.sample.models.NLClassifierOutputPayload;
import org.komine.watson.nlc.sample.rest.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TextClassifierFromCSV extends AbstractNLCRestClient {
    private static final String BASE_URL = "https://gateway.watsonplatform.net/natural-language-classifier/api";
    private static final String API_PATH = "/v1/classifiers";
    private static final String METHOD = "POST";

    private static final String USERNAME_OPTION = "u", USERNAME_OPTION_LONG = "username";
    private static final String PASSWORD_OPTION = "p", PASSWORD_OPTION_LONG = "password";
    private static final String CLASSIFIERIF_OPTION = "d", CLASSIFIERIF_OPTION_LONG = "id";
    private static final String INPUTFILE_OPTION = "i", INPUTFILE_OPTION_LONG = "in";
    private static final String OUTPUTFILEF_OPTION = "o", OUTPUTFILE_OPTION_LONG = "out";

    public static void main(String args[]) throws IOException {
        Option usernameOption = createOption(USERNAME_OPTION, USERNAME_OPTION_LONG, true,
                "The username for NLC service", true, USERNAME_OPTION_LONG);
        Option passwordOption = createOption(PASSWORD_OPTION, PASSWORD_OPTION_LONG, true,
                "The password for NLC service", true, PASSWORD_OPTION_LONG);
        Option classifierIdOption = createOption(CLASSIFIERIF_OPTION, CLASSIFIERIF_OPTION_LONG, true,
                "The Classifier ID available in NLC service", true, CLASSIFIERIF_OPTION_LONG);
        Option inputFilenameOption = createOption(INPUTFILE_OPTION, INPUTFILE_OPTION_LONG, true, "CSV input file name",
                true, INPUTFILE_OPTION_LONG);
        Option outputFilenameOption = createOption(OUTPUTFILEF_OPTION, OUTPUTFILE_OPTION_LONG, true, "Output file name",
                true, OUTPUTFILE_OPTION_LONG);

        final Options options = buildOptions(usernameOption, passwordOption, classifierIdOption, inputFilenameOption,
                outputFilenameOption);

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
        final String inputFilename = cmd.getOptionValue(INPUTFILE_OPTION);
        final String outputFilename = cmd.getOptionValue(OUTPUTFILEF_OPTION);

        final String url = BASE_URL + API_PATH + "/" + classifierId + "/classify";

        // input file
        FileInputStream input = new FileInputStream(new File(inputFilename));
        InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
        CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
        List<CSVRecord> csvDataList = parser.getRecords();

        // output file
        FileOutputStream output = new FileOutputStream(new File(outputFilename));
        OutputStreamWriter writer = new OutputStreamWriter(output, Charset.forName("UTF-8"));
        BufferedWriter bw = new BufferedWriter(writer);
        CSVPrinter printer = CSVFormat.EXCEL.print(bw);

        for (int i = 0; i < csvDataList.size(); i++) {
            CSVRecord csvRecord = csvDataList.get(i);

            if (csvRecord.size() > 0) {
                String text = csvRecord.get(0);
                String intent = csvRecord.get(1);
                System.out.println(text + ", " + intent);

                LinkedList<String> list = new LinkedList<String>();
                list.add(text);
                list.add(intent);

                NLClassifierOutputPayload ret = getClasses(text, url, username, password);

                if (null != ret) {
                    NLClassifiedClass[] classes = ret.classes;
                    if (null != classes) {
                        for (int j = 0; j < classes.length; j++) {
                            list.add(classes[j].class_name);
                            list.add(String.valueOf(classes[j].confidence));
                        }
                    } else {
                        list.add("NO=CLASS");
                        list.add(String.valueOf(ret.code));
                        list.add(ret.error);
                        list.add(ret.description);
                    }
                } else {
                    list.add("NULL-RESPONSE");
                }
                printer.printRecord(list);
            }
        }

        parser.close();
        printer.flush();
        printer.close();
    }

    private static NLClassifierOutputPayload getClasses(String text, String url, String username, String password) {
        NLClassifierOutputPayload ret = null;

        try {
            NLClassifierInputPayload payload = new NLClassifierInputPayload(text);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            StringEntity entity = new StringEntity(json, "UTF-8");
            entity.setContentType("application/json; charset=UTF-8");
            String result = RestClient.makeRestRequest(METHOD, url, username, password, null, entity);
            System.out.println("result: " + result);

            ret = mapper.readValue(result, NLClassifierOutputPayload.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
