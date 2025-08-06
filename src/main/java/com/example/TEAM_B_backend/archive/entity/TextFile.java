package com.example.TEAM_B_backend.archive.entity;

import com.example.TEAM_B_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "text_file")
public class TextFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String transcript; // transcript로 통일

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary1;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary2;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary3;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TextFile(String originalFileName, String transcript,
                      String summary1, String summary2, String summary3, User user) {
        this.originalFileName = originalFileName;
        this.transcript = transcript;
        this.summary1 = summary1;
        this.summary2 = summary2;
        this.summary3 = summary3;
        this.user = user;

    }
}
