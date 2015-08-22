package org.komine.watson.nlc.sample;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public abstract class AbstractNLCRestClient {
    public static Options buildOptions(Option option, Option... additionalOptions) {
        final Options options = new Options();
        options.addOption(option);
        for (Option o : additionalOptions) {
            options.addOption(o);
        }
        return options;
    }

    public static Option createOption(String opt, String longOpt, boolean hasArg, String description, boolean required,
            String argName) {
        Option option = new Option(opt, longOpt, hasArg, description);
        option.setRequired(required);
        option.setArgName(argName);
        return option;
    }
}
