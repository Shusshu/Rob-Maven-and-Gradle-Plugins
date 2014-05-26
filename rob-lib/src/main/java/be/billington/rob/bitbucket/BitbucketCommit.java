package be.billington.rob.bitbucket;

import be.billington.rob.Commit;

public class BitbucketCommit implements Commit {

    private String date;
    private String message;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
