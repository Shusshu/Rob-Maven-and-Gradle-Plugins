package be.billington.rob.github;

import be.billington.rob.Commit;
import be.billington.rob.ConfigSections;
import be.billington.rob.RobLogManager;
import org.slf4j.Logger;
import retrofit.RestAdapter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RobLogGithubManager extends RobLogManager {

    private final String token;
    private final String owner;
    private final String repository;

    public RobLogGithubManager(Logger log, ConfigSections config, String owner, String repository, String fromDate, String toDate, String token) {
        super(log, config, fromDate, toDate);
        this.token = token;
        this.owner = owner;
        this.repository = repository;
    }

    @Override
    protected List<? extends Commit> fetchFromApi(int page){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Github.URL)
                .build();

        Github github = restAdapter.create(Github.class);
        List<GithubCommit> commits;

        String auth = "";

        if (token != null && !token.isEmpty()) {
            auth = "Bearer " + token;
        }

        if (page == 0) {
            commits = github.listCommits(owner, repository,
                    this.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    this.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE), auth);
        } else {
            commits = github.listCommits(owner, repository, page,
                    this.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    this.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE), auth);
        }

        return commits;
    }

    @Override
    protected boolean hasNextPage() {
        return true;
    }

}
