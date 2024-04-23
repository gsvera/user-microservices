package com.esthetic.usermicroservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    public boolean error;
    public Object items;
    public String message;
    public ResponseDTO(Object obj) {
        this.items = obj;
    }

}
