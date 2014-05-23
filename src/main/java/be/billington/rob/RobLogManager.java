package be.billington.rob;

import be.billington.rob.bitbucket.Bitbucket;
import be.billington.rob.bitbucket.BitbucketResponse;
import be.billington.rob.github.Github;
import be.billington.rob.github.GithubCommit;
import retrofit.RestAdapter;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

public abstract class RobLogManager {

    protected final LocalDate startDate;
    protected final LocalDate endDate;
    private Log log;
    private Map<String, List<String>> commitListMap;

    public RobLogManager(Map<String, List<String>> commitListMap, Log log, LocalDate startDate, LocalDate endDate) {
        this.commitListMap = commitListMap;
        this.log = log;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Log getLog(){
        return this.log;
    }

    protected abstract List<? extends Commit> fetchFromApi(int page);

    protected abstract boolean hasNextPage();

    public void fetchAndProcessCommitMessages(ConfigSections configSections) {
        int page = 0;
        List<? extends Commit> commits = fetchFromApi(page);

        LocalDate commitDate;
        boolean readNextPage = true;

        do {
            page++;
            getLog().info("Walking around house number: " + page + " (Page)");

            for (Commit commit : commits) {

                commitDate = LocalDate.parse(commit.getDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                if (commitDate.isAfter(this.startDate) &&
                        (commitDate.isBefore(this.endDate) || commitDate.isEqual(this.endDate)) ) {

                    if (commit.getMessage().charAt(0) == '!') {
                        getLog().info("Electrified fences on this side, skipping it (!)");
                        continue;
                    }

                    String[] commitMsgList = commit.getMessage().split("\n");

                    configSections.getSections().forEach( (section) -> {
                        if (section.excludeCommit(commit.getMessage())) {
                            return ;
                        }
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
            if (readNextPage && hasNextPage()) {
                getLog().info("A few more houses to go.");
                commits = fetchFromApi(page);

            } else {
                getLog().info("Last one for today.");
                readNextPage = false;
            }

        } while (readNextPage);

        getLog().info("Time is up. It is no longer safe to rob houses.");
    }

}
