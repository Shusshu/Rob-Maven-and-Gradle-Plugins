package be.billington.rob;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okio.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

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

public class MainSWT {

    private Shell shell;
    private Text txtOwner, txtRepo, txtPrefix, txtBranch, txtConsole, txtFilePath, txtConfigPath;
    private DateTime dateFrom, dateTo;
    private Combo comboApi, profilesCombo;
    private Logger logger;
    private File configFile, profileFile;
    private List<Profile> profiles;
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    private Map<String, String> config = new HashMap<>();

    public MainSWT(Display display) {
        LoggerContext context = new LoggerContext();
        logger = context.getLogger(MainSWT.class);

        shell = new Shell(display);

        shell.setText("Rob");

        initConfig();
        initProfiles();
        initUI();
        initUIProfiles(false);
        initLogger(context);

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void initUIProfiles(boolean last) {
        int pos = 0;
        if (last){
            pos = profiles.size() - 1;
        }
        if (profiles != null) {
            profilesCombo.removeAll();
            profiles.forEach((p) -> profilesCombo.add(p.getTitle()));
            profilesCombo.select(pos);
        }

        bindProfile(pos);
    }

    private void initLogger(LoggerContext context) {
        ConsoleAppender consoleAppender = new ConsoleAppender<>();
        UIAppender uiAppender = new UIAppender((txt) -> Display.getDefault().asyncExec( () -> txtConsole.append(txt) ));

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
        ConfigDialog configDialog = new ConfigDialog(shell, config);
        config = configDialog.open();
        try {
            writeConfig();
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    public void initUI() {
        shell.setLayout(new FillLayout());

        Composite inputContainer = new Composite(shell, SWT.BORDER);
        inputContainer.setLayout(new RowLayout(SWT.VERTICAL));

        ToolBar bar = new ToolBar (inputContainer, SWT.FLAT | SWT.BORDER);
        initToolBar(bar);

        profilesCombo = new Combo(inputContainer, SWT.PUSH);
        profilesCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                bindProfile(profilesCombo.getSelectionIndex());
            }
        });

        //1st column
        Composite container = new Composite(inputContainer, SWT.PUSH);
        GridLayout layout = new GridLayout(2, false);

        layout.marginWidth = 10;
        container.setLayout(layout);

        Label lblRepo = new Label(container, SWT.NONE);
        lblRepo.setText("Repository name:");

        txtRepo = new Text(container, SWT.BORDER);
        txtRepo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblOwner = new Label(container, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblNewLabel.horizontalIndent = 1;
        lblOwner.setLayoutData(gd_lblNewLabel);
        lblOwner.setText("Owner:");

        txtOwner = new Text(container, SWT.BORDER);
        txtOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));


        Label lblApi = new Label(container, SWT.NONE);
        lblApi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblApi.setText("API:");

        comboApi = new Combo(container, SWT.PUSH);
        comboApi.setItems(new String[]{Rob.API_BITBUCKET, Rob.API_GITHUB});

        Label lblPrefix = new Label(container, SWT.NONE);
        lblPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblPrefix.setText("Jira Prefix:");

        txtPrefix = new Text(container, SWT.BORDER);
        txtPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblBranch = new Label(container, SWT.NONE);
        lblBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblBranch.setText("Branch:");

        txtBranch = new Text(container, SWT.BORDER);
        txtBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblFilePath = new Label(container, SWT.NONE);
        lblFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblFilePath.setText("File Path:");

        Composite filePathComposite = new Composite(container, SWT.FLAT);
        filePathComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

        txtFilePath = new Text(filePathComposite, SWT.BORDER);

        Button fileChooser = new Button(filePathComposite, SWT.PUSH);
        fileChooser.setText("Browse...");

        fileChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                FileDialog dialog = new FileDialog(shell);

                String filePath = dialog.open();

                if (filePath != null) {
                    txtFilePath.setText(filePath);
                }
            }
        });


        Label lblConfigPath = new Label(container, SWT.NONE);
        lblConfigPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblConfigPath.setText("Config Path:");

        Composite configPathComposite = new Composite(container, SWT.FLAT);
        configPathComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

        txtConfigPath = new Text(configPathComposite, SWT.BORDER);

        Button configFileChooser = new Button(configPathComposite, SWT.PUSH);
        configFileChooser.setText("Browse...");

        configFileChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                FileDialog dialog = new FileDialog(shell);

                String filePath = dialog.open();

                if (filePath != null) {
                    txtConfigPath.setText(filePath);
                }
            }
        });

        Label lblFromDate = new Label(container, SWT.NONE);
        lblFromDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblFromDate.setText("From date:");

        dateFrom = new DateTime(container, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);

        Label lblToDate = new Label(container, SWT.NONE);
        lblToDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblToDate.setText("To date:");

        dateTo = new DateTime(container, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);

        //2nd column
        Composite rightContainer = new Composite(shell, SWT.BORDER);
        rightContainer.setLayout(new FillLayout());
        txtConsole = new Text(rightContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        txtConsole.setEditable(false);
    }

    private void initToolBar(ToolBar bar) {
        ToolItem generate = new ToolItem(bar, SWT.PUSH);
        generate.setText("Generate");
        generate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                robIt();
            }
        });

        new ToolItem(bar, SWT.SEPARATOR);

        ToolItem output = new ToolItem(bar, SWT.PUSH);
        output.setText("Display output");

        output.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadGeneratedFile();
            }
        });

        new ToolItem(bar, SWT.SEPARATOR);

        ToolItem settings = new ToolItem(bar, SWT.PUSH);
        settings.setText("Settings");

        settings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createConfig();
            }
        });

        new ToolItem(bar, SWT.SEPARATOR);

        ToolItem saveProfile = new ToolItem(bar, SWT.PUSH);
        saveProfile.setText("Save/Add profile");

        saveProfile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveProfile();
            }
        });

        new ToolItem(bar, SWT.SEPARATOR);

        ToolItem removeProfile = new ToolItem(bar, SWT.PUSH);
        removeProfile.setText("Remove current profile");

        removeProfile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeCurrentProfile();
            }
        });

        new ToolItem(bar, SWT.SEPARATOR);

        ToolItem quit = new ToolItem(bar, SWT.PUSH);
        quit.setText("Quit");

        quit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.getDisplay().dispose();
                System.exit(0);
            }
        });
    }

    private void bindProfile(int pos) {
        if (profiles == null || profiles.isEmpty()) {
            comboApi.select(0);
            txtOwner.setText("afrogleap");
            txtRepo.setText("wecycle-android-recyclemanager");
            txtPrefix.setText("WEC");
            txtBranch.setText("development");
            txtFilePath.setText("./target/changelog.txt");

        } else {
            Profile profile = profiles.get(pos);

            if (profile.getApi().equalsIgnoreCase(Rob.API_BITBUCKET)){
                comboApi.select(0);
            } else {
                comboApi.select(1);
            }
            txtOwner.setText(profile.getOwner());
            txtRepo.setText(profile.getRepo());
            txtPrefix.setText(profile.getPrefix());
            txtBranch.setText(profile.getBranch());
            txtFilePath.setText(profile.getFilePath());
            if (profile.getFromDate() != null && !profile.getFromDate().isEmpty()) {
                LocalDate date = LocalDate.parse(profile.getFromDate());
                dateFrom.setDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            }
            if (profile.getToDate() != null && !profile.getToDate().isEmpty()) {
                LocalDate date = LocalDate.parse(profile.getToDate());
                dateTo.setDate(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            }
        }
    }

    private void removeCurrentProfile(){
        profiles.remove(profilesCombo.getSelectionIndex());
        initUIProfiles(true);
    }

    private void saveProfile() {

        String dateFromStr = LocalDate.of(dateFrom.getYear(), dateFrom.getMonth() + 1, dateFrom.getDay()).toString();
        String dateToStr = LocalDate.of(dateTo.getYear(), dateTo.getMonth() + 1, dateTo.getDay()).toString();

        Profile profile = new Profile(comboApi.getText(), txtOwner.getText(), txtRepo.getText(),
                txtPrefix.getText(), txtBranch.getText(), "", txtFilePath.getText(),
                dateFromStr, dateToStr);
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
    }

    private void robIt() {
        String dateFromStr = LocalDate.of(dateFrom.getYear(), dateFrom.getMonth() + 1, dateFrom.getDay()).toString();
        String dateToStr = LocalDate.of(dateTo.getYear(), dateTo.getMonth() + 1, dateTo.getDay()).toString();

        Configuration conf = new Configuration.ConfigurationBuilder(logger, comboApi.getText(), txtRepo.getText(), txtOwner.getText())
                .branch(txtBranch.getText()).prefix(txtPrefix.getText()).filePath(txtFilePath.getText()).fromDate(dateFromStr).toDate(dateToStr)
                .token(config.get(Common.CONFIG_TOKEN)).key(config.get(Common.CONFIG_KEY)).secret(config.get(Common.CONFIG_SECRET))
                .username(config.get(Common.CONFIG_USERNAME)).password(config.get(Common.CONFIG_PASSWORD))
                .configPath(txtConfigPath.getText()).build();

        pool.execute( new RobWorker(conf) );

    }

    private void loadGeneratedFile() {
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

    public void center(Shell shell) {

        Rectangle bds = shell.getDisplay().getBounds();

        Point p = shell.getSize();

        int nLeft = (bds.width - p.x) / 2;
        int nTop = (bds.height - p.y) / 2;

        shell.setBounds(nLeft, nTop, p.x, p.y);
    }

    public static void main(String[] args) {
        Display display = new Display();
        new MainSWT(display);
        display.dispose();
    }
}
