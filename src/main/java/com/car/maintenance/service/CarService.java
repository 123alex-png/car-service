package com.car.maintenance.service;

import com.car.maintenance.model.Owner;
import com.car.maintenance.repository.CarRepository;
import com.car.maintenance.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public void saveCar(Owner owner, String licensePlate, String brand, String model) {
        Car car = new Car(null, owner, licensePlate, brand, model);
        carRepository.save(car);
    }

    public void saveCar(Car car) {
        carRepository.save(car);
    }

    public List<Car> getCarsByOwnerId(Long ownerId) {
        return carRepository.findByOwnerId(ownerId);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public void deleteCar(Long carId) {
        carRepository.deleteById(carId);
    }
}
