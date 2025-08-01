package org.example.scheduler.repository;

import org.example.scheduler.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
    List<Schedule> findByName(String name);
}
