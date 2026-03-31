package com.project.car.taxonomy.repository;

import com.project.car.taxonomy.entity.Locality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalityRepository extends JpaRepository<Locality, Long> {
	List<Locality> findAllByActiveTrueOrderByNameAsc();
}