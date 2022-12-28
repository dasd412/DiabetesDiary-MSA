package com.dasd412.api.diaryservice.adapter.out.web.dto.delete;

public class DiaryDeleteResponseDTO {

    private final Long id;

    public DiaryDeleteResponseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
