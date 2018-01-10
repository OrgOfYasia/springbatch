package com.yasia.batch.fieldsetmapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.yasia.batch.model.Employee;

/**
 * 常用于文件列名与对象属性名不统一，或者数据类型转换失败的情况
 * @author LinYunZhi
 * @since 2018-01-07 23:17:51
 */
public class EmployeeFieldSetMapper implements FieldSetMapper<Employee>{

	@Override
	public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
		Employee employee = new Employee();
		employee.setName(fieldSet.readString("name"));
		employee.setAge(fieldSet.readInt("age"));
		employee.setHireType(fieldSet.readInt("hireType"));
		employee.setHireDate(fieldSet.readDate("hireDate"));
		
		return employee;
	}

}
