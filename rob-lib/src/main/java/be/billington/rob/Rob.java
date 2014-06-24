package be.billington.rob;

import be.billington.rob.bitbucket.RobLogBitbucketManager;
import be.billington.rob.github.RobLogGithubManager;
import org.slf4j.Logger;
import retrofit.RetrofitError;

import java.io.File;
import java.io.IOException;

public class Rob {
    public static final String API_BITBUCKET = "Bitbucket";
    public static final String API_GITHUB = "Github";

    public static void logs(Configuration conf) {
        try {

            ConfigSections config = ConfigSections.createConfigSections(conf.getConfigPath(), conf.getPrefix(), conf.getApi());

            RobLogManager manager;
            if (conf.getApi().equalsIgnoreCase(API_BITBUCKET)) {
                manager = new RobLogBitbucketManager(conf, config);
            } else {
                manager = new RobLogGithubManager(conf, config);
            }

            manager.fetchAndProcessCommitMessages();

            manager.generateFile(conf.getOutputDir(), conf.getFilePath());

        } catch (RetrofitError e) {

            conf.getLogger().error("Network Error: " + e.getMessage() + " - " + e.getResponse().getStatus() + " - URL: " + e.getUrl(), e);

            if (e.getResponse() != null){
                switch (e.getResponse().getStatus()) {
                    case 401: conf.getLogger().error("Unauthorized - Check your credentials"); break;
                    default: break;
                }
            }

        } catch (IOException ioex) {
            conf.getLogger().error("File Error: " + ioex.getMessage(), ioex);
        }
    }
}
