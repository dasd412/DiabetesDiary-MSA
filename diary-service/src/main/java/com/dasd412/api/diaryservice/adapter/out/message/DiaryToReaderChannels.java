package com.dasd412.api.diaryservice.adapter.out.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DiaryToReaderChannels {

    @Output("outboundDiaryChangesToReader")
    MessageChannel getOutputChannel();
}
