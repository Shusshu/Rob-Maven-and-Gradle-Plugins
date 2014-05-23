package be.billington.rob.bitbucket;

import be.billington.rob.Commit;
import be.billington.rob.RobLogManager;
import org.apache.maven.plugin.logging.Log;
import retrofit.RestAdapter;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RobLogBitbucketManager extends RobLogManager {

    private final String key;
    private final String secret;
    private final String owner;
    private final String repository;
    private final String branch;

    private BitbucketResponse resp;

    public RobLogBitbucketManager(Map<String, List<String>> commitListMap, Log log, String key, String secret, String owner, String repository, String branch, LocalDate startDate, LocalDate endDate) {
        super(commitListMap, log, startDate, endDate);
        this.key = key;
        this.secret = secret;
        this.owner = owner;
        this.repository = repository;
        this.branch = branch;
    }

    @Override
    protected List<? extends Commit> fetchFromApi(int page){
        //Process
        RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(key, secret);
        //oAuthConsumer.setTokenWithSecret(token, secret);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Bitbucket.URL)
                .setClient(new SigningOkClient(oAuthConsumer))
                .build();

        Bitbucket bitbucket = restAdapter.create(Bitbucket.class);

        if (page == 0) {
            resp = bitbucket.listCommits(owner, repository, branch);
        } else {
            resp = bitbucket.listCommits(owner, repository, branch, page);
        }

        getLog().info( "Neighborhood with " + resp.getPagelen() + " houses (Pages)." );

        return resp.getValues();
    }

    @Override
    protected boolean hasNextPage() {
        return resp.getNext() != null && !resp.getNext().isEmpty();
    }
}
