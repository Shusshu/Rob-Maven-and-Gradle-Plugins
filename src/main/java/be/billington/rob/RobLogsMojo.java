package be.billington.rob;


import be.billington.rob.bitbucket.Bitbucket;
import be.billington.rob.bitbucket.BitbucketResponse;
import be.billington.rob.bitbucket.Commit;
import okio.Buffer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Rob logger
 *
 */
@Mojo( name = "logs", requiresProject = false)
public class RobLogsMojo extends AbstractMojo
{

    @Parameter(property = "rob.repo", required = true)
    private String repository;

    @Parameter(property = "rob.prefix", required = true)
    private String prefix;

    @Parameter(property = "rob.rules")
    private String rulesFile;

    @Parameter(property = "rob.from.date")
    private String startDateStr;

    @Parameter(property = "rob.to.date")
    private String endDateStr;

    @Parameter(property = "rob.owner", defaultValue = "afrogleap")
    private String owner;

    @Parameter(property = "rob.branch", defaultValue = "development")
    private String branch;

    @Parameter(property = "rob.file", defaultValue = "./changelog.txt")
    private String filePath;

    @Parameter(property = "rob.key", required = true)
    private String key;

    @Parameter(property = "rob.secret", required = true)
    private String secret;

    @Parameter(property = "rob.failOnError", defaultValue = "true")
    private boolean failOnError = false;

    @Parameter(readonly = true, defaultValue = "${project.build.directory}")
    protected File targetDirectory;

    /**
     * @since 1.1.0
     */
    @Component
    private SecDispatcher securityDispatcher;

    private Map<String, List<String>> commitListMap = new LinkedHashMap<>();

    private LocalDate startDate, endDate;

    public void execute() throws MojoExecutionException
    {
        getLog().info( "Robbing..." );

        if (!initDateParams()) {
            getLog().info( "Couldn't rob anything." );
            return ;
        }

        try {
            //Init
            ConfigSections configSections = ConfigSections.createConfigSections(rulesFile, this.prefix);

            configSections.initMap(commitListMap);

            //Process
            RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(key, secret);
            //oAuthConsumer.setTokenWithSecret(token, secret);

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Bitbucket.URL)
                    .setClient(new SigningOkClient(oAuthConsumer))
                    .build();

            Bitbucket bitbucket = restAdapter.create(Bitbucket.class);

            BitbucketResponse resp = bitbucket.listCommits( owner, repository, branch );

            getLog().info( "Neighborhood with " + resp.getPagelen() + " houses (Pages)." );

            LocalDate commitDate;
            boolean readNextPage = true;

            do {
                getLog().info("Walking around house number: " + resp.getPage() + " (Page)");

                for (Commit commit : resp.getValues()) {

                    commitDate = LocalDate.parse(commit.getDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    if (commitDate.isAfter(this.startDate) &&
                            (commitDate.isBefore(this.endDate) || commitDate.isEqual(this.endDate)) ) {

                        if (commit.getMessage().charAt(0) == '!') {
                            getLog().info("Electrified fences on this side, skipping it (!)");
                            continue;
                        }

                        String[] commitMsgList = commit.getMessage().split("\n");

                        configSections.getSections().forEach( (section) -> {
                            if (commit.getMessage().toLowerCase().contains(section.getMatch().toLowerCase())) {
                                commitListMap.get(section.getTitle()).add(commitMsgList[0]);
                            }
                        });

                        for (Section exclusiveSection : configSections.getExclusiveSections()) {
                            if (configSections.hasMatchInSections(commit.getMessage())) {
                                continue;
                            }
                            if (exclusiveSection.excludeCommit(commit.getMessage())) {
                                continue;
                            }
                            commitListMap.get(exclusiveSection.getTitle()).add(commitMsgList[0]);
                        }

                    } else if (commitDate.isBefore(this.endDate) ) {
                        readNextPage = false;
                        getLog().info("Shit, cops on the lookout.");
                        break;
                    }
                }
                if (readNextPage && resp.getNext() != null && !resp.getNext().isEmpty()) {
                    getLog().info("A few more houses to go.");
                    resp = bitbucket.listCommits(owner, repository, branch, resp.getPage() + 1);

                } else {
                    getLog().info("Last one for today.");
                    readNextPage = false;
                }

            } while (readNextPage);

            getLog().info("Time is up. It is no longer safe to rob houses.");

            generateFile();

        } catch (RetrofitError e) {
            getLog().error( "Network Error: " + e.getMessage() + " - " + e.getResponse().getStatus(), e);

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
            getLog().error("Start date must be before end date");
            return false;
        }
        return true;
    }

    private void generateFile() {
        getLog().info("Counting how much stuff I have robbed today. (Generate file)");

        Buffer buffer = new Buffer();
        buffer.writeUtf8("Changelog from " + this.startDate + " until " + this.endDate + "\n");


        commitListMap.forEach( (title, commits) -> writeToFile(buffer, title, commits) );

        try {
            File file;
            if (targetDirectory != null && targetDirectory.exists()){
                file = new File(targetDirectory, filePath );
            } else {
                file = new File( filePath );
            }

            file.createNewFile();
            buffer.writeTo(new FileOutputStream( file ));

            getLog().info("Report: " + file.getPath());

        } catch (IOException ex) {
            getLog().error( "Could not create file: " + ex.getMessage(), ex);
        }
    }

    private void writeToFile(Buffer buffer, String title, List<String> content) {
        if (content.size() > 0) {
            buffer.writeUtf8(title);
            for (String msg : content){
                buffer.writeUtf8("- " + msg + "\n");
            }
        }
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