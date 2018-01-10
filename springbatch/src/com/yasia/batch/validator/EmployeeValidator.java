package com.yasia.batch.validator;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import com.yasia.batch.model.Employee;

import lombok.extern.java.Log;

@Log
public class EmployeeValidator implements Validator<Employee>{

	@Override
	public void validate(Employee employee) throws ValidationException {
		if(employee.getAge()>20) {//过滤掉大于20岁的
			String errorMsg = String.format("%s is groupup.", employee.getName());
			log.log(java.util.logging.Level.WARNING, errorMsg);
			throw new ValidationException(errorMsg);
		}
	}

}
