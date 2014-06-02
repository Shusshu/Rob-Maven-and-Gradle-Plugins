package be.billington.rob;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.impl.StaticLoggerBinder;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import java.io.File;

/**
 * Rob logger
 *
 */
@Mojo( name = "logs", requiresProject = false)
public class RobLogsMojo extends AbstractMojo
{

    @Parameter(property = "rob.repo", required = true)
    private String repository;

    @Parameter(property = "rob.api", required = true, defaultValue = "Bitbucket")
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

    public void execute() throws MojoExecutionException
    {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());

        getLog().info( "Robbing..." );

        try {
            Configuration conf = new Configuration.ConfigurationBuilder(StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(""), api, repository, owner)
                    .branch(branch).prefix(prefix).filePath(filePath).fromDate(startDateStr).toDate(endDateStr)
                    .token(token).key(key).secret(secret).outputDir(targetDirectory).build();

            Rob.logs(conf);

        } catch (Exception e) {
            getLog().error( "Error: " + e.getMessage(), e);
        }

        getLog().info( "Robbed." );
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