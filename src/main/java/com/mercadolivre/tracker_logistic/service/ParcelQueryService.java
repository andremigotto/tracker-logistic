package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.domain.parcel.ParcelPageResponse;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ParcelQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ParcelQueryService.class);
    private final ParcelRepository parcelRepository;

    public ParcelQueryService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    //Consulta um pacote pelo seu ID único, excluindo eventos se solicitado
    @Cacheable(value = "parcels", key = "{#id, #includeEvents}")
    public ParcelEntity getParcelById(UUID id, boolean includeEvents) {

        logger.info("Buscando pacote por ID: {} | Incluir eventos: {}", id, includeEvents);

        ParcelEntity parcel = includeEvents
                ? parcelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"))
                : parcelRepository.findParcelWithoutEvents(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcel not found"));

        logger.debug("Pacote encontrado: {}", parcel);

        return parcel;
    }

    //Consulta uma lista de pacotes com base em filtros de remetente e/ou destinatário se solicitado filtro.
    @Cacheable(value = "parcelsByFilter", key = "{#sender, #recipient, #pageable.pageNumber, #pageable.pageSize}", unless = "#result.isEmpty()")
    public ParcelPageResponse getParcelsByFilter(String sender, String recipient, Pageable pageable) {

        logger.info("Buscando pacotes - Remetente: {}, Destinatário: {}, Página: {}", sender, recipient, pageable);

        Page<ParcelEntity> parcels =
                (sender != null && recipient != null) ? parcelRepository.findBySenderAndRecipientWithoutEvents(sender, recipient, pageable) :
                        (sender != null) ? parcelRepository.findBySenderWithoutEvents(sender, pageable) :
                                (recipient != null) ? parcelRepository.findByRecipientWithoutEvents(recipient, pageable) :
                                        parcelRepository.findAllWithoutEvents(pageable);

        if (parcels.isEmpty()) {
            logger.warn("Nenhum pacote encontrado com os filtros aplicados.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No parcels found with the provided filters.");
        }

        logger.info("Consulta retornou {} pacotes", parcels.getTotalElements());

        return new ParcelPageResponse(
                parcels.getNumber(),
                parcels.getTotalPages(),
                parcels.getTotalElements(),
                parcels.getContent()
        );
    }

}
