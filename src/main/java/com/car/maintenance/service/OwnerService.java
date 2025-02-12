package com.car.maintenance.service;

import com.car.maintenance.model.Owner;
import com.car.maintenance.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
@Service
@RequiredArgsConstructor
public class OwnerService {
    @Autowired
    private final OwnerRepository ownerRepository;

    public Owner saveOwner(String name, String phoneNumber) {
        return ownerRepository.save(new Owner(null, name, phoneNumber));
    }

    public Owner saveOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id).orElse(null);
    }

    public List<Owner> getOwnersByName(String name) {
        return ownerRepository.findByName(name);
    }

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }
}
