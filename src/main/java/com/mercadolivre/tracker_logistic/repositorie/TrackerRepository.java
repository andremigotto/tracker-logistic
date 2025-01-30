package com.mercadolivre.tracker_logistic.repositorie;

import com.mercadolivre.tracker_logistic.domain.tracker.TrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackerRepository extends JpaRepository<TrackerEntity, UUID> {
}
