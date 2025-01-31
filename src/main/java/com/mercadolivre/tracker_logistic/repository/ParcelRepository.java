package com.mercadolivre.tracker_logistic.repository;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParcelRepository extends JpaRepository<ParcelEntity, UUID> {

    List<ParcelEntity> findBySender(String sender);

    List<ParcelEntity> findByRecipient(String recipient);

    List<ParcelEntity> findBySenderAndRecipient(String sender, String recipient);
}
