package org.example.scheduler.dto.schedule;

import lombok.Getter;
import org.example.scheduler.dto.comment.CommentResponseDto;

import java.util.List;

@Getter
public class ScheduleWithCommentsResponseDto {
    private ScheduleResponseDto schedule;
    private List<CommentResponseDto> comments;

    public ScheduleWithCommentsResponseDto(ScheduleResponseDto scheduleResponseDto, List<CommentResponseDto> comments) {
        this.schedule = scheduleResponseDto;
        this.comments = comments;
    }
}