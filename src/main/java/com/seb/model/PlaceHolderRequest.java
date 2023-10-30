package com.seb.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceHolderRequest {
    @NotBlank(message = "title is mandatory")
    private String title;
    @NotBlank(message = "body is mandatory")
    private String body;
    @NotNull(message = "userId is mandatory")
    private Integer userId;
}
