package com.yasia.batch.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.yasia.batch.fieldsetmapper.EmployeeFieldSetMapper;
import com.yasia.batch.listener.JobCompletionNotificationListener;
import com.yasia.batch.model.Employee;
import com.yasia.batch.processor.EmployeeItemProcessor;
import com.yasia.batch.validator.EmployeeValidator;

/**
 * Demo9
 * 文件读取，写入数据库——自定义对象属性映射类EmployeeFieldSetMapper
 * 对比spring体统的属性映射实现FlatFileBatchConfiguration
 * @author LinYunZhi
 * @since 2018-01-07 22:36:48
 */
//@Configuration
//@EnableBatchProcessing//入口类加了，这里不再需要
public class SimpleFlatFileBatchConfiguration {

	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    //@Qualifier("dataSource")
    public DataSource dataSource;
    
    @Bean
    public Job fileReaderJob(JobCompletionNotificationListener listener) throws Exception {
    	return this.jobBuilderFactory.get("fileReaderJob")
    			.incrementer(new RunIdIncrementer())
    			.listener(listener)
    			.start(chunkBasedStep())
    			.build();
    }
    
    @Bean public Step chunkBasedStep() throws Exception {
    	return this.stepBuilderFactory.get("chunkBasedStep")
    			.<Employee, Employee>chunk(1)
    			.reader(fileItemReader())
    			.processor(itemProcessor())
    			.writer(writer())
    			//.writer(list->list.forEach(System.err::println))
    			.allowStartIfComplete(true)
    			.build();
    }
    
    @Bean public ItemReader<Employee> fileItemReader(){
    	FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
    	reader.setResource(new ClassPathResource("data.txt"));
    	reader.setLinesToSkip(1);//跳过1行
    	reader.setLineMapper(this.createLineMapper());
		return reader;
    }
    
    private LineMapper<Employee> createLineMapper() {
    	DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
    	lineMapper.setLineTokenizer(this.createLineTokenizer());
    	lineMapper.setFieldSetMapper(new EmployeeFieldSetMapper());
    	lineMapper.afterPropertiesSet();
    	return lineMapper;
    }
    
    private DelimitedLineTokenizer createLineTokenizer() {
    	DelimitedLineTokenizer dlt = new DelimitedLineTokenizer();
    	//dlt.setDelimiter(";");//設置分隔符
    	dlt.setNames(new String[] { "name", "age","hireType","hireDate"});
        return dlt;
    }
    
    @Bean public ItemProcessor<Employee, Employee> itemProcessor() throws Exception {
    	List<ItemProcessor<Employee, Employee>> delegates = new ArrayList<>();
		ValidatingItemProcessor<Employee> vip = new ValidatingItemProcessor<>(new EmployeeValidator());
		vip.setFilter(true);//把抛异常的数据过滤掉,如果false(默认)，则抛出异常，后面的工作不再继续
		
		delegates.add(new EmployeeItemProcessor());
		delegates.add(vip);
		
		CompositeItemProcessor<Employee, Employee> cip = new CompositeItemProcessor<>();
		cip.setDelegates(delegates);
		cip.afterPropertiesSet();
		return cip;
    }
    
    @Bean public EmployeeItemProcessor processor() {
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
    
}
