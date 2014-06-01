package be.billington.rob;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.function.Consumer;


public class UIAppender extends AppenderBase<ILoggingEvent> {

    private Consumer<String> consumer;

    public UIAppender(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void append(ILoggingEvent event) {
        consumer.accept("\n" + event.getFormattedMessage());
    }
}