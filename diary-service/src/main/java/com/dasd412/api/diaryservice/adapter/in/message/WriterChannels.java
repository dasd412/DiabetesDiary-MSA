package com.dasd412.api.diaryservice.adapter.in.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface WriterChannels {

    @Input("inboundWriterChanges")
    SubscribableChannel getChannel();
}
