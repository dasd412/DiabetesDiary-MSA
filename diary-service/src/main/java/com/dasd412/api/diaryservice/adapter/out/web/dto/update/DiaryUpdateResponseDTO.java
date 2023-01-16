package com.dasd412.api.diaryservice.adapter.out.web.dto.update;

public class DiaryUpdateResponseDTO {

    private final Long id;

    public DiaryUpdateResponseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
