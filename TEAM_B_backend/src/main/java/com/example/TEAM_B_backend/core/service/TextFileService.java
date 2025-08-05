package com.example.TEAM_B_backend.core.service;

import com.example.TEAM_B_backend.core.repository.TextFileRepository;
import com.example.TEAM_B_backend.core.entity.TextFile;
import com.example.TEAM_B_backend.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class TextFileService {

    private final TextFileRepository textFileRepository;

    public TextFileService(TextFileRepository textFileRepository) {
        this.textFileRepository = textFileRepository;
    }

    public void saveTextFile(String originalFileName, String transcript,
                             String summary1, String summary2, String summary3,
                             User user) {
        TextFile textFile = new TextFile(originalFileName, transcript, summary1, summary2, summary3, user);
        textFileRepository.save(textFile);
    }
}
