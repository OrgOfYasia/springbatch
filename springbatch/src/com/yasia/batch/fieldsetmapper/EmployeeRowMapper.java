package com.yasia.batch.fieldsetmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.yasia.batch.model.Employee;

/**
 * 常用于文件列名与对象属性名不统一，或者数据类型转换失败的情况
 * @author LinYunZhi
 * @since 2018-01-07 23:17:51
 */
public class EmployeeRowMapper implements RowMapper<Employee>{

	@Override
	public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
		Employee employee = new Employee();
		employee.setName(rs.getString("name"));
		employee.setAge(rs.getInt("age"));
		
		return employee;
	}

}
