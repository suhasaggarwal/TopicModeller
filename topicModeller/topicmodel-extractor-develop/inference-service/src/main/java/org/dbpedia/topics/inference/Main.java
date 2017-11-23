package org.dbpedia.topics.inference;

import org.apache.commons.cli.ParseException;
import org.dbpedia.topics.inference.service.CORSResponseFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

/**
 * Main class.
 *
 */
public class Main {

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        CmdLineOpts opts = new CmdLineOpts();

        try {
            opts.parse(args);
        } catch (ParseException e) {
            e.printStackTrace();
            opts.printHelp();
            return;
        }

        if (opts.isHelp()) {
            opts.printHelp();
            return;
        }

        String topicModelFile = opts.getOptionValue(CmdLineOpts.MODEL_FILE);
        String filename = Paths.get(topicModelFile).getFileName().toString();
        filename = filename.split("\\.")[0].split("-", 2)[1];
        String[] features = filename.split("-");


        try {
            Inferencer.getInferencer(features).loadFile(topicModelFile);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String typesFile = opts.hasOption(CmdLineOpts.TYPES_FILE) ?
                opts.getOptionValue(CmdLineOpts.TYPES_FILE) : Config.TYPES_TRIPLE_FILE;
        String categoriesFile = opts.hasOption(CmdLineOpts.CATEGORIES_FILE) ?
                opts.getOptionValue(CmdLineOpts.CATEGORIES_FILE) : Config.CATEGORIES_TRIPLE_FILE;
        String hypernymsFile = opts.hasOption(CmdLineOpts.HYPERNYMS_FILE) ?
                opts.getOptionValue(CmdLineOpts.HYPERNYMS_FILE) : Config.HYPERNYMS_TRIPLE_FILE;
        if (opts.hasOption(CmdLineOpts.IN_MEMORY)) {
            Inferencer.getInferencer(features).createInMemoryTasks(typesFile, categoriesFile, hypernymsFile);
        }
        else {
            throw new IllegalArgumentException("Not yet implemented, currently only in-memory lookup possible.");
        }

        final HttpServer server = createServer();

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.shutdown();
            }
        }, "shutdownHook"));

        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", Config.SERVER_BASE_URI));
        server.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer createServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("org.dbpedia.topics.inference.service");
        rc.register(CORSResponseFilter.class);
        rc.register(MoxyJsonFeature.class).register(new MoxyJsonConfig().resolver());

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(Config.SERVER_BASE_URI), rc);

        return server;
    }
}

