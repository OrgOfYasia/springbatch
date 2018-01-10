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
	
	@RequestMapping(value="/all", method = RequestMethod.GET)
	public List<Student> findAllStu() {
		System.out.println("going to fetch student list...1");
		System.out.println("going to fetch student list...2");
		System.out.println("going to fetch student list...3");
		System.out.println("going to fetch student list...4");
		System.out.println("going to fetch student list...5");
		System.out.println("going to fetch student list...6");
		return studentRepository.findAll();
		
	}

}
