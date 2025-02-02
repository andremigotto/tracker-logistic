package com.mercadolivre.tracker_logistic.repository;

import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
}
