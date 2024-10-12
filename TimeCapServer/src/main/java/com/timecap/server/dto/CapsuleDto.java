package com.timecap.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CapsuleDto {
    private String capsule;
    private String recipientEmail;

    public CapsuleDto(String capsule, String recipientEmail) {
        this.capsule = capsule;
        this.recipientEmail = recipientEmail;
    }
}