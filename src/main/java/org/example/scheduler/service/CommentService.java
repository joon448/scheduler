package org.example.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.dto.comment.CommentRequestDto;
import org.example.scheduler.dto.comment.CommentResponseDto;
import org.example.scheduler.entity.Comment;
import org.example.scheduler.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto save(CommentRequestDto commentRequestDto, Long scheduleId){
        if(commentRepository.countByScheduleId(scheduleId) >= 10){
            throw new IllegalStateException("댓글 등록 실패: 하나의 일정에 최대 10개의 댓글을 달 수 있습니다.");
        }
        Comment comment = new Comment(commentRequestDto.getName(), commentRequestDto.getPassword(), commentRequestDto.getContent(), scheduleId);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }
}
