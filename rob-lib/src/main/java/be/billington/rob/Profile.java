package be.billington.rob;

public class Profile {

    private String api;
    private String owner;
    private String repo;
    private String prefix;
    private String branch;
    private String configPath;
    private String filePath;
    private String fromDate;
    private String toDate;

    public Profile() {
        super();
    }

    public Profile(String api, String owner, String repo, String prefix, String branch, String configPath, String filePath, String fromDate, String toDate) {
        this.api = api;
        this.owner = owner;
        this.repo = repo;
        this.prefix = prefix;
        this.branch = branch;
        this.configPath = configPath;
        this.filePath = filePath;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }


    public String getTitle(){
        return this.repo + " - " + this.fromDate + "::" + this.toDate;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
