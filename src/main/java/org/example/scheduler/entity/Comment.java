package org.example.scheduler.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    private Long scheduleId;

    public Comment(String name, String password, String content, Long scheduleId) {
        this.name = name;
        this.password = password;
        this.content = content;
        this.scheduleId = scheduleId;
    }

    public void updateContent(String content){
        this.content = content;
    }

    public void updateName(String name){
        this.name = name;
    }
}
