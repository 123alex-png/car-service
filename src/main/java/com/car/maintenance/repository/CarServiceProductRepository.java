package com.car.maintenance.repository;

import com.car.maintenance.model.CarServiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
@Repository
public interface CarServiceProductRepository extends JpaRepository<CarServiceProduct, Long> {
}
