package com.project.car.inquiry.repository;

import com.project.car.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findAllByArtistProfileIdOrderByCreatedAtDesc(Long artistProfileId);
}

