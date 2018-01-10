package com.yasia.batch.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.yasia.batch.listener.JobCompletionNotificationListener;
import com.yasia.batch.model.Employee;
import com.yasia.batch.processor.EmployeeItemProcessor;

/**
 * Demo8
 * 文件读取，写入数据库——使用spring提供的对象属性映射类
 * @author LinYunZhi
 * @since 2018-01-07 22:36:48
 */
//@Configuration
//@EnableBatchProcessing//入口类加了，这里不再需要
public class FlatFileBatchConfiguration {

	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    //@Qualifier("dataSource")
    public DataSource dataSource;
    
    
    @Bean
    public FlatFileItemReader<Employee> reader() {
    	System.out.println("reader invoked...");
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
        reader.setResource(new ClassPathResource("data.txt"));
        reader.setLineMapper(new DefaultLineMapper<Employee>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
            	//setDelimiter(";");//设置数据的分隔符，默认是逗号
            	//名称必须跟数据格式顺序一致
                setNames(new String[] { "name", "age","hireType","hireDate"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
                setTargetType(Employee.class);
            }});
        }});
        System.out.println("return reader...");
        return reader;
    }
    
    @Bean
    public EmployeeItemProcessor processor() {
    	System.out.println("processor invoked...");
    	return new EmployeeItemProcessor();
    }
    
    @Bean
    public JdbcBatchItemWriter<Employee> writer() {
    	System.out.println("writer invoked...");
        JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<Employee>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
        writer.setSql("INSERT INTO employee (name, age, hireType, hireDate) VALUES (:name, :age, :hireType, :hireDate)");
        writer.setDataSource(dataSource);
        return writer;
    }
    
    @Bean
    public Job importEmployeeJob(JobCompletionNotificationListener listener) {
    	System.out.println("importEmployeeJob invoked...");
        return jobBuilderFactory.get("importEmployeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
    	System.out.println("step1 invoked...");
        return stepBuilderFactory.get("step1")
                .<Employee, Employee> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    
}
