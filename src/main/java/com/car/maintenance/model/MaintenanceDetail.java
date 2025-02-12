package com.car.maintenance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "maintenance_details")
public class MaintenanceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private MaintenanceRecord record;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private CarServiceProduct service;

    private Integer quantity;
    private Double price;
}
