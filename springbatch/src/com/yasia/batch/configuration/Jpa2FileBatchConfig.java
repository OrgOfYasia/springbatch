package com.yasia.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.yasia.batch.model.Student;
import com.yasia.batch.repository.StudentRepository;

/**
 * Demo13
 * @author LinYunZhi
 * @since 2018-01-09 00:18:30
 */
//@Configuration
public class Jpa2FileBatchConfig {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Bean ListItemReader<Student> reader(){
		return new ListItemReader<>(studentRepository.findAll());
	}
	
	@Bean Step jpa2FileStep() throws Exception {
		return this.stepBuilderFactory.get("jpa2FileStep")
		.<Student, Student>chunk(5)
		.reader(reader())
		//.processor(processor())
		//.writer(db2TxtFileWriter())
		.allowStartIfComplete(true)
		.build();
	}
	
	@Bean public ItemWriter<Student> db2TxtFileWriter() throws Exception{
		FlatFileItemWriter<Student> writer = new FlatFileItemWriter<Student>();
		writer.setLineAggregator(new StudentAggregator());//setLineAggregator是必须的
		writer.setLineSeparator("\t\n");
		String workFolder = System.getProperty("user.dir");
		writer.setResource(new FileSystemResource(workFolder.concat("/output/data.txt")));
		writer.afterPropertiesSet();//用于校验必须初始化的属性
		System.err.println("writer to file: "+workFolder.concat("/output/data.txt"));
		return writer;
		
	}
	
	public class StudentAggregator implements LineAggregator<Student>{

		@Override
		public String aggregate(Student item) {
			return item.getId()+","+item.getName()+","+item.getAge()
			+","+item.getGrade()+","+item.getCourses()+","+item.getScore()
			+","+item.getMemo();
		}
		
	}
	
	@Bean Job jpa2FileJob() throws Exception {
		return this.jobBuilderFactory.get("jpa2FileJob")
				.start(jpa2FileStep())
				.build();
	}
}
