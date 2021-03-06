package com.app.miliwili.src.emotionRecord;

import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionRecordRepository extends CrudRepository<EmotionRecord, Long> {
    Optional<EmotionRecord> findByIdAndStatus(Long emotionRecordId, String status);
    Optional<EmotionRecord> findByDateAndUserInfo_idAndStatus(LocalDate date, Long userId, String status);
    List<EmotionRecord> findByStatusAndUserInfo_IdAndDateBetween(String status, Long userId, LocalDate start, LocalDate date);
    List<EmotionRecord> findByUserInfo_Id(Long userId);
    List<EmotionRecord> findByUserInfo_IdAndStatus(Long userId, String status);
    boolean existsByDateAndUserInfo_IdAndStatus(LocalDate date, Long userId, String status);
}