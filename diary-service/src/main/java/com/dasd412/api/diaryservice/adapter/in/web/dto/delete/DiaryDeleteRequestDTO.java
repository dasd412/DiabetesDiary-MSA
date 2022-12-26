package com.dasd412.api.diaryservice.adapter.in.web.dto.delete;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class DiaryDeleteRequestDTO {

    @NotNull
    private final Long writerId;

    @NotNull
    private final Long diaryId;

    public DiaryDeleteRequestDTO(Long writerId, Long diaryId) {
        this.writerId = writerId;
        this.diaryId = diaryId;
    }
}
