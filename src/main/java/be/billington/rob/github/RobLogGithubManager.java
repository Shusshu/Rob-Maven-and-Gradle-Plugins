package be.billington.rob.github;

import be.billington.rob.Commit;
import be.billington.rob.RobLogManager;
import org.apache.maven.plugin.logging.Log;
import retrofit.RestAdapter;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class RobLogGithubManager extends RobLogManager {

    private final String key;
    private final String secret;
    private final String owner;
    private final String repository;
    private final String branch;

    public RobLogGithubManager(Map<String, List<String>> commitListMap, Log log, String key, String secret, String owner, String repository, String branch, LocalDate startDate, LocalDate endDate) {
        super(commitListMap, log, startDate, endDate);
        this.key = key;
        this.secret = secret;
        this.owner = owner;
        this.repository = repository;
        this.branch = branch;
    }

    @Override
    protected List<? extends Commit> fetchFromApi(int page){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Github.URL)
                .build();

        Github github = restAdapter.create(Github.class);

        getLog().info( "Date: " + this.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        getLog().info( "Date: " + this.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

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
