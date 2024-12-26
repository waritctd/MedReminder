package com.waritctd.MedReminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.waritctd.MedReminder.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
