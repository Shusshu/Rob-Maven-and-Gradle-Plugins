package be.billington.rob.bitbucket;

import be.billington.rob.Credentials;

public class BitbucketCredentials implements Credentials {
    private final String key;
    private final String secret;

    public BitbucketCredentials(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public String getToken(){
        throw new UnsupportedOperationException("Bitbucket does not support token");
    }
}
