package com.yasia.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yasia.batch.model.Student;

public interface StudentRepository extends JpaSpecificationExecutor<Student>, JpaRepository<Student, Long> {

}
