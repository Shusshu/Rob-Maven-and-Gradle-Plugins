package be.billington.rob;


import be.billington.rob.bitbucket.RobLogBitbucketManager;
import be.billington.rob.github.RobLogGithubManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.impl.StaticLoggerBinder;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import retrofit.RetrofitError;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Rob logger
 *
 */
@Mojo( name = "logs", requiresProject = false)
public class RobLogsMojo extends AbstractMojo
{

    @Parameter(property = "rob.repo", required = true)
    private String repository;

    @Parameter(property = "rob.api", required = true, defaultValue = "bitbucket")
    private String api;

    @Parameter(property = "rob.owner", required = true, defaultValue = "afrogleap")
    private String owner;

    @Parameter(property = "rob.prefix")
    private String prefix;

    @Parameter(property = "rob.rules")
    private String rulesFile;

    @Parameter(property = "rob.from.date")
    private String startDateStr;

    @Parameter(property = "rob.to.date")
    private String endDateStr;

    @Parameter(property = "rob.branch", defaultValue = "development")
    private String branch;

    @Parameter(property = "rob.file", defaultValue = "./changelog.txt")
    private String filePath;

    @Parameter(property = "rob.key")
    private String key;

    @Parameter(property = "rob.secret")
    private String secret;

    @Parameter(property = "rob.github.token")
    private String token;

    @Parameter(property = "rob.failOnError", defaultValue = "true")
    private boolean failOnError = false;

    @Parameter(readonly = true, defaultValue = "${project.build.directory}")
    protected File targetDirectory;

    /**
     * @since 1.1.0
     */
    @Component
    private SecDispatcher securityDispatcher;

    private LocalDate startDate, endDate;

    public void execute() throws MojoExecutionException
    {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());

        getLog().info( "Robbing..." );

        if (!initDateParams()) {
            getLog().info( "Couldn't rob anything." );
            return ;
        }

        try {
            ConfigSections config = ConfigSections.createConfigSections(rulesFile, this.prefix);

            RobLogManager manager;
            if (api.equals("bitbucket")) {
                manager = new RobLogBitbucketManager(StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(""), config, key, secret, owner, repository, branch, startDate, endDate);
            } else {
                manager = new RobLogGithubManager(StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(""), config, token, owner, repository, startDate, endDate);
            }

            manager.fetchAndProcessCommitMessages();

            manager.generateFile(targetDirectory, filePath);

        } catch (RetrofitError e) {

            getLog().error( "Network Error: " + e.getMessage() + " - " + e.getResponse().getStatus() + " - URL: " + e.getUrl(), e);

        } catch (IOException ioex) {
            getLog().error( "File Error: " + ioex.getMessage(), ioex);
        }

        getLog().info( "Robbed." );
    }

    private boolean initDateParams() {
        if (endDateStr != null && endDateStr.length() > 0) {
            endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);

        } else {
            endDate = LocalDate.now();
        }

        if (startDateStr != null && startDateStr.length() > 0) {
            startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            startDate = endDate.minusDays(14l);
        }

        if (startDate.isAfter(endDate)){
            getLog().error("'From date' must be before 'to date'");
            return false;
        }
        return true;
    }

    protected String decrypt(String encoded) throws MojoExecutionException {
        try {
            return securityDispatcher.decrypt( encoded );

        } catch ( SecDispatcherException e ) {
            getLog().error( "error using security dispatcher: " + e.getMessage(), e );
            throw new MojoExecutionException( "error using security dispatcher: " + e.getMessage(), e );
        }
    }
}