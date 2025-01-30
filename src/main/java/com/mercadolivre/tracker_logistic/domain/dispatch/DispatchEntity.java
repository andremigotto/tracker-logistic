package com.mercadolivre.tracker_logistic.domain.dispatch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "dispatches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DispatchEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private boolean isHoliday;
    private String funFact;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate estimatedDeliveryDate;

    @OneToOne
    @JoinColumn(name = "parcel_id", nullable = false)
    private ParcelEntity parcel;
}
