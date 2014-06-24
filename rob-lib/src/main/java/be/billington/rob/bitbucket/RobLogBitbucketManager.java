package be.billington.rob.bitbucket;

import be.billington.rob.Commit;
import be.billington.rob.ConfigSections;
import be.billington.rob.Configuration;
import be.billington.rob.RobLogManager;
import retrofit.RestAdapter;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

import java.util.Base64;
import java.util.List;

public class RobLogBitbucketManager extends RobLogManager {

    private final Configuration conf;

    private BitbucketResponse resp;

    public RobLogBitbucketManager(Configuration conf, ConfigSections config) {
        super(conf.getLogger(), config, conf.getFromDate(), conf.getToDate());
        this.conf = conf;
    }

    @Override
    protected List<? extends Commit> fetchFromApi(int page){

        if (conf.hasUsernamePassword()) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Bitbucket.URL).build();

            Bitbucket bitbucket = restAdapter.create(Bitbucket.class);
            String auth = "Basic " + Base64.getEncoder().encodeToString( (conf.getUsername() + ":" + conf.getPassword()).getBytes() );

            if (page == 0) {
                resp = bitbucket.listCommits(conf.getOwner(), conf.getRepo(), conf.getBranch(), auth);
            } else {
                resp = bitbucket.listCommits(conf.getOwner(), conf.getRepo(), conf.getBranch(), page, auth);
            }
            getLog().info( "Driving the car" );

        } else {
            RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(conf.getKey(), conf.getSecret());
            //oAuthConsumer.setTokenWithSecret(token, secret);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setClient(new SigningOkClient(oAuthConsumer))
                    .setEndpoint(Bitbucket.URL).build();

            Bitbucket bitbucket = restAdapter.create(Bitbucket.class);

            if (page == 0) {
                resp = bitbucket.listCommits(conf.getOwner(), conf.getRepo(), conf.getBranch());
            } else {
                resp = bitbucket.listCommits(conf.getOwner(), conf.getRepo(), conf.getBranch(), page);
            }
            getLog().info( "Driving a van" );
        }

        getLog().info( "Neighborhood with " + resp.getPagelen() + " houses (Pages)." );

        return resp.getValues();
    }

    @Override
    protected boolean hasNextPage() {
        return resp.getNext() != null && !resp.getNext().isEmpty();
    }
}
