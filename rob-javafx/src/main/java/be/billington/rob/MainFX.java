package be.billington.rob;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okio.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFX extends Application {

    private Stage primaryStage;

    @FXML
    private SplitPane rootLayout;

    @FXML
    private TextField txtOwner;
    @FXML
    private TextField txtRepo;
    @FXML
    private ComboBox<String> comboApi;
    @FXML
    private TextField txtPrefix;
    @FXML
    private TextField txtBranch;
    @FXML
    private TextArea txtConsole;
    @FXML
    private TextField txtFilePath;
    @FXML
    private DatePicker txtFromDate;
    @FXML
    private DatePicker txtToDate;

    //private Combo profilesCombo;

    private Logger logger;
    private File configFile, profileFile;
    private List<Profile> profiles;
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    private Map<String, String> config = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainFX.class.getResource("/be/billington/rob/Layout2.fxml"));

            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rob");
        initRootLayout();
    }

    @FXML
    private void initialize() {
        LoggerContext context = new LoggerContext();
        logger = context.getLogger(MainFX.class);

        initConfig();
        initProfiles();

        initUIProfiles(false);

        initUILogger(context);
    }

    private void initUIProfiles(boolean last) {
        int pos = 0;
        if (last){
            pos = profiles.size() - 1;
        }
        if (profiles != null) {
            //TODO profilesCombo.removeAll();
            //TODO profiles.forEach((p) -> profilesCombo.add(p.getTitle()));
            //TODO profilesCombo.select(pos);
        }

        bindProfile(pos);
    }

    private void initUILogger(LoggerContext context) {
        ConsoleAppender consoleAppender = new ConsoleAppender<>();
        UIAppender uiAppender = new UIAppender((txt) ->  Platform.runLater(() -> txtConsole.appendText(txt)) );

        PatternLayoutEncoder pa = new PatternLayoutEncoder();
        pa.setPattern("%r %5p %c [%t] - %m%n");
        pa.setContext(context);
        pa.start();

        uiAppender.setContext(context);
        uiAppender.start();

        consoleAppender.setEncoder(pa);
        consoleAppender.setContext(context);
        consoleAppender.start();

        logger.addAppender(uiAppender);
        logger.addAppender(consoleAppender);
    }

    private void initConfig() {
        configFile = new File("./rob.conf");
        try {
            if (!readConfig()) {
                createConfig();
            }

        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    private void initProfiles() {
        profileFile = new File("./rob.profiles");
        try {
            Gson gson = new Gson();
            Source source = Okio.source(profileFile);
            BufferedSource profileGsonSource = Okio.buffer(source);
            Type listType = new TypeToken<ArrayList<Profile>>() {}.getType();
            profiles = gson.fromJson(profileGsonSource.readUtf8(), listType);

            profileGsonSource.close();
            source.close();

        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    private void writeConfig() throws IOException {
        Sink sink = Okio.sink(configFile);
        BufferedSink bufferedSink = Okio.buffer(sink);

        this.config.forEach((k, v) -> {
            try {
                bufferedSink.writeUtf8(k + "=" + v + "\n");
            } catch (IOException e) {
                logger.error("IOException: " + e.getMessage(), e);
            }
        });
        bufferedSink.close();
        sink.close();
    }

    private boolean readConfig() throws IOException {
        if (!configFile.exists()) {
            return false;
        }
        boolean simpleCheckatLeastOneLine = false;
        Source source = Okio.source(configFile);
        BufferedSource bufferedSource = Okio.buffer(source);

        String line = bufferedSource.readUtf8Line();
        while (line != null) {
            if (line.contains("=")) {
                String[] values = line.split("=");
                if (values.length > 1) {
                    this.config.put(values[0], values[1]);
                }
            }

            line = bufferedSource.readUtf8Line();
            simpleCheckatLeastOneLine = true;
        }
        bufferedSource.close();
        source.close();
        return simpleCheckatLeastOneLine;
    }

    private void createConfig() {
        //TODO dialog is config is missing ConfigDialog configDialog = new ConfigDialog(shell, config);
        //TODO config = configDialog.open();
        try {
            writeConfig();
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    private void initButtonActions() {
        //generate.setOnAction((event) -> robIt());

        //output.setOnAction((event) -> loadGeneratedFile());

        /*settings.setOnAction((event) -> createConfig());

        saveProfile.setOnAction(n(event) -> saveProfile());

        removeProfile.setOnAction((event) -> removeCurrentProfile());*/

        //quit.setOnAction((event) -> quit());
    }

    private void bindProfile(int pos) {
        if (profiles == null || profiles.isEmpty()) {
            comboApi.setValue(Rob.API_BITBUCKET);
            txtOwner.setText("afrogleap");
            txtRepo.setText("wecycle-android-recyclemanager");
            txtPrefix.setText("WEC");
            txtBranch.setText("development");
            txtFilePath.setText("./target/changelog.txt");
        } else {
            Profile profile = profiles.get(pos);

            if (profile.getApi().equalsIgnoreCase(Rob.API_BITBUCKET)){
                comboApi.setValue(Rob.API_BITBUCKET);
            } else {
                comboApi.setValue(Rob.API_GITHUB);
            }
            txtOwner.setText(profile.getOwner());
            txtRepo.setText(profile.getRepo());
            txtPrefix.setText(profile.getPrefix());
            txtBranch.setText(profile.getBranch());
            txtFilePath.setText(profile.getFilePath());
            if (profile.getToDate() != null && !profile.getToDate().isEmpty()) {
                txtToDate.setValue(LocalDate.parse(profile.getToDate()));
            }
            if (profile.getFromDate() != null && !profile.getFromDate().isEmpty()) {
                txtFromDate.setValue(LocalDate.parse(profile.getFromDate()));
            }
        }
    }
/*
    private void removeCurrentProfile(){
        profiles.remove(profilesCombo.getSelectionIndex());
        initUIProfiles(true);
    }

    private void saveProfile() {
        Profile profile = new Profile(comboApi.getValue(), txtOwner.getText(), txtRepo.getText(),
                txtPrefix.getText(), txtBranch.getText(), "", txtFilePath.getText(),
                txtFromDate.getValue().toString(), txtToDate.getValue().toString());
        if (profiles == null){
            profiles = new ArrayList<>();
        }
        profiles.add(profile);
        initUIProfiles(true);
        try {
            Sink sink = Okio.sink(profileFile);
            BufferedSink bufferedSink = Okio.buffer(sink);
            Gson gson = new Gson();
            bufferedSink.writeUtf8(gson.toJson(profiles));

            bufferedSink.close();
            sink.close();
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }*/

    @FXML
    protected void robIt() {
        String dateFromStr = "", dateToStr = "";
        if (txtFromDate != null && txtFromDate.getValue() != null){
            dateFromStr = txtFromDate.getValue().toString();
        }
        if (txtToDate != null && txtToDate.getValue() != null){
            dateToStr = txtToDate.getValue().toString();
        }
        pool.execute( new RobRunnable(logger, comboApi.getValue(), txtOwner.getText(), txtRepo.getText(),
                txtPrefix.getText(), txtBranch.getText(), txtFilePath.getText(),
                dateFromStr, dateToStr, config) );

    }

    @FXML
    protected void loadGeneratedFile() {
        try {
            Source source = Okio.source(new File(txtFilePath.getText()));
            BufferedSource bufferedSource = Okio.buffer(source);
            String content = bufferedSource.readUtf8();
            txtConsole.setText(content);

            bufferedSource.close();
            source.close();
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    @FXML
    private void quit(){
        System.exit(0);
    }

}
