package be.billington.rob.bitbucket;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Bitbucket {

    @GET("/api/2.0/repositories/afrogleap/{repo}/commits/{branch}")
    BitbucketResponse listCommits( @Path("repo") String repo, @Path("branch") String branch);

    @GET("/api/2.0/repositories/afrogleap/{repo}/commits/{branch}")
    BitbucketResponse listCommits( @Path("repo") String repo, @Path("branch") String branch, @Query("page") int page);

}
