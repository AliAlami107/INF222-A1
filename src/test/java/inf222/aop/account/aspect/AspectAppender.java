package inf222.aop.account.aspect;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class AspectAppender extends AppenderBase<ILoggingEvent> {

    public final List<ILoggingEvent> events = new ArrayList<ILoggingEvent>();

    @Override
    public void append(ILoggingEvent event) {
        events.add(event);
    }
}