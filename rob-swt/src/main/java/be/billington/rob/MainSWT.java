package be.billington.rob;

import be.billington.rob.bitbucket.BitbucketCredentials;
import be.billington.rob.github.GithubCredentials;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainSWT {

    private Shell shell;
    private Text txtOwner, txtRepo, txtApi, txtPrefix, txtBranch, txtConsole;
    private Logger logger;
    private static final String CONFIG_KEY = "key";
    private static final String CONFIG_SECRET = "secret";
    private static final String CONFIG_TOKEN = "token";

    private Map<String, String> config = new HashMap<>();

    public MainSWT(Display display) {

        logger = LoggerFactory.getLogger("");

        shell = new Shell(display);

        shell.setText("Rob");

        initConfig();
        initUI();

        shell.setSize(600, 400);
        shell.setLocation(300, 300);

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void initConfig(){
        try {
            Source source = Okio.source(new File("rob.conf"));
            BufferedSource bufferedSource = Okio.buffer(source);

            String line = bufferedSource.readUtf8Line();
            while (line != null && !line.isEmpty()){
                String[] values = line.split("=");
                config.put(values[0], values[1]);

                line = bufferedSource.readUtf8Line();
            }

        } catch (IOException  e) {
            logger.error("IOException: " + e.getMessage(), e);
        }
    }

    public void initUI() {
        GridLayout shellLayout = new GridLayout(2, false);
        shell.setLayout(shellLayout);

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

        Button generate = new Button(container, SWT.PUSH);
        generate.setText("Generate");
        generate.setBounds(50, 50, 80, 30);

        generate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                robLogs();
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

        //2nd column
        Composite rightContainer = new Composite(shell, SWT.PUSH);
        rightContainer.setLayout(new FillLayout());

        txtConsole = new Text(rightContainer, SWT.MULTI);
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

    public void robLogs(){
        logger.info("Robbing...");

        try {
            Credentials credentials;
            if (txtApi.getText().toLowerCase().equals(Rob.API_BITBUCKET)){
                credentials = new BitbucketCredentials(config.get(CONFIG_KEY), config.get(CONFIG_SECRET));
            } else {
                credentials = new GithubCredentials(config.get(CONFIG_TOKEN));
            }
            //rulesFile, filePath, fromDate, toDate, credentials);
            Rob.logs(logger, txtApi.getText(), txtOwner.getText(), txtRepo.getText(), txtPrefix.getText(), txtBranch.getText(), "", "./target/changelog.txt", "", "", credentials);

        } catch (Exception ex) {
            logger.error("Error: " + ex.getMessage(), ex);
        }

        logger.info("Robbed.");
    }
}
