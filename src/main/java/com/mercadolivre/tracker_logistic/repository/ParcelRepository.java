package com.mercadolivre.tracker_logistic.repository;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParcelRepository extends JpaRepository<ParcelEntity, UUID> {

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.sender = :sender AND p.recipient = :recipient")
    List<ParcelEntity> findBySenderAndRecipientWithoutEvents(@Param("sender") String sender, @Param("recipient") String recipient);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.sender = :sender")
    List<ParcelEntity> findBySenderWithoutEvents(@Param("sender") String sender);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.recipient = :recipient")
    List<ParcelEntity> findByRecipientWithoutEvents(@Param("recipient") String recipient);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.id = :id")
    Optional<ParcelEntity> findParcelWithoutEvents(@Param("id") UUID id);
}
