package com.dasd412.api.writerservice.adapter.out.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface WriterChannels {

    @Output("outboundWriterChanges")
    MessageChannel getOutputChannel();
}
