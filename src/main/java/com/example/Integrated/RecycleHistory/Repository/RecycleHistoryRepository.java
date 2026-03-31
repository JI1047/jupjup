package com.example.Integrated.RecycleHistory.Repository;

import com.example.Integrated.RecycleHistory.Entity.RecycleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecycleHistoryRepository extends JpaRepository<RecycleHistory, Integer> {
    List<RecycleHistory> findByUserId(Long userId);

}
