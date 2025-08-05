package com.example.TEAM_B_backend.core.repository;

import com.example.TEAM_B_backend.core.entity.TextFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextFileRepository extends JpaRepository<TextFile, Long> {

}
