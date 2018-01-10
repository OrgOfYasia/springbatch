package com.yasia.batch.configuration;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yasia.batch.listener.JobCompletionNotificationListener;
import com.yasia.batch.model.Employee;
import com.yasia.batch.model.Student;
import com.yasia.batch.processor.Student2EmployeeItemProcessor;
import com.yasia.batch.repository.EmployeeRepository;
import com.yasia.batch.repository.StudentRepository;

/**
 * Demo15
 * 读取数据库，用jpa写入数据库——自定义对象属性映射类EmployeeFieldSetMapper
 * 对比spring体统的属性映射实现FlatFileBatchConfiguration
 * @author LinYunZhi
 * @since 2018-01-07 22:36:48
 */
//@Configuration
public class ReadFromDBJpaWriterBatchConfiguration {
	
	@Value("${spring.batch.chunk.size:5}")//默认5条数据
	private int chunkSize;

	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    EmployeeRepository employeeRepository;
    
    @Autowired
    EntityManagerFactory entityManagerFactory;
    
    @Autowired
    StudentRepository studentRepository;
    
    @Bean
    public Job jdbcReaderJob(JobCompletionNotificationListener listener) throws Exception {
    	return this.jobBuilderFactory.get("jdbcReaderJob")
    			.incrementer(new RunIdIncrementer())
    			.listener(listener)
    			.start(chunkBasedStep())
    			.build();
    }
    
    @Bean public Step chunkBasedStep() throws Exception {
    	return this.stepBuilderFactory.get("chunkBasedStep")
    			.<Student, Employee>chunk(1)
    			.reader(reader())
    			.processor(processor())
    			.writer(jpaWriter())
    			.allowStartIfComplete(true)
    			.build();
    }
    
    @Bean ListItemReader<Student> reader(){
		return new ListItemReader<>(studentRepository.findAll());
	}
    
    @Bean
    public Student2EmployeeItemProcessor processor() {
    	System.out.println("processor invoked...");
    	return new Student2EmployeeItemProcessor();
    }
    
    @Bean
    public ItemWriter<Employee> jpaWriter() throws Exception {
    	JpaItemWriter<Employee> writer = new JpaItemWriter<Employee>();
    	writer.setEntityManagerFactory(this.entityManagerFactory);
        writer.afterPropertiesSet();
        return writer;
    }
    
}
