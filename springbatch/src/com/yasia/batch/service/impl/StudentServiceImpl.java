package com.yasia.batch.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.yasia.batch.model.ReaderResponse;
import com.yasia.batch.model.Student;
import com.yasia.batch.repository.StudentRepository;
import com.yasia.batch.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	StudentRepository studentRepository;

	@Override
	public ReaderResponse findStudentsWithPagination(int page, int pageSize) {
		ReaderResponse response = new ReaderResponse();
		Pageable pageable = new PageRequest(page, pageSize, new Sort(Sort.Direction.ASC, "id"));
		Page<Student> stuPage = studentRepository.findAll(pageable);
		
		response.setStudents(stuPage.getContent());
		response.setHasNext(stuPage.hasNext());
		return response;
	}
	
	
}
