package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.domain.parcel.ParcelEntity;
import com.mercadolivre.tracker_logistic.repository.ParcelRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ParcelCleanupService {

    private final ParcelRepository parcelRepository;

    public ParcelCleanupService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    //Executa o job TODO DOMINGO ÀS 03:00 DA MANHÃ
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void deleteExpiredParcels() {
        List<ParcelEntity> expiredParcels = parcelRepository.findParcelsToDelete(Instant.now());

        if (!expiredParcels.isEmpty()) {
            parcelRepository.deleteAll(expiredParcels);
        }
    }
}
