package be.billington.rob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.scene.control.TextField;


public class UIAppender extends AppenderBase<ILoggingEvent> {

    private PatternLayoutEncoder encoder;
    private ByteArrayOutputStream out;
    private TextField text;

    public UIAppender(TextField text){
        this.out = new ByteArrayOutputStream();
        this.text = text;
    }

    @Override
    public void start() {
        if (this.encoder == null) {
            addError("No encoder set for the appender named ["+ name +"].");
            return;
        }

        try {
            encoder.init(out);
        } catch (IOException e) {
        }
        super.start();
    }

    public void append(ILoggingEvent event) {
        // output the events as formatted by the wrapped layout
        try {
            this.encoder.doEncode(event);
            text.appendText("\n" + event.getFormattedMessage());

        } catch (IOException e) {
        }

    }

    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }
}