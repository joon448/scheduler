package org.example.scheduler.dto.comment;

import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {
    private String name;
    private String password;
    private String content;
}
