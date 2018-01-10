package com.yasia.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.yasia.batch.fieldsetmapper.EmployeeRowMapper;
import com.yasia.batch.listener.JobCompletionNotificationListener;
import com.yasia.batch.model.Employee;
import com.yasia.batch.processor.EmployeeItemProcessor;

/**
 * Demo11
 * 读取数据库，写入数据库——自定义对象属性映射类EmployeeFieldSetMapper
 * 对比spring体统的属性映射实现FlatFileBatchConfiguration
 * @author LinYunZhi
 * @since 2018-01-07 22:36:48
 */
//@Configuration
public class ReadFromDBBatchConfiguration {
	
	@Value("${spring.batch.chunk.size:5}")//默认5条数据
	private int chunkSize;

	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    //@Qualifier("dataSource")
    public DataSource dataSource;
    
    @Bean
    public Job jdbcReaderJob(JobCompletionNotificationListener listener) {
    	return this.jobBuilderFactory.get("jdbcReaderJob")
    			.incrementer(new RunIdIncrementer())
    			.listener(listener)
    			.start(chunkBasedStep())
    			.build();
    }
    
    @Bean public Step chunkBasedStep() {
    	return this.stepBuilderFactory.get("chunkBasedStep")
    			.<Employee, Employee>chunk(1)
    			.reader(jdbcPagingItemReader())
    			//.processor(processor())
    			.writer(writer())
    			//.writer(list->list.forEach(System.err::println))
    			.allowStartIfComplete(true)
    			.build();
    }
    
    @Bean public ItemReader<Employee> jdbcPagingItemReader(){
    	JdbcPagingItemReader<Employee> reader = new JdbcPagingItemReader<Employee>();
    	//JdbcCursorItemReader<Employee> cursorReader = new JdbcCursorItemReader<Employee>();
    	reader.setDataSource(dataSource);
    	reader.setFetchSize(20);
    	reader.setRowMapper(new EmployeeRowMapper());
    	MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
    	queryProvider.setSelectClause("*");
    	queryProvider.setFromClause("from student");
    	
    	Map<String, Order> sortKeys = new HashMap<String, Order>(1);
    	sortKeys.put("id", Order.ASCENDING);//设置排序，防止读出脏数据(默认随机读取)
    	queryProvider.setSortKeys(sortKeys);
    	reader.setQueryProvider(queryProvider);
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
        writer.afterPropertiesSet();
        return writer;
    }
    
}
