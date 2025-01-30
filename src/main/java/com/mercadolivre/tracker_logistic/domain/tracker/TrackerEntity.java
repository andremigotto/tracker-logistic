package com.mercadolivre.tracker_logistic.domain.tracker;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tracking_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackerEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String location;
    private String description;
    private Date dateTime;

    @ManyToOne
    @JoinColumn(name = "parcel_id", nullable = false)
    private ParcelEntity parcel;
}
