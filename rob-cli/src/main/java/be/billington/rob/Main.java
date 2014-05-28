package be.billington.rob;

import be.billington.rob.bitbucket.BitbucketCredentials;
import be.billington.rob.github.GithubCredentials;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final String O_REPO = "r";
    private static final String O_OWNER = "o";
    private static final String O_BRANCH = "b";
    private static final String O_PREFIX = "p";
    private static final String O_API = "a";
    private static final String O_FROM_DATE = "fd";
    private static final String O_TO_DATE = "td";
    private static final String O_OUTPUT_FILE = "f";
    private static final String O_CONFIG_FILE = "c";
    private static final String O_BITBUCKET_KEY = "k";
    private static final String O_BITBUCKET_SECRET = "s";
    private static final String O_GITHUB_TOKEN = "t";

    private static String repo, owner, branch, api, prefix, fromDate, toDate, filePath, rulesFile, key, secret, token;

    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger("");
        logger.info("Robbing...");

        CommandLineParser parser = new BasicParser();

        try {
            CommandLine cmd = parser.parse(createParserOptions(), args);

            initParams(cmd);

            Credentials credentials;
            if (api.toLowerCase().equals(Rob.API_BITBUCKET)){
                credentials = new BitbucketCredentials(key, secret);
            } else {
                credentials = new GithubCredentials(token);
            }
            Rob.logs(logger, api, owner, repo, prefix, branch, rulesFile, filePath, fromDate, toDate, credentials);

        } catch (ParseException e) {
            logger.error("ParseException: " + e.getMessage(), e);
            usage(createParserOptions());

        } catch (Exception ex) {
            logger.error("Error: " + ex.getMessage(), ex);
        }

        logger.info("Robbed.");
    }

    private static void initParams(CommandLine cmd) {
        repo = cmd.getOptionValue(O_REPO);
        owner = cmd.getOptionValue(O_OWNER);

        if (cmd.hasOption(O_BRANCH)) {
            branch = cmd.getOptionValue(O_BRANCH);
        } else {
            branch = "development";
        }
        if (cmd.hasOption(O_PREFIX)) {
            prefix = cmd.getOptionValue(O_PREFIX);
        }
        if (cmd.hasOption(O_API)) {
            api = cmd.getOptionValue(O_API);
        } else {
            api = Rob.API_BITBUCKET;
        }
        if (cmd.hasOption(O_FROM_DATE)) {
            fromDate = cmd.getOptionValue(O_FROM_DATE);
        }
        if (cmd.hasOption(O_TO_DATE)) {
            toDate = cmd.getOptionValue(O_TO_DATE);
        }
        if (cmd.hasOption(O_OUTPUT_FILE)) {
            filePath = cmd.getOptionValue(O_OUTPUT_FILE);
        } else {
            filePath = "./changelog.txt";
        }
        if (cmd.hasOption(O_CONFIG_FILE)) {
            rulesFile = cmd.getOptionValue(O_CONFIG_FILE);
        }
        if (cmd.hasOption(O_BITBUCKET_KEY)) {
            key = cmd.getOptionValue(O_BITBUCKET_KEY);
        }
        if (cmd.hasOption(O_BITBUCKET_SECRET)) {
            secret = cmd.getOptionValue(O_BITBUCKET_SECRET);
        }
        if (cmd.hasOption(O_GITHUB_TOKEN)) {
            token = cmd.getOptionValue(O_GITHUB_TOKEN);
        }
    }

    private static Options createParserOptions() {
        Options options = new Options();

        Option repoOption = new Option(O_REPO, "repo", true, "Repository name");
        repoOption.setRequired(true);
        options.addOption(repoOption);

        Option ownerOption = new Option(O_OWNER, "owner", true, "Repository owner");
        ownerOption.setRequired(true);
        options.addOption(ownerOption);

        Option branchOption = new Option(O_BRANCH, "branch", true, "Repository branch");
        options.addOption(branchOption);

        Option prefixOption = new Option(O_PREFIX, "prefix", true, "Jira Prefix");
        options.addOption(prefixOption);

        Option apiOption = new Option(O_API, "api", true, "API");
        options.addOption(apiOption);

        Option fromDateOption = new Option(O_FROM_DATE, "from-date", true, "From date");
        options.addOption(fromDateOption);

        Option toDateOption = new Option(O_TO_DATE, "to-date", true, "To date");
        options.addOption(toDateOption);

        Option outputOption = new Option(O_OUTPUT_FILE, "output-file", true, "Output file");
        options.addOption(outputOption);

        Option rulesOption = new Option(O_CONFIG_FILE, "conf", true, "Config file");
        options.addOption(rulesOption);

        Option bitbucketKeyOption = new Option(O_BITBUCKET_KEY, "key", true, "bitbucket key");
        options.addOption(bitbucketKeyOption);

        Option bitbucketSecretOption = new Option(O_BITBUCKET_SECRET, "secret", true, "bitbucket secret");
        options.addOption(bitbucketSecretOption);

        Option githubToken = new Option(O_GITHUB_TOKEN, "token", true, "Github token");
        options.addOption(githubToken);

        return options;
    }

    private static void usage(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "Rob", options );
    }
}
