package be.billington.rob;

import okio.Buffer;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class RobLogManager {

    protected final LocalDate startDate;
    protected final LocalDate endDate;
    private final ConfigSections config;
    private final Logger log;
    private final Map<String, List<String>> commitListMap;

    public RobLogManager(Logger log, ConfigSections config, String fromDate, String toDate) {
        this.commitListMap = new LinkedHashMap<>();
        this.log = log;
        this.config = config;

        if (toDate != null && toDate.length() > 0) {
            this.endDate = LocalDate.parse(toDate, DateTimeFormatter.ISO_LOCAL_DATE);

        } else {
            this.endDate = LocalDate.now();
        }

        if (fromDate != null && fromDate.length() > 0 && !fromDate.equals(toDate)) {
            this.startDate = LocalDate.parse(fromDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            this.startDate = this.endDate.minusDays(14l);
        }

        if (this.startDate.isAfter(this.endDate)){
            throw new IllegalArgumentException("'From date' must be before 'to date'");
        }

        initMap();
    }

    public void initMap() {
        for (Section section : this.config.getSections()){
            commitListMap.put(section.getTitle(), new ArrayList<>());
        }
        for (Section section : this.config.getExclusiveSections()){
            commitListMap.put(section.getTitle(), new ArrayList<>());
        }
    }

    public Logger getLog(){
        return this.log;
    }

    protected abstract List<? extends Commit> fetchFromApi(int page);

    protected abstract boolean hasNextPage();

    public void fetchAndProcessCommitMessages() {
        int page = 1;
        List<? extends Commit> commits = fetchFromApi(page);

        LocalDate commitDate;
        boolean readNextPage = true;

        do {
            getLog().info("Walking around house number: " + page + " (Page)");

            for (Commit commit : commits) {

                commitDate = LocalDate.parse(commit.getDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                if (commitDate.isAfter(this.startDate) &&
                        (commitDate.isBefore(this.endDate) || commitDate.isEqual(this.endDate)) ) {

                    if (commit.getMessage().charAt(0) == '!') {
                        getLog().info("Electrified fences on this side, skipping it (!)");
                        continue;
                    }

                    String[] commitMsgList = commit.getMessage().split("\n");

                    config.getSections().forEach( (section) -> {
                        if (section.excludeCommit(commit.getMessage())) {
                            return ;
                        }
                        if (commit.getMessage().toLowerCase().contains(section.getMatch().toLowerCase())) {
                            commitListMap.get(section.getTitle()).add(commitMsgList[0]);
                        }
                    });

                    for (Section exclusiveSection : config.getExclusiveSections()) {
                        if (config.hasMatchInSections(commit.getMessage())) {
                            continue;
                        }
                        if (exclusiveSection.excludeCommit(commit.getMessage())) {
                            continue;
                        }
                        commitListMap.get(exclusiveSection.getTitle()).add(commitMsgList[0]);
                    }

                } else if (commitDate.isBefore(this.endDate) ) {
                    readNextPage = false;
                    getLog().info("Shit, cops on the lookout.");
                    break;
                }
            }
            if (readNextPage && hasNextPage()) {
                getLog().info("A few more houses to go.");
                page++;
                commits = fetchFromApi(page);

                if (commits == null || commits.isEmpty()) {
                    readNextPage = false;
                    getLog().info("Not enough room in the bag. Gotta stop");
                }

            } else {
                getLog().info("Last one for today.");
                readNextPage = false;
            }

        } while (readNextPage);

        getLog().info("Time is up. It is no longer safe to rob houses.");
    }


    public void generateFile(File targetDirectory, String filePath) {
        getLog().info("Counting how much stuff I have robbed today. (Generate file)");

        Buffer buffer = new Buffer();
        buffer.writeUtf8("Changelog from " + this.startDate + " until " + this.endDate + "\n");


        commitListMap.forEach( (title, commits) -> writeToFile(buffer, title, commits) );

        try {
            File file;
            if (targetDirectory != null && targetDirectory.exists()){
                file = new File(targetDirectory, filePath );
            } else {
                file = new File( filePath );
            }
            file.getParentFile().mkdirs();
            file.createNewFile();

            buffer.writeTo(new FileOutputStream( file ));

            getLog().info("Report: " + file.getPath());

        } catch (IOException ex) {
            getLog().error( "Could not create file: " + ex.getMessage(), ex);
        }
    }
    private void writeToFile(Buffer buffer, String title, List<String> content) {
        if (content.size() > 0) {
            buffer.writeUtf8(title);
            for (String msg : content){
                buffer.writeUtf8("- " + msg + "\n");
            }
        }
    }

}
