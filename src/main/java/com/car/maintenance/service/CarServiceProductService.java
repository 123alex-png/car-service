package com.car.maintenance.service;

import com.car.maintenance.model.CarServiceProduct;
import com.car.maintenance.repository.CarServiceProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qu Li 
 * Created on 2025-02-10
 */
@Service
@RequiredArgsConstructor
public class CarServiceProductService {
    @Autowired
    private final CarServiceProductRepository carServiceProductRepository;

    public void createCarServiceProduct(String name, Double unitPrice) {
        carServiceProductRepository.save(new CarServiceProduct(null, name, unitPrice));
    }

    public void saveCarServiceProduct(CarServiceProduct carServiceProduct) {
        carServiceProductRepository.save(carServiceProduct);
    }

    public CarServiceProduct getCarServiceProduct(Long id) {
        return carServiceProductRepository.findById(id).orElse(null);
    }

    public List<CarServiceProduct> getAllCarServiceProducts() {
        return carServiceProductRepository.findAll();
    }
}
