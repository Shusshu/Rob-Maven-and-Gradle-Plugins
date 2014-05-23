package be.billington.rob.github;

import be.billington.rob.Commit;
import be.billington.rob.ConfigSections;
import be.billington.rob.RobLogManager;
import org.apache.maven.plugin.logging.Log;
import retrofit.RestAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RobLogGithubManager extends RobLogManager {

    private final String token;
    private final String owner;
    private final String repository;

    public RobLogGithubManager(Log log, ConfigSections config, String token, String owner, String repository, LocalDate startDate, LocalDate endDate) {
        super(log, config, startDate, endDate);
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

        List<GithubCommit> commits = github.listCommits(owner, repository,
                this.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                this.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

        return commits;
    }

    @Override
    protected boolean hasNextPage() {
        return false;
    }

}
