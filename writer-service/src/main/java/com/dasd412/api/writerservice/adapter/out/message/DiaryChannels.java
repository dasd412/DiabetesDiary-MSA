package com.dasd412.api.writerservice.adapter.out.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DiaryChannels {
    @Input("inboundDiaryChanges")
    SubscribableChannel getChannel();
}
