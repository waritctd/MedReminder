package com.waritctd.MedReminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waritctd.MedReminder.model.Medication;
import com.waritctd.MedReminder.model.User;
import com.waritctd.MedReminder.repository.MedicationRepository;
import com.waritctd.MedReminder.service.MedicationService;
import com.waritctd.MedReminder.service.TwilioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicationController.class)
public class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationRepository medicationRepository;

    @MockBean
    private MedicationService medicationService;
    
    @MockBean
    private TwilioService twilioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Medication medication;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setPhoneNumber("+15558675310");

        medication = new Medication();
        medication.setId(1L);
        medication.setName("Aspirin");
        medication.setTime("08:00");
        medication.setUser(user);
    }

    @Test
    void testGetAllMedications() throws Exception {
        when(medicationRepository.findAll()).thenReturn(Arrays.asList(medication));

        mockMvc.perform(get("/api/medications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aspirin"))
                .andExpect(jsonPath("$[0].time").value("08:00"));
    }

    @Test
    void testGetMedicationsByUser() throws Exception {
        when(medicationRepository.findByUserId(1L)).thenReturn(Arrays.asList(medication));

        mockMvc.perform(get("/api/medications/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aspirin"))
                .andExpect(jsonPath("$[0].time").value("08:00"));
    }

    @Test
    void testAddMedication() throws Exception {
        when(medicationRepository.save(Mockito.any(Medication.class))).thenReturn(medication);

        mockMvc.perform(post("/api/medications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aspirin"))
                .andExpect(jsonPath("$.time").value("08:00"));
    }

    @Test
    void testUpdateMedication() throws Exception {
        Medication updatedMedication = new Medication();
        updatedMedication.setName("Ibuprofen");
        updatedMedication.setTime("10:00");
        updatedMedication.setUser(user);

        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(Mockito.any(Medication.class))).thenReturn(updatedMedication);

        mockMvc.perform(put("/api/medications/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMedication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ibuprofen"))
                .andExpect(jsonPath("$.time").value("10:00"));
    }

    @Test
    void testDeleteMedication() throws Exception {
        doNothing().when(medicationRepository).deleteById(1L);

        mockMvc.perform(delete("/api/medications/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSendReminders() throws Exception {
        doNothing().when(medicationService).sendMedicationReminders("08:00");

        mockMvc.perform(post("/api/medications/reminders/{time}", "08:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reminders sent for time: 08:00"));
    }
    
    @Test
    void testSendMedicationSms() throws Exception {
        // Mock the repository to return a user and their medication
        when(medicationRepository.findByUserId(1L)).thenReturn(Arrays.asList(medication));
        
        // Mock the Twilio service to avoid actually sending SMS
        doNothing().when(twilioService).sendSms(anyString(), anyString());

        // Perform the POST request to send medication SMS
        mockMvc.perform(post("/api/medications/send-medication-sms?userId=1"))  // Corrected URL path
                .andExpect(status().isOk())
                .andExpect(content().string("Medication reminders sent to: +15558675310"));

        // Verify that the TwilioService sendSms method was called with the correct parameters
        verify(twilioService, times(1)).sendSms(eq("+15558675310"), contains("Your medication reminders:"));
    }


}
