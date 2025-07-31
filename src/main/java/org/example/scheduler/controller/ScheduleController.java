package org.example.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.dto.comment.CommentRequestDto;
import org.example.scheduler.dto.comment.CommentResponseDto;
import org.example.scheduler.dto.schedule.ScheduleDeleteRequestDto;
import org.example.scheduler.dto.schedule.ScheduleRequestDto;
import org.example.scheduler.dto.schedule.ScheduleResponseDto;
import org.example.scheduler.dto.schedule.ScheduleUpdateRequestDto;
import org.example.scheduler.service.CommentService;
import org.example.scheduler.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final CommentService commentService;

    @PostMapping("/schedules")
    public ScheduleResponseDto createSchedule(@RequestBody ScheduleRequestDto scheduleRequestDto) {
        return scheduleService.save(scheduleRequestDto);
    }

    @GetMapping("/schedules")
    public List<ScheduleResponseDto> getSchedules(@RequestParam(required = false) String name) {
        if (name == null) {
            return scheduleService.findAll();
        }
        return scheduleService.findByName(name);
    }

    @GetMapping("/schedules/{id}")
    public ScheduleResponseDto getSchedule(@PathVariable Long id) {
        return scheduleService.findById(id);
    }

    @PatchMapping("/schedules/{id}")
    public ScheduleResponseDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        return scheduleService.update(id, scheduleUpdateRequestDto);

    }

    @DeleteMapping("/schedules/{id}")
    public void deleteSchedule(@PathVariable Long id, @RequestBody ScheduleDeleteRequestDto scheduleDeleteRequestDto) {
        scheduleService.delete(id, scheduleDeleteRequestDto);
    }

    @PostMapping("/schedules/{scheduleId}/comments")
    public CommentResponseDto createComment(@PathVariable Long scheduleId, @RequestBody CommentRequestDto commentRequestDto){
        return commentService.save(commentRequestDto, scheduleId);
    }

}
