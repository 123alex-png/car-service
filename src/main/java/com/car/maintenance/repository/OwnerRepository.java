package com.car.maintenance.repository;

import com.car.maintenance.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    List<Owner> findByName(String name);
}
