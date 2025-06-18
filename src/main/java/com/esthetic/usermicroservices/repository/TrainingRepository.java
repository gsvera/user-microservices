package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Long> {
}
