package com.yasia.batch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yasia.batch.model.Student;
import com.yasia.batch.repository.StudentRepository;

@Controller
@RequestMapping("/stu")
public class StudentController {
	
	@Autowired
	private StudentRepository studentRepository;
	
	@RequestMapping(value="/all", method = {RequestMethod.GET, RequestMethod.POST})
	public List<Student> findAllStu() {
		
		return studentRepository.findAll();
		
	}

}
