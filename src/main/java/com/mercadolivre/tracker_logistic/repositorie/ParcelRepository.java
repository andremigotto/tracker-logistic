package com.mercadolivre.tracker_logistic.repositorie;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcelRepository extends JpaRepository<ParcelEntity, UUID> {
}
