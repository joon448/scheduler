package org.example.scheduler.dto.comment;

import lombok.Getter;
import org.example.scheduler.entity.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private final Long id;
    private final String name;
    private final String content;
    private final Long scheduleId;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;


    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.name = comment.getName();
        this.content = comment.getContent();
        this.scheduleId = comment.getScheduleId();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
