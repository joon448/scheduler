package org.example.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.dto.comment.CommentResponseDto;
import org.example.scheduler.dto.schedule.*;
import org.example.scheduler.entity.Comment;
import org.example.scheduler.entity.Schedule;
import org.example.scheduler.repository.CommentRepository;
import org.example.scheduler.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ScheduleResponseDto save(ScheduleRequestDto scheduleRequestDto){
        validateScheduleRequest(scheduleRequestDto, "등록");
        Schedule schedule = new Schedule(scheduleRequestDto.getName(), scheduleRequestDto.getPassword(), scheduleRequestDto.getTitle(), scheduleRequestDto.getContent());

        scheduleRepository.save(schedule);

        return new ScheduleResponseDto(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAll(){
        return scheduleRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Schedule::getModifiedAt).reversed())
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findByName(String name) {
        return scheduleRepository.findByName(name)
                .stream()
                .sorted(Comparator.comparing(Schedule::getModifiedAt).reversed())
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleWithCommentsResponseDto findById(Long id) {
        Schedule schedule = getScheduleOrThrow(id, "조회");

        List<CommentResponseDto> comments = commentRepository.findByScheduleId(id)
                .stream()
                .sorted(Comparator.comparing(Comment::getModifiedAt).reversed())
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());

        return new ScheduleWithCommentsResponseDto(new ScheduleResponseDto(schedule), comments);
    }

    @Transactional
    public ScheduleResponseDto update(Long id, ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        validateScheduleUpdateRequest(scheduleUpdateRequestDto, "수정");
        Schedule schedule = getScheduleOrThrow(id, "수정");
        validatePassword(schedule, scheduleUpdateRequestDto.getPassword(), "수정");

        if(scheduleUpdateRequestDto.getName()!=null){
            schedule.updateName(scheduleUpdateRequestDto.getName());
        }
        if(scheduleUpdateRequestDto.getTitle()!=null){
            schedule.updateTitle(scheduleUpdateRequestDto.getTitle());
        }

        scheduleRepository.flush(); // modifiedAt 반영
        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    public void delete(Long id, ScheduleDeleteRequestDto scheduleDeleteRequestDto) {
        Schedule schedule = getScheduleOrThrow(id, "삭제");
        validatePassword(schedule, scheduleDeleteRequestDto.getPassword(), "삭제");

        commentRepository.deleteByScheduleId(id);
        scheduleRepository.delete(schedule);
    }

    private Schedule getScheduleOrThrow(Long id, String action){
        return scheduleRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "일정 "+action+" 실패: 존재하지 않는 ID 입니다."));
    }

    private void validateScheduleRequest(ScheduleRequestDto scheduleRequestDto, String action) {
        if (scheduleRequestDto.getName() == null || scheduleRequestDto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 작성자명을 입력해주세요.");
        }
        if (scheduleRequestDto.getPassword() == null || scheduleRequestDto.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 비밀번호를 입력해주세요.");
        }
        if (scheduleRequestDto.getTitle() == null || scheduleRequestDto.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 제목을 입력해주세요.");
        }
        if (scheduleRequestDto.getContent() == null || scheduleRequestDto.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 내용을 입력해주세요.");
        }
        if(scheduleRequestDto.getName().length() > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 작성자명은 최대 20자까지 입력 가능합니다.");
        }
        if(scheduleRequestDto.getTitle().length() > 30){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 제목은 최대 30자까지 입력 가능합니다.");
        }
        if(scheduleRequestDto.getContent().length() > 200){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 내용은 최대 200자까지 입력 가능합니다.");
        }
    }

    private void validateScheduleUpdateRequest(ScheduleUpdateRequestDto scheduleUpdateRequestDto, String action) {
        if (scheduleUpdateRequestDto.getPassword() == null || scheduleUpdateRequestDto.getPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 비밀번호를 입력해주세요.");
        }
        if (scheduleUpdateRequestDto.getName() == null && scheduleUpdateRequestDto.getTitle() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 수정할 항목이 없습니다.");
        }
        if(scheduleUpdateRequestDto.getName() != null && scheduleUpdateRequestDto.getName().length() > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 작성자명은 최대 20자까지 입력 가능합니다.");
        }
        if(scheduleUpdateRequestDto.getTitle() != null && scheduleUpdateRequestDto.getTitle().length() > 30){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "일정 "+action+" 실패: 제목은 최대 30자까지 입력 가능합니다.");
        }
    }

    private void validatePassword(Schedule schedule, String password, String action) {
        if (!schedule.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "일정 "+action+" 실패: 비밀번호가 일치하지 않습니다.");
        }
    }

}
