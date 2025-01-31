package com.mercadolivre.tracker_logistic.repository;


import com.mercadolivre.tracker_logistic.domain.dispatch.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DispatchRepository extends JpaRepository<DispatchEntity, UUID> {
}
