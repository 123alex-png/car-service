package com.car.maintenance.service;

import com.car.maintenance.model.MaintenanceRecord;
import com.car.maintenance.repository.MaintenanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qu Li 
 * Created on 2025-02-10
 */


@Service
public class MaintenanceRecordService {

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    public List<MaintenanceRecord> getAllRecords() {
        return maintenanceRecordRepository.findAll();
    }

    public MaintenanceRecord saveRecord(MaintenanceRecord record) {
        return maintenanceRecordRepository.save(record);
    }
}
