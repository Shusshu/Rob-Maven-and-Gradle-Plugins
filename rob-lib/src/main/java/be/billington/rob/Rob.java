package be.billington.rob;

import be.billington.rob.bitbucket.RobLogBitbucketManager;
import be.billington.rob.github.RobLogGithubManager;
import org.slf4j.Logger;
import retrofit.RetrofitError;

import java.io.File;
import java.io.IOException;

public class Rob {
    public static final String API_BITBUCKET = "bitbucket";

    //TODO improve API with credentials

    public static void logs(Logger logger, String api, String owner, String repository, String prefix, String branch, String rulesFile, String filePath, String fromDate, String toDate, Credentials credentials) {
        logs(logger, api, owner, repository, prefix, branch, rulesFile, filePath, fromDate, toDate, credentials, null);
    }

    public static void logs(Logger logger, String api, String owner, String repository, String prefix, String branch, String rulesFile, String filePath, String fromDate, String toDate, Credentials credentials, File targetDirectory) {
        try {
            ConfigSections config = ConfigSections.createConfigSections(rulesFile, prefix, api);

            RobLogManager manager;
            if (api.toLowerCase().equals(API_BITBUCKET)) {
                manager = new RobLogBitbucketManager(logger, config, owner, repository, branch, fromDate, toDate, credentials);
            } else {
                manager = new RobLogGithubManager(logger, config, owner, repository, fromDate, toDate, credentials);
            }

            manager.fetchAndProcessCommitMessages();

            manager.generateFile(targetDirectory, filePath);

        } catch (RetrofitError e) {

            logger.error("Network Error: " + e.getMessage() + " - " + e.getResponse().getStatus() + " - URL: " + e.getUrl(), e);

            if (e.getResponse() != null){
                switch (e.getResponse().getStatus()) {
                    case 401: logger.error("Unauthorized - Check your credentials"); break;
                    default: break;
                }
            }

        } catch (IOException ioex) {
            logger.error("File Error: " + ioex.getMessage(), ioex);
        }
    }
}
