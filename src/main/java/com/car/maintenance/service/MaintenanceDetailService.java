package com.car.maintenance.service;

import com.car.maintenance.model.MaintenanceDetail;
import com.car.maintenance.repository.MaintenanceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceDetailService {

    @Autowired
    private MaintenanceDetailRepository maintenanceDetailRepository;

    public List<MaintenanceDetail> getDetailsByRecord(Long recordId) {
        return maintenanceDetailRepository.findByRecordId(recordId);
    }

    public void saveAllDetails(List<MaintenanceDetail> details) {
        maintenanceDetailRepository.saveAll(details);
    }

    public void deleteDetail(Long id) {
        maintenanceDetailRepository.deleteById(id);
    }
}
