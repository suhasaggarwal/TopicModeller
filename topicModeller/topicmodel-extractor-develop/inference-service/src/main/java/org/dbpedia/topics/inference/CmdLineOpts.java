package org.dbpedia.topics.inference;

import org.apache.commons.cli.*;

/**
 * Created by wlu on 26.05.16.
 */
class CmdLineOpts {
    public static final Option MODEL_FILE = Option.builder("f").longOpt("model-file")
            .desc("File with saved topic model mined by the topic modeller.").required(true).hasArg().argName("model file").build();

    public static final Option IN_MEMORY = Option.builder("m").longOpt("in-memory")
            .desc("read the triple files specified in props.properties into memory to provide much faster annotation. be sure to provide sufficient heap size with -Xmx64g")
            .build();

    public static final Option HYPERNYMS_FILE = Option.builder("hyp").longOpt("hypernyms")
            .desc("Triple file with the linked hypernyms dataset (for in memory querying).").hasArg().argName("hypernyms file").build();
    public static final Option TYPES_FILE = Option.builder("typ").longOpt("types")
            .desc("File with rdf:types triples (instance_types.ttl, for in memory querying).").hasArg().argName("types file").build();

    public static final Option CATEGORIES_FILE = Option.builder("cat").longOpt("categories")
            .desc("File with dcterm:subject triples (article_categories.ttl, for in memory querying).").hasArg().argName("categories file").build();

    public static final Option HELP = Option.builder("h").longOpt("help").desc("Shows this message.").build();

    private Options options = new Options();
    private CommandLine cmd;
    private String cmdName = "inference-service";

    public CmdLineOpts() {
        Option[] optsArr = new Option[]{HELP, MODEL_FILE, IN_MEMORY, HYPERNYMS_FILE, TYPES_FILE, CATEGORIES_FILE};
        for (Option option : optsArr) {
            this.options.addOption(option);
        }
    }

    public void parse(String[] args) throws ParseException {
        cmd = new DefaultParser().parse(options, args);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmdName, options, true);
    }

    public boolean hasOption(Option opt) {
        return cmd.hasOption(opt.getOpt());
    }


    public String getOptionValue(Option opt) {
        return cmd.getOptionValue(opt.getOpt());
    }

    public String getOptionValue(Option opt, String def) {
        return cmd.getOptionValue(opt.getOpt(), def);
    }

    public String[] getOptionValues(Option opt) {
        return cmd.getOptionValues(opt.getOpt());
    }

    public boolean isHelp() {
        return hasOption(HELP);
    }
}
