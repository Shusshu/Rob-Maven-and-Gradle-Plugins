package be.billington.rob;

import be.billington.rob.bitbucket.BitbucketCredentials;
import be.billington.rob.github.GithubCredentials;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import okio.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import ch.qos.logback.classic.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainSWT {

    private Shell shell;
    private Text txtOwner, txtRepo, txtApi, txtPrefix, txtBranch, txtConsole, txtFilePath, txtFromDate, txtToDate;
    private Logger logger;
    public static final String CONFIG_KEY = "key";
    public static final String CONFIG_SECRET = "secret";
    public static final String CONFIG_TOKEN = "token";

    private Map<String, String> config = new HashMap<>();

    public MainSWT(Display display) {
        LoggerContext context = new LoggerContext();
        logger = context.getLogger(MainSWT.class);

        shell = new Shell(display);

        shell.setText("Rob");

        initConfig();
        initUI();
        initLogger(context);

        shell.setSize(1024, 768);

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void initLogger(LoggerContext context){
        ConsoleAppender consoleAppender = new ConsoleAppender<>();
        UIAppender uiAppender = new UIAppender(txtConsole);

        PatternLayoutEncoder pa = new PatternLayoutEncoder();
        pa.setPattern("%r %5p %c [%t] - %m%n");
        pa.setContext(context);
        pa.start();

        uiAppender.setEncoder(pa);
        uiAppender.setContext(context);
        uiAppender.start();

        consoleAppender.setEncoder(pa);
        consoleAppender.setContext(context);
        consoleAppender.start();

        logger.addAppender(uiAppender);
        //logger.addAppender(consoleAppender);
    }

    private void initConfig(){
        File fileConfig = new File("./rob.conf");

        try {
            if (!fileConfig.exists()){
                createConfig();

                writeConfig(fileConfig);
            } else {
                readConfig(fileConfig);
            }

        } catch (IOException  e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    private void writeConfig(File fileConfig) throws IOException {
        Sink sink = Okio.sink(fileConfig);
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

    private void readConfig(File fileConfig) throws IOException {
        Source source = Okio.source(fileConfig);
        BufferedSource bufferedSource = Okio.buffer(source);

        String line = bufferedSource.readUtf8Line();
        while (line != null && !line.isEmpty()){
            String[] values = line.split("=");
            this.config.put(values[0], values[1]);

            line = bufferedSource.readUtf8Line();
        }
        bufferedSource.close();
        source.close();
    }

    private void createConfig(){
        ConfigDialog configDialog = new ConfigDialog(shell);
        config = configDialog.open();
    }

    public void initUI() {
        shell.setLayout(new FillLayout());

        //1st column
        Composite container = new Composite(shell, SWT.PUSH);
        GridLayout layout = new GridLayout(2, false);

        layout.marginRight = 5;
        layout.marginLeft = 10;
        container.setLayout(layout);

        Label lblRepo = new Label(container, SWT.NONE);
        lblRepo.setText("Repository name:");

        txtRepo = new Text(container, SWT.BORDER);
        txtRepo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtRepo.setText("wecycle-android-recyclemanager");

        Label lblOwner = new Label(container, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblNewLabel.horizontalIndent = 1;
        lblOwner.setLayoutData(gd_lblNewLabel);
        lblOwner.setText("Owner:");

        txtOwner = new Text(container, SWT.BORDER);
        txtOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtOwner.setText("afrogleap");

        Label lblApi = new Label(container, SWT.NONE);
        lblApi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblApi.setText("API:");

        txtApi = new Text(container, SWT.BORDER);
        txtApi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtApi.setText("bitbucket");

        Label lblPrefix = new Label(container, SWT.NONE);
        lblPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblPrefix.setText("Jira Prefix:");

        txtPrefix = new Text(container, SWT.BORDER);
        txtPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtPrefix.setText("WEC");

        Label lblBranch = new Label(container, SWT.NONE);
        lblBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblBranch.setText("Branch:");

        txtBranch = new Text(container, SWT.BORDER);
        txtBranch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtBranch.setText("development");

        Label lblFilePath = new Label(container, SWT.NONE);
        lblFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblFilePath.setText("File Path:");

        txtFilePath = new Text(container, SWT.BORDER);
        txtFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtFilePath.setText("./target/changelog.txt");

        Label lblFromDate = new Label(container, SWT.NONE);
        lblFromDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblFromDate.setText("From date:");

        txtFromDate = new Text(container, SWT.BORDER);
        txtFromDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtFromDate.setText("");

        Label lblToDate = new Label(container, SWT.NONE);
        lblToDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblToDate.setText("To date:");

        txtToDate = new Text(container, SWT.BORDER);
        txtToDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtToDate.setText("");

        Button generate = new Button(container, SWT.PUSH);
        generate.setText("Generate");
        generate.setBounds(50, 50, 80, 30);

        generate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new RobThread(logger, txtApi.getText(), txtOwner.getText(), txtRepo.getText(),
                        txtPrefix.getText(), txtBranch.getText(), txtFilePath.getText(),
                        txtFromDate.getText(), txtToDate.getText(), config).start();
            }
        });

        Button quit = new Button(container, SWT.PUSH);
        quit.setText("Quit");
        quit.setBounds(50, 50, 80, 30);

        quit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.getDisplay().dispose();
                System.exit(0);
            }
        });


        Button output = new Button(container, SWT.PUSH);
        output.setText("Output");
        output.setBounds(50, 50, 80, 30);

        output.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadGeneratedFile();
            }
        });

        //2nd column
        Composite rightContainer = new Composite(shell, SWT.PUSH);
        rightContainer.setLayout(new FillLayout());
        txtConsole = new Text(rightContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        txtConsole.setEditable(false);
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
