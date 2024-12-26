package com.waritctd.MedReminder.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.waritctd.MedReminder.model.Medication;
import com.waritctd.MedReminder.model.User;
import com.waritctd.MedReminder.repository.MedicationRepository;
import com.waritctd.MedReminder.service.MedicationService;
import com.waritctd.MedReminder.service.TwilioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private TwilioService twilioService;

    @InjectMocks
    private MedicationService medicationService;

    private User user;
    private Medication medication1, medication2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setPhoneNumber("1234567890");

        medication1 = new Medication();
        medication1.setId(1L);
        medication1.setName("Medicine A");
        medication1.setTime("09:00");
        medication1.setUser(user);

        medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("Medicine B");
        medication2.setTime("09:00");
        medication2.setUser(user);
    }

    @Test
    void testGetMedicationsForTime() {
        when(medicationRepository.findAll()).thenReturn(Arrays.asList(medication1, medication2));

        List<Medication> medications = medicationService.getMedicationsForTime("09:00");

        assertNotNull(medications);
        assertEquals(2, medications.size());
        assertTrue(medications.contains(medication1));
        assertTrue(medications.contains(medication2));

        verify(medicationRepository).findAll();
    }

    @Test
    void testSendMedicationReminders() {
        when(medicationRepository.findAll()).thenReturn(Arrays.asList(medication1, medication2));

        medicationService.sendMedicationReminders("09:00");

        String expectedMessage = "Reminder: You need to take the following medications at 09:00: Medicine A, Medicine B";
        verify(twilioService).sendSms(user.getPhoneNumber(), expectedMessage);

        verify(twilioService, times(1)).sendSms(eq(user.getPhoneNumber()), eq(expectedMessage));
    }
}
