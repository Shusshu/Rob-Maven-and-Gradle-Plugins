package be.billington.rob.github;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.List;

public interface Github {

    String URL = "https://api.github.com";

    @GET("/repos/{owner}/{repo}/commits")
    List<GithubCommit> listCommits(@Path("owner") String owner, @Path("repo") String repo);

    @GET("/repos/{owner}/{repo}/commits")
    List<GithubCommit> listCommits(@Path("owner") String owner, @Path("repo") String repo, @Query("since") String since, @Query("until") String until);

}
