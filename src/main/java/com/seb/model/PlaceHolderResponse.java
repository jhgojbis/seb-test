package com.seb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PlaceHolderResponse {
    private String postId;
    private String id;
    private Integer userId;
    private String title;
    private String name;
    private String email;
    private String body;
}
