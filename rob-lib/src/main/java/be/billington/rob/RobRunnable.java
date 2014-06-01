package be.billington.rob;

import be.billington.rob.bitbucket.BitbucketCredentials;
import be.billington.rob.github.GithubCredentials;
import org.slf4j.Logger;

import java.util.Map;

public class RobRunnable implements Runnable {

    public static final String CONFIG_KEY = "key";
    public static final String CONFIG_SECRET = "secret";
    public static final String CONFIG_TOKEN = "token";
    private final Logger logger;
    private final String api, owner, repo, prefix, branch, filePath, fromDate, toDate;
    private final Map<String, String> config;

    public RobRunnable(Logger logger, String api, String owner, String repo, String prefix, String branch, String filePath, String fromDate, String toDate, Map<String, String> config){
        this.logger = logger;
        this.api = api;
        this.owner = owner;
        this.repo = repo;
        this.prefix = prefix;
        this.branch = branch;
        this.config = config;
        this.filePath = filePath;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public void run() {
        robLogs();
    }

    public void robLogs(){
        logger.info("Robbing...");

        try {
            Credentials credentials;
            if (api.equalsIgnoreCase(Rob.API_BITBUCKET)){
                credentials = new BitbucketCredentials(config.get(CONFIG_KEY), config.get(CONFIG_SECRET));
            } else {
                credentials = new GithubCredentials(config.get(CONFIG_TOKEN));
            }
            Rob.logs(logger, api, owner, repo, prefix, branch, "", filePath, fromDate, toDate, credentials);

        } catch (Exception ex) {
            logger.error("Error: " + ex.getMessage(), ex);
        }

        logger.info("Robbed.");
    }
}
