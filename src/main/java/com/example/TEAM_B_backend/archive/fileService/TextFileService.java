package com.example.TEAM_B_backend.archive.fileService;

import com.example.TEAM_B_backend.archive.dto.FileDetailResponseDto;
import com.example.TEAM_B_backend.archive.dto.TextFileDto;
import com.example.TEAM_B_backend.archive.repository.TextFileRepository;
import com.example.TEAM_B_backend.archive.entity.TextFile;
import com.example.TEAM_B_backend.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<TextFileDto> getFileList(Long userId) {
        List<TextFile> files = textFileRepository.findAllByUserIdOrderByCreatedAtDesc(userId); // 최신순 정렬 선택
        return files.stream()
                .map(TextFileDto::new)
                .collect(Collectors.toList());
    }

    public FileDetailResponseDto getFileDetails(Long fileId) {
        TextFile textFile = textFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        return new FileDetailResponseDto(
                textFile.getTranscript(),
                Map.of(
                        "default", textFile.getSummary1(),
                        "keypoint", textFile.getSummary2(),
                        "short", textFile.getSummary3()
                )
        );
    }
    public TextFileDto getTextFileByIdAndUserId(Long fileId, Long userId) {
        TextFile textFile = textFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        if (!textFile.getUser().getId().equals(userId)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        return new TextFileDto(textFile);
    }
}
