package com.yasia.batch.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.yasia.batch.model.Employee;

import lombok.extern.java.Log;

@Log
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport{

	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			List<Employee> results = jdbcTemplate.query("SELECT name, age FROM Employee", new RowMapper<Employee>() {
				@Override
				public Employee mapRow(ResultSet rs, int row) throws SQLException {
					return new Employee().setName(rs.getString(1)).setAge(Integer.valueOf(rs.getString(2)));
				}
			});

			for (Employee emplyee : results) {
				log.info("Found <" + emplyee + "> in the database.");
			}

		}
	}
	
}
