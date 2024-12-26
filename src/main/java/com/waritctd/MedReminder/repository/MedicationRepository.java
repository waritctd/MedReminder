package com.waritctd.MedReminder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.waritctd.MedReminder.model.Medication;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
	
	List<Medication> findByUserId(Long userId);

}
