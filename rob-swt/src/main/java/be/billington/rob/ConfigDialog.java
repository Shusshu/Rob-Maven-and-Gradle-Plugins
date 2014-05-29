package be.billington.rob;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.HashMap;
import java.util.Map;


public class ConfigDialog extends Dialog {

    private Map<String, String> config;

    public ConfigDialog(Shell parent) {
        super(parent);
        this.config = new HashMap<>();
    }

    public Map<String, String> open() {
        Shell parent = getParent();
        Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(2, false));
        dialog.setSize(300, 200);
        dialog.setText("Settings");

        Label lblKey = new Label(dialog, SWT.NONE);
        lblKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblKey.setText("Bitbucket key:");

        Text txtKey = new Text(dialog, SWT.BORDER);
        txtKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtKey.setText("xxx");

        Label lblSecret = new Label(dialog, SWT.NONE);
        lblSecret.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblSecret.setText("Bitbucket secret:");

        Text txtSecret = new Text(dialog, SWT.BORDER);
        txtSecret.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtSecret.setText("yyy");

        Label lblToken = new Label(dialog, SWT.NONE);
        lblToken.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblToken.setText("Github token:");

        Text txtToken = new Text(dialog, SWT.BORDER);
        txtToken.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtToken.setText("xxxxxqqq");

        Button saveClose = new Button(dialog, SWT.PUSH);
        saveClose.setText("Save and Close");
        saveClose.setBounds(50, 50, 80, 30);

        saveClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                config.put(MainSWT.CONFIG_KEY, txtKey.getText());
                config.put(MainSWT.CONFIG_SECRET, txtSecret.getText());
                config.put(MainSWT.CONFIG_TOKEN, txtToken.getText());
                dialog.close();
            }
        });

        dialog.open();
        Display display = parent.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        return config;
    }
}