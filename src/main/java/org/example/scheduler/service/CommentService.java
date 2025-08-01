package org.example.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.dto.comment.CommentRequestDto;
import org.example.scheduler.dto.comment.CommentResponseDto;
import org.example.scheduler.entity.Comment;
import org.example.scheduler.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto save(CommentRequestDto commentRequestDto, Long scheduleId){
        if(commentRepository.countByScheduleId(scheduleId) >= 10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 등록 실패: 하나의 일정에 최대 10개의 댓글을 달 수 있습니다.");
        }
        if(commentRequestDto.getContent().length() > 100){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 등록 실패: 내용은 최대 100자까지 입력 가능합니다.");
        }
        Comment comment = new Comment(commentRequestDto.getName(), commentRequestDto.getPassword(), commentRequestDto.getContent(), scheduleId);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }
}
