package com.dasd412.api.diaryservice.common.utils.date;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 년도,월,일,시,분,초에 해당하는 문자열을 LocalDateTime으로 변환하기 위한 객체
 */
@RequiredArgsConstructor
@Builder
public class DateStringJoiner {

    private final String year;
    private final String month;
    private final String day;

    private final String hour;
    private final String minute;
    private final String second;

    public LocalDateTime convertLocalDateTime() {
        String date = this.year + "-" + this.month + "-" + this.day + " " + this.hour + ":" + this.minute + ":" + this.second;
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
