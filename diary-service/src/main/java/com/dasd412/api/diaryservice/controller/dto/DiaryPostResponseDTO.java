package com.dasd412.api.diaryservice.controller.dto;

public class DiaryPostResponseDTO {

    private final Long id;

    public DiaryPostResponseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
