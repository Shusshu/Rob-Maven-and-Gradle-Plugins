package be.billington.rob;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigSections {

    private static final String DEFAULT_RULES = "default_rules.json";

    public static ConfigSections createConfigSections(String rulesFilePath, String prefix) throws IOException {
        Gson gson = new Gson();
        Source source;
        if (rulesFilePath == null){
            source = Okio.source(ConfigSections.class.getResourceAsStream(DEFAULT_RULES));
        } else {
            source = Okio.source(new File( rulesFilePath ));
        }
        BufferedSource configRulesFileJson = Okio.buffer(source);
        ConfigSections configSections = gson.fromJson(configRulesFileJson.readUtf8(), ConfigSections.class);
        //TODO improve filtering via maven
        configSections.filtering(prefix);

        return configSections;
    }

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

    public void initMap(Map<String, List<String>> commitListMap) {
        for (Section section : getSections()){
            commitListMap.put(section.getTitle(), new ArrayList<>());
        }
        for (Section section : getExclusiveSections()){
            commitListMap.put(section.getTitle(), new ArrayList<>());
        }
    }
}
