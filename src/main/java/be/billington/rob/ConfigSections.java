package be.billington.rob;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigSections {

    private List<Section> sections;

    @SerializedName("exclusive_sections")
    private List<Section> exclusiveSections;

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getExclusiveSections() {
        return exclusiveSections;
    }

    public void setExclusiveSections(List<Section> exclusiveSections) {
        this.exclusiveSections = exclusiveSections;
    }

    public void addSectionMatch(String message){
        //TODO
    }

    public boolean hasMatchInSections(String message){
        for (Section section : getSections()) {
            if ( message.toLowerCase().contains(section.getMatch()) ) {
                return true;
            }
        }
        return false;
    }

    public void filtering(String prefix) {
        getSections().parallelStream().forEach((section) -> {
            if (section.getMatch().contains("${rob.prefix}")) {
                section.setMatch(prefix);
            }
        });
    }
}
