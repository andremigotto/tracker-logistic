package com.mercadolivre.tracker_logistic.repository;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParcelRepository extends JpaRepository<ParcelEntity, UUID> {

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.sender = :sender AND p.recipient = :recipient")
    Page<ParcelEntity> findBySenderAndRecipientWithoutEvents(@Param("sender") String sender, @Param("recipient") String recipient, Pageable pageable);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.sender = :sender")
    Page<ParcelEntity> findBySenderWithoutEvents(@Param("sender") String sender, Pageable pageable);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.recipient = :recipient")
    Page<ParcelEntity> findByRecipientWithoutEvents(@Param("recipient") String recipient, Pageable pageable);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p")
    Page<ParcelEntity> findAllWithoutEvents(Pageable pageable);

    @Query("SELECT new com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity(p.id, p.description, p.sender, p.recipient, p.status, p.createdAt, p.updatedAt) FROM ParcelEntity p WHERE p.id = :id")
    Optional<ParcelEntity> findParcelWithoutEvents(@Param("id") UUID id);

    @Query("SELECT p FROM ParcelEntity p WHERE p.expiredAt < :now")
    List<ParcelEntity> findParcelsToDelete(@Param("now") Instant now);
}
