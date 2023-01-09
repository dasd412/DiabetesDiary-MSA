package com.dasd412.api.diaryservice.adapter.out.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface DiaryChannels {

    @Output("outboundDiaryChanges")
    SubscribableChannel getOutputChannel();
}
