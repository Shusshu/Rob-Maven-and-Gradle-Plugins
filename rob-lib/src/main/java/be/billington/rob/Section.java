package be.billington.rob;

import java.util.List;

public class Section {

    private String title;
    private String match;
    private List<String> excludes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public boolean excludeCommit(String message) {
        if (excludes == null){
            return false;
        }
        for (String exclude : excludes) {
            if (message.toLowerCase().contains(exclude)) {
                return true;
            }
        }
        return false;
    }

    public String toString(){
        return this.title + " - " + this.match;
    }
}
