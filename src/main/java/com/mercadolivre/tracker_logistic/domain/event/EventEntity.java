package com.mercadolivre.tracker_logistic.domain.event;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String location;
    private String description;
    private Instant dateTime;

    @ManyToOne
    @JoinColumn(name = "parcel_id", nullable = false)
    private ParcelEntity parcel;
}
