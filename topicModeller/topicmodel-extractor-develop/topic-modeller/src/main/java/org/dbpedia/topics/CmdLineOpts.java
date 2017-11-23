package org.dbpedia.topics;

import org.apache.commons.cli.*;

/**
 * Created by wlu on 26.05.16.
 */
class CmdLineOpts {
    public static final Option TOPIC_MODELLING = Option.builder("T").longOpt("topic-modelling")
            .desc("Starts the topic modelling. Requires specifying the reader and the finisher.").build();

    public static final Option MODELLING_ALGORITHM = Option.builder("a").longOpt("algorithm")
            .desc("Which algorithm should be used. Possible values are 'lda' and 'hlda'.").hasArg().argName("algorithm").build();

    public static final Option NUM_TOPICS = Option.builder("n").longOpt("num-topics").hasArgs()
            .desc("How many topics should be mined. Must be specified when using LDA.").build();

    public static final Option NUM_LEVELS = Option.builder("l").longOpt("num-levels").hasArgs()
            .desc("How many levels should the topic hierarchy have. Must be specified when using hLDA.").build();

    public static final Option FEATURES = Option.builder("f").longOpt("features")
            .desc("Which features should be used for topic modelling. Possible values are 'w' (words), 'e' (entities), 't' (types), 'c' (categories), 'h' (hypernyms).")
            .numberOfArgs(5).hasArgs().argName("features...").build();

    public static final Option PREPROCESSING_PIPELINE = Option.builder("P").longOpt("preprocessing")
            .desc("Starts the pipeline to prepare documents for topic modelling. Requires specifying the reader and the finisher.").build();

    public static final Option TASKS = Option.builder("t").longOpt("tasks")
            .desc("Which tasks should the pipeline perform. Possible values are 'lemma', 'annotate' [using Spotlight], 'types', 'categories', 'hypernyms'.")
            .numberOfArgs(5).hasArgs().argName("tasks...").build();

    public static final Option READER = Option.builder("R").longOpt("reader")
            .desc("Which data should the pipeline be run on? Possible values are 'abstracts' and 'wikidump'.").hasArgs().argName("reader").build();

    public static final Option FINISHER = Option.builder("F").longOpt("finisher")
            .desc("How should the pipeline finish. Possible values are 'mongo'.").hasArg().argName("finisher").build();

    public static final Option IN_MEMORY = Option.builder("m").longOpt("in-memory")
            .desc("read the triple files specified in props.properties into memory to provide much faster annotation. be sure to provide sufficient heap size with -Xmx64g")
            .build();

    public static final Option ENCODE_MINED_TOPICS = Option.builder("E").longOpt("encode")
            .desc("Encode mined topics as RDF.").build();

    public static final Option INPUT = Option.builder("i").longOpt("input")
            .desc("Specify the input file / directory.").hasArg().argName("input").build();

    public static final Option OUTPUT = Option.builder("o").longOpt("output")
            .desc("Specify the output file / directory.").hasArg().argName("output").build();

    public static final Option OUTPUT_FORMAT = Option.builder("of").longOpt("output-format")
            .desc("Specify the output format. For accepted jena formats see:" +
                    "https://jena.apache.org/documentation/io/rdf-output.html#jena_model_write_formats")
            .hasArg().argName("output format").build();

    public static final Option NUM_TOPIC_WORDS = Option.builder("w").longOpt("num-topic-words")
            .desc("Using how many words should a topic be described?")
            .hasArg().argName("number words").build();

    public static final Option DONT_STORE_TEXT = Option.builder("nt").longOpt("no-texts")
            .desc("Don't store texts after the preprocessing pipeline. Might make sense to use with long wikipedia" +
                    " articles that make MongoDB communication very slow.").build();

    public static final Option DUMP_MONGO = Option.builder("dm").longOpt("dump-mongo")
            .desc("Read the documents from mongo and store them on disc in chunks of specified size in the specified output dir.").build();

    public static final Option CHUNK_SIZE = Option.builder("cs").longOpt("chunk-size")
            .desc("Size of the chunk (when saving mongo dump).").hasArg().build();

    public static final Option IMPORT_ELASTIC = Option.builder("ie").longOpt("import-to-elastic")
            .desc("Reads the mongo dump and inserts the document to elastic search.").build();

    public static final Option HELP = Option.builder("h").longOpt("help").desc("Shows this message.").build();

    private Options options = new Options();
    private CommandLine cmd;
    private String cmdName = "topic-modeller";

    public CmdLineOpts() {
        Option[] optsArr = new Option[]{TOPIC_MODELLING, MODELLING_ALGORITHM, NUM_LEVELS, NUM_TOPICS, FEATURES,
                PREPROCESSING_PIPELINE, TASKS, READER, FINISHER, IN_MEMORY, ENCODE_MINED_TOPICS, INPUT,
                OUTPUT, OUTPUT_FORMAT, NUM_TOPIC_WORDS, DONT_STORE_TEXT, DUMP_MONGO, CHUNK_SIZE, IMPORT_ELASTIC,
                HELP};
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
