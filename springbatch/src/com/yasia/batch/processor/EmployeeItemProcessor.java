package com.yasia.batch.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.item.ItemProcessor;

import com.yasia.batch.model.Employee;
import com.yasia.framework.core.utils.DateUtil;

import lombok.extern.java.Log;

@Log
public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee>{@Override
	
	public Employee process(final Employee employee) throws Exception {
		log.info("employee item processor invoked...");
		if(null!=employee) {
			if(null!=employee.getHireType() || employee.getHireType()>0) {//非临时工
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String becomeARegularDate = DateUtil.getAfterMonthDate(sdf.format(employee.getHireDate()), "-", 3);
				if(DateUtil.compareDate(sdf.format(new Date()), becomeARegularDate)<2) {
					//试用期结束，转正
					employee.setIsProbation(1);
					employee.setName(employee.getName().toUpperCase());
				}
			}
		}
		return employee;
	}

	
}
