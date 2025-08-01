package org.example.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.dto.comment.CommentRequestDto;
import org.example.scheduler.dto.comment.CommentResponseDto;
import org.example.scheduler.entity.Comment;
import org.example.scheduler.repository.CommentRepository;
import org.example.scheduler.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public CommentResponseDto save(CommentRequestDto commentRequestDto, Long scheduleId){
        validateScheduleExists(scheduleId, "등록");
        validateCommentRequest(commentRequestDto, "등록");
        validateCommentLimit(scheduleId, "등록");
        Comment comment = new Comment(commentRequestDto.getName(), commentRequestDto.getPassword(), commentRequestDto.getContent(), scheduleId);

        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    private void validateScheduleExists(Long scheduleId, String action) {
        if(!scheduleRepository.existsById(scheduleId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글 "+action+" 실패: 존재하지 않는 ID입니다.");
        }
    }

    private void validateCommentRequest(CommentRequestDto commentRequestDto, String action) {
        if (commentRequestDto.getName() == null || commentRequestDto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 "+action+" 실패: 작성자명을 입력해주세요.");
        }
        if (commentRequestDto.getPassword() == null || commentRequestDto.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 "+action+" 실패: 비밀번호를 입력해주세요.");
        }
        if (commentRequestDto.getContent() == null || commentRequestDto.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 "+action+" 실패: 내용을 입력해주세요.");
        }
        if(commentRequestDto.getName().length() > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 "+action+" 실패: 작성자명은 최대 20자까지 입력 가능합니다.");
        }
        if(commentRequestDto.getContent().length() > 100){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 "+action+" 실패: 내용은 최대 100자까지 입력 가능합니다.");
        }
    }

    private void validateCommentLimit(Long scheduleId, String action) {
        if (commentRepository.countByScheduleId(scheduleId) >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 "+action+" 실패: 하나의 일정에 최대 10개의 댓글을 달 수 있습니다.");
        }
    }
}
