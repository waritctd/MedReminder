package com.waritctd.MedReminder.repository;

import com.waritctd.MedReminder.model.Medication;
import com.waritctd.MedReminder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional 
public class MedicationRepositoryTest {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setPhoneNumber("+15558675310");
        userRepository.save(testUser);

        Medication medication1 = new Medication();
        medication1.setName("Aspirin");
        medication1.setTime("08:00");
        medication1.setUser(testUser);

        Medication medication2 = new Medication();
        medication2.setName("Paracetamol");
        medication2.setTime("09:00");
        medication2.setUser(testUser);

        medicationRepository.save(medication1);
        medicationRepository.save(medication2);
    }

    @Test
    void testFindByUserId() {
        List<Medication> medications = medicationRepository.findByUserId(testUser.getId());

        assertNotNull(medications);
        assertEquals(2, medications.size());
        assertTrue(medications.stream().anyMatch(med -> med.getName().equals("Aspirin")));
        assertTrue(medications.stream().anyMatch(med -> med.getName().equals("Paracetamol")));
    }

    @Test
    void testFindByUserId_NoMedications() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setPhoneNumber("+15558675311");
        userRepository.save(newUser);

        List<Medication> medications = medicationRepository.findByUserId(newUser.getId());

        //Verify no medications are found
        assertNotNull(medications);
        assertTrue(medications.isEmpty());
    }
}
