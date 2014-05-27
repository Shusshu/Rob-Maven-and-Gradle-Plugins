package be.billington.rob.github;

import be.billington.rob.Credentials;

public class GithubCredentials implements Credentials {

    private final String token;

    public GithubCredentials(String token){
        this.token = token;
    }

    @Override
    public String getKey() {
        throw new UnsupportedOperationException("Github does not support key / secret");
    }

    @Override
    public String getSecret() {
        throw new UnsupportedOperationException("Github does not support key / secret");
    }

    @Override
    public String getToken(){
        return this.token;
    }
}
