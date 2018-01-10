package com.yasia.batch.itemwriter;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.yasia.batch.model.Employee;
import com.yasia.batch.repository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmployeeItemWriter implements ItemWriter<Employee>{

	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	public void write(List<? extends Employee> items) throws Exception {
		if(null!=items && !items.isEmpty()) {
			employeeRepository.save(items);
			log.info("writer finished...");
		}else {
			log.info("no data for writer...");
		}
	}


}
