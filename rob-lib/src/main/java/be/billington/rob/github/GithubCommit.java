package be.billington.rob.github;

import be.billington.rob.Commit;

public class GithubCommit implements Commit {

    private String sha;
    private CommitInfo commit;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public CommitInfo getCommit() {
        return commit;
    }

    public void setCommit(CommitInfo commit) {
        this.commit = commit;
    }

    @Override
    public String getMessage() {
        return getCommit().getMessage();
    }

    @Override
    public String getDate() {
        return getCommit().getAuthor().getDate();
    }
}
