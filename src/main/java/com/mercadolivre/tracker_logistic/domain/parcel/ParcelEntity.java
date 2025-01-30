package com.mercadolivre.tracker_logistic.domain.parcel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mercadolivre.tracker_logistic.domain.event.EventEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<EventEntity> events;


    //Responsável por atualizar o status do pacote
    public void updateParcelStatus(String newStatus) {
        Map<String, Set<String>> validTransitions = Map.of(
                "CREATED", Set.of("IN_TRANSIT"),
                "IN_TRANSIT", Set.of("DELIVERED")
        );

        if ("CANCELLED".equals(this.status)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Invalid status transition: Parcel is already cancelled");
        }

        if ("DELIVERED".equals(this.status)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Invalid status transition: Parcel is already delivered");
        }

        if (!validTransitions.getOrDefault(this.status, Set.of()).contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status transition: Cannot transition from " + this.status + " to " + newStatus);
        }

        if ("DELIVERED".equals(newStatus)) {
            this.deliveredAt = Instant.now();
        }

        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
