package com.dasd412.api.diaryservice.common.utils.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

/**
 * 문자열 <-> LocalDateTime 변환을 도와주는 유틸 클래스.
 */
public class DateStringConverter {

    private DateStringConverter() {
    }

    public static LocalDateTime convertMapParamsToStartDate(Map<String, String> startYearMonthDayEndYearMonthDay) {
        return LocalDateTime.of(Integer.parseInt(startYearMonthDayEndYearMonthDay.get("startYear")),
                Integer.parseInt(startYearMonthDayEndYearMonthDay.get("startMonth")),
                Integer.parseInt(startYearMonthDayEndYearMonthDay.get("startDay")),
                0, 0);
    }

    public static LocalDateTime convertMapParamsToEndDate(Map<String, String> startYearMonthDayEndYearMonthDay) {
        return LocalDateTime.of(Integer.parseInt(startYearMonthDayEndYearMonthDay.get("endYear")),
                Integer.parseInt(startYearMonthDayEndYearMonthDay.get("endMonth")),
                Integer.parseInt(startYearMonthDayEndYearMonthDay.get("endDay")),
                0, 0);
    }

    public static boolean isStartDateEqualOrBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isEqual(endDate) || startDate.isBefore(endDate);
    }

    public static Optional<LocalDateTime> convertLocalDateTime(String year, String month, String day) {
        try {
            return Optional.of(LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0));
        } catch (NumberFormatException | DateTimeParseException exception) {
            return Optional.empty();
        }
    }
}
