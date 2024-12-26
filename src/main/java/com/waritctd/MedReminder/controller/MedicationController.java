package com.waritctd.MedReminder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waritctd.MedReminder.model.Medication;
import com.waritctd.MedReminder.model.User;
import com.waritctd.MedReminder.repository.MedicationRepository;
import com.waritctd.MedReminder.service.MedicationService;
import com.waritctd.MedReminder.service.TwilioService;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

	private final MedicationService medicationService;
	private final MedicationRepository medicationRepository;
	private final TwilioService twilioService;
	
	@Autowired
	public MedicationController(MedicationService medicationService, MedicationRepository medicationRepository) {
		super();
		this.medicationService = medicationService;
		this.medicationRepository = medicationRepository;
		this.twilioService = new TwilioService();
	}
	
	@GetMapping
	public ResponseEntity<List<Medication>> getAllMedications() {
		return ResponseEntity.ok(medicationRepository.findAll());
	}
	
    // Get medications for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Medication>> getMedicationsByUser(@PathVariable Long userId) {
        List<Medication> medications = medicationRepository.findByUserId(userId);
        return ResponseEntity.ok(medications);
    }

    // Add a new medication
    @PostMapping
    public ResponseEntity<Medication> addMedication(@RequestBody Medication medication) {
        Medication savedMedication = medicationRepository.save(medication);
        return ResponseEntity.ok(savedMedication);
    }

    // Update an existing medication
    @PutMapping("/{id}")
    public ResponseEntity<Medication> updateMedication(
            @PathVariable Long id, @RequestBody Medication updatedMedication) {
        return medicationRepository.findById(id)
                .map(medication -> {
                    medication.setName(updatedMedication.getName());
                    medication.setTime(updatedMedication.getTime());
                    medication.setUser(updatedMedication.getUser());
                    Medication savedMedication = medicationRepository.save(medication);
                    return ResponseEntity.ok(savedMedication);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a medication
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@PathVariable Long id) {
        medicationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // set reminder by time
    @PostMapping("/reminders/{time}")
    public ResponseEntity<String> sendReminders(@PathVariable String time) {
        medicationService.sendMedicationReminders(time);
        return ResponseEntity.ok("Reminders sent for time: " + time);
    }
    
    // for testing to send the sms without timing it
    @PostMapping("/send-medication-sms")
    public ResponseEntity<String> sendMedicationSms(@RequestParam Long userId) {
        List<Medication> medications = medicationRepository.findByUserId(userId);
        if (medications.isEmpty()) {
            return ResponseEntity.badRequest().body("No medications found for user with ID: " + userId);
        }

        User user = medications.get(0).getUser();  // Assuming the user exists in the first medication
        StringBuilder medicationMessage = new StringBuilder("Your medication reminders: \n");
        medications.forEach(medication -> medicationMessage.append(medication.getName()).append(" at ").append(medication.getTime()).append("\n"));

        twilioService.sendSms(user.getPhoneNumber(), medicationMessage.toString());

        return ResponseEntity.ok("Medication reminders sent to: " + user.getPhoneNumber());
    }

}
	
	
	
