package com.yasia.batch.processor;


import org.springframework.batch.item.ItemProcessor;

import com.yasia.batch.model.Employee;
import com.yasia.batch.model.Student;

import lombok.extern.java.Log;

@Log
public class Student2EmployeeItemProcessor implements ItemProcessor<Student, Employee>{@Override
	
	public Employee process(final Student stu) throws Exception {
		log.info("employee item processor invoked...");
		if(null!=stu) {
			Employee employee = new Employee();
			employee.setName(stu.getName());
			employee.setAge(stu.getAge());
			return employee;
		}
		return null;
	}

}
