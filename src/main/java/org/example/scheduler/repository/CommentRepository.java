package org.example.scheduler.repository;

import org.example.scheduler.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByScheduleId(Long id);
    Long countByScheduleId(Long scheduleId);
    void deleteByScheduleId(Long id);
}
