package be.billington.rob;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigSections {

    private static final String DEFAULT_RULES = "default_rules.json";

    public static ConfigSections createConfigSections(String rulesFilePath, String prefix, String api) throws IOException {
        Gson gson = new Gson();
        Source source;
        if (rulesFilePath == null || rulesFilePath.length() == 0) {
            source = Okio.source(ConfigSections.class.getResourceAsStream(DEFAULT_RULES));
        } else {
            source = Okio.source(new File( rulesFilePath ));
        }
        BufferedSource configRulesFileJson = Okio.buffer(source);
        ConfigSections configSections = gson.fromJson(configRulesFileJson.readUtf8(), ConfigSections.class);
        //TODO improve filtering via maven
        configSections.filtering(prefix, api);

        configRulesFileJson.close();
        source.close();

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


    public boolean hasMatchInSections(String message){
        for (Section section : getSections()) {
            if ( message.toLowerCase().contains(section.getMatch()) ) {
                return true;
            }
        }
        return false;
    }

    public void filtering(String prefix, String api) {
        if (prefix == null || prefix.isEmpty()) {
            return ;
        }
        getSections().parallelStream().forEach((section) -> {
            if (section.getMatch().contains("${rob.prefix}")) {
                section.setMatch(prefix.toLowerCase());
            }
            if (section.getTitle().contains("${rob.api}")) {
                section.setTitle( section.getTitle().replace("${rob.api}", api) );
            }
        });
    }

}
