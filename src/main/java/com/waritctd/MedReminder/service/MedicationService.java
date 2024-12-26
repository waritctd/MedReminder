package com.waritctd.MedReminder.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.waritctd.MedReminder.model.Medication;
import com.waritctd.MedReminder.model.User;
import com.waritctd.MedReminder.repository.MedicationRepository;

@Service
public class MedicationService {
    private final MedicationRepository medicationRepository;
    private final TwilioService twilioService;
    
    @Autowired
    public MedicationService(MedicationRepository medicationRepository, TwilioService twilioService) {
		super();
		this.medicationRepository = medicationRepository;
		this.twilioService = twilioService;
	}

	public List<Medication> getMedicationsForTime(String time) {
        return medicationRepository.findAll().stream()
                .filter(med -> med.getTime().equals(time))
                .collect(Collectors.toList());
    }

    public void sendMedicationReminders(String time) {
        // Fetch medications for the specified time
        List<Medication> medications = getMedicationsForTime(time);

        // Group medications by user
        Map<User, List<Medication>> userMedicationsMap = medications.stream()
                .collect(Collectors.groupingBy(Medication::getUser));

        // Send SMS reminders for each user
        for (Map.Entry<User, List<Medication>> entry : userMedicationsMap.entrySet()) {
            User user = entry.getKey();
            List<Medication> userMedications = entry.getValue();

            // Construct a message listing all medications
            String message = "Reminder: You need to take the following medications at " + time + ": " +
                    userMedications.stream()
                            .map(Medication::getName)
                            .collect(Collectors.joining(", "));

            twilioService.sendSms(user.getPhoneNumber(), message);
        }
    }
}
