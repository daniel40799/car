package com.project.car.taxonomy.repository;

import com.project.car.taxonomy.entity.ArtBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtBranchRepository extends JpaRepository<ArtBranch, Long> {
	List<ArtBranch> findAllByActiveTrueOrderByNameAsc();
}