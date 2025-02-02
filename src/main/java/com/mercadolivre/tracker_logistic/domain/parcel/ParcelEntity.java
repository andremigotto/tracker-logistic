package com.mercadolivre.tracker_logistic.domain.parcel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "America/Sao_Paulo")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "America/Sao_Paulo")
    private Instant updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "America/Sao_Paulo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant deliveredAt;

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<EventEntity> events;

    @JsonIgnore
    private Instant expiredAt;

    public ParcelEntity(UUID id, String description, String sender, String recipient, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.description = description;
        this.sender = sender;
        this.recipient = recipient;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}