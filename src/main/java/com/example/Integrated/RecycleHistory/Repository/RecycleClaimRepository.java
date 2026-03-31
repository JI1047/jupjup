package com.example.Integrated.RecycleHistory.Repository;

import com.example.Integrated.RecycleHistory.Entity.RecycleClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecycleClaimRepository extends JpaRepository<RecycleClaim, Long> {
    Optional<RecycleClaim> findByIdAndStatus(UUID id, RecycleClaim.Status status);


    Optional<RecycleClaim> findById(UUID uuid);
}
