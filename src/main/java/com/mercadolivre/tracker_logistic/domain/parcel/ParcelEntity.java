package com.mercadolivre.tracker_logistic.domain.parcel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParcelEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String description;
    private String sender;
    private String recipient;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant deliveredAt;

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EventEntity> events;


    //Responsável por atualizar o status do pacote
    public void updateParcelStatus(String newStatus) {
        if (("IN_TRANSIT".equals(newStatus) && !"CREATED".equals(this.status)) ||
                ("DELIVERED".equals(newStatus) && !"IN_TRANSIT".equals(this.status))) {
            throw new IllegalArgumentException("Invalid status transition");
        }
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
