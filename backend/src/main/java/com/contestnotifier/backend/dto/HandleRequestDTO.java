package com.contestnotifier.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HandleRequestDTO {
    private String leetcodeHandle;
    private String codeforcesHandle;
}
