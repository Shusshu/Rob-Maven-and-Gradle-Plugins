package be.billington.rob.bitbucket;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Bitbucket {

    String URL = "https://bitbucket.org";

    @GET("/api/2.0/repositories/{owner}/{repo}/commits/{branch}")
    BitbucketResponse listCommits( @Path("owner") String owner, @Path("repo") String repo, @Path("branch") String branch);

    @GET("/api/2.0/repositories/{owner}/{repo}/commits/{branch}")
    BitbucketResponse listCommits( @Path("owner") String owner, @Path("repo") String repo, @Path("branch") String branch, @Query("page") int page);


    @GET("/api/2.0/repositories/{owner}/{repo}/commits/{branch}")
    BitbucketResponse listCommits( @Path("owner") String owner, @Path("repo") String repo, @Path("branch") String branch, @Header("Authorization") String auth);

    @GET("/api/2.0/repositories/{owner}/{repo}/commits/{branch}")
    BitbucketResponse listCommits( @Path("owner") String owner, @Path("repo") String repo, @Path("branch") String branch, @Query("page") int page, @Header("Authorization") String auth);
}
