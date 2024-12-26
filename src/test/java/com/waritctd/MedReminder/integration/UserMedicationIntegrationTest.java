package com.waritctd.MedReminder.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waritctd.MedReminder.model.Medication;
import com.waritctd.MedReminder.model.User;
import com.waritctd.MedReminder.repository.MedicationRepository;
import com.waritctd.MedReminder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserMedicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Medication medication;

    @BeforeEach
    void setUp() {
        // Clean the repositories before each test to ensure a clean slate
        medicationRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save a user
        user = new User();
        user.setName("John Doe");
        user.setPhoneNumber("+15558675310");
        user = userRepository.save(user);

        // Create and save a medication for the user
        medication = new Medication();
        medication.setName("Aspirin");
        medication.setTime("09:00 AM");
        medication.setUser(user);
        medicationRepository.save(medication);
    }

    @Test
    void testCreateUserAndMedication() throws Exception {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setPhoneNumber("+15558765432");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("+15558765432"));

        Medication newMedication = new Medication();
        newMedication.setName("Ibuprofen");
        newMedication.setTime("02:00 PM");
        newMedication.setUser(user);

        mockMvc.perform(post("/api/medications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMedication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ibuprofen"))
                .andExpect(jsonPath("$.time").value("02:00 PM"));
    }

    @Test
    void testGetUserMedications() throws Exception {
        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("+15558675310"));

        mockMvc.perform(get("/api/medications/user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aspirin"))
                .andExpect(jsonPath("$[0].time").value("09:00 AM"));
    }

    @Test
    void testUpdateUserAndMedication() throws Exception {
        user.setName("John Updated");
        user.setPhoneNumber("+15558987654");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.phoneNumber").value("+15558987654"));

        medication.setName("Paracetamol");
        medication.setTime("10:00 AM");

        mockMvc.perform(put("/api/medications/{id}", medication.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.time").value("10:00 AM"));
    }

    @Test
    void testDeleteUserAndMedication() throws Exception {
        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/medications/user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aspirin"));

        mockMvc.perform(delete("/api/medications/{id}", medication.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/medications/user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isNotFound());
    }


    @Test
    void testSendMedicationReminders() throws Exception {
        mockMvc.perform(post("/api/medications/reminders/{time}", "09:00 AM"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reminders sent for time: 09:00 AM"));
    }
}
