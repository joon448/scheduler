package org.example.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.dto.ScheduleDeleteRequestDto;
import org.example.scheduler.dto.ScheduleUpdateRequestDto;
import org.example.scheduler.entity.Schedule;
import org.example.scheduler.repository.ScheduleRepository;
import org.example.scheduler.dto.ScheduleRequestDto;
import org.example.scheduler.dto.ScheduleResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public ScheduleResponseDto save(ScheduleRequestDto scheduleRequestDto){
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
    public ScheduleResponseDto findById(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(()-> new IllegalStateException("일정 조회 실패: 존재하지 않는 ID 입니다."));
        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto update(Long id, ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(()-> new IllegalStateException("일정 수정 실패: 존재하지 않는 ID 입니다."));
        if(!schedule.getPassword().equals(scheduleUpdateRequestDto.getPassword())){
            throw new IllegalStateException("일정 수정 실패: 비밀번호가 일치하지 않습니다.");
        }
        if(scheduleUpdateRequestDto.getName()!=null){
            schedule.updateName(scheduleUpdateRequestDto.getName());
        }
        if(scheduleUpdateRequestDto.getTitle()!=null){
            schedule.updateTitle(scheduleUpdateRequestDto.getTitle());
        }
        scheduleRepository.flush(); // modifiedAt 반영
        return new ScheduleResponseDto(schedule);
    }

    public void delete(Long id, ScheduleDeleteRequestDto scheduleDeleteRequestDto) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(()-> new IllegalStateException("일정 삭제 실패: 존재하지 않는 ID 입니다."));

        if(!schedule.getPassword().equals(scheduleDeleteRequestDto.getPassword())){
            throw new IllegalStateException("일정 삭제 실패: 비밀번호가 일치하지 않습니다.");
        }

        scheduleRepository.delete(schedule);
    }
}
