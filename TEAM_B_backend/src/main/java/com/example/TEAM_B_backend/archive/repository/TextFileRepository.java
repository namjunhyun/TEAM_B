package com.example.TEAM_B_backend.archive.repository;

import com.example.TEAM_B_backend.archive.entity.TextFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextFileRepository extends JpaRepository<TextFile, Long> {
    List<TextFile> findAllByUserId(Long userId);
    List<TextFile> findAllByUserIdOrderByCreatedAtDesc(Long userId); // 최신순(업로드 시간 기준 내림차순)
    List<TextFile> findAllByUserIdOrderByCreatedAtAsc(Long userId); // 오래된 순(업로드 시간 기준 오름차순)
    List<TextFile> findAllByUserIdOrderByOriginalFileNameAsc(Long userId); // 파일 이름 오름차순(가나다순, 숫자순)
    List<TextFile> findAllByUserIdOrderByOriginalFileNameDesc(Long userId); // 파일 이름 내림차순(역순)

    // 추후에 사용자가 정렬 방식을 선택하는 기능을 넣고 싶다면 위에 안쓰던 기능들 이용할 것.
    // 지금은 사용자가 정렬 방식 선택 못하고, 우리가 최신순으로 정렬 기준 정해서 제공하는 상태.
}
