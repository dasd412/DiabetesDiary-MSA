package com.dasd412.api.writerservice.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveTestDTO {

    private String name;
    private String email;

    public SaveTestDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
