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

        //Definindo as transições válidas de status
        Map<String, List<String>> validTransitions = Map.of(
                "CREATED", List.of("IN_TRANSIT", "CANCELLED"),
                "IN_TRANSIT", List.of("DELIVERED")
        );

        //Verifica se o pacote já foi cancelado
        if ("CANCELLED".equals(this.status)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Invalid status transition: Parcel is already cancelled");
        }

        //Verifica se o status informado é válido
        if (!validTransitions.containsKey(this.status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status. Allowed values: CREATED, IN_TRANSIT, DELIVERED");
        }

        //Verifica se a transição de status é válida
        if (!validTransitions.get(this.status).equals(newStatus)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Invalid status transition: Status must follow CREATED -> IN_TRANSIT -> DELIVERED");
        }

        //Atualiza a data de entrega caso o status seja "DELIVERED"
        if ("DELIVERED".equals(newStatus)) {
            this.deliveredAt = Instant.now();
        }

        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
