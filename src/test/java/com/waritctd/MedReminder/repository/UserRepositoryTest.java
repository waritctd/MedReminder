package com.waritctd.MedReminder.repository;

import com.waritctd.MedReminder.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional 
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setPhoneNumber("+15558675310");
        userRepository.save(testUser); 
    }

    @Test
    void testSaveUser() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setPhoneNumber("+15558675311");
        
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("Jane Doe", savedUser.getName());
        assertEquals("+15558675311", savedUser.getPhoneNumber());
    }

    @Test
    void testFindById() {
        Optional<User> foundUser = userRepository.findById(testUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getName(), foundUser.get().getName());
        assertEquals(testUser.getPhoneNumber(), foundUser.get().getPhoneNumber());
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(testUser);

        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testFindAllUsers() {
        User anotherUser = new User();
        anotherUser.setName("Alice");
        anotherUser.setPhoneNumber("+15558675312");
        userRepository.save(anotherUser);

        long countBefore = userRepository.count();
        assertEquals(2, countBefore); 

        assertTrue(userRepository.findAll().size() > 0);
    }
}
