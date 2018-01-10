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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.yasia.batch.listener.JobCompletionNotificationListener;
import com.yasia.batch.model.Employee;
import com.yasia.batch.processor.EmployeeItemProcessor;

/**
 * Demo10
 * 文件读取，写入数据库——自定义对象属性映射类EmployeeFieldSetMapper
 * 对比spring体统的属性映射实现FlatFileBatchConfiguration
 * @author LinYunZhi
 * @since 2018-01-07 22:36:48
 */
//@Configuration
//@EnableBatchProcessing//入口类加了，这里不再需要
public class XmlFileBatchConfiguration {
	
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
    public Job xmlJob(JobCompletionNotificationListener listener) {
    	return this.jobBuilderFactory.get("xmlJob")
    			.incrementer(new RunIdIncrementer())
    			.listener(listener)
    			.start(chunkBasedStep())
    			.build();
    }
    
    @Bean public Step chunkBasedStep() {
    	return this.stepBuilderFactory.get("chunkBasedStep")
    			.<Employee, Employee>chunk(1)
    			.reader(fileItemReader())
    			.processor(processor())
    			.writer(writer())
    			//.writer(list->list.forEach(System.err::println))
    			.allowStartIfComplete(true)
    			.build();
    }
    
    @Bean public ItemReader<Employee> fileItemReader(){
    	StaxEventItemReader<Employee> reader = new StaxEventItemReader<Employee>();
    	reader.setResource(new ClassPathResource("data/data.xml"));
    	//reader.setLinesToSkip(1);//跳过1行
    	reader.setFragmentRootElementName("employee");//每一块数据的根节点
    	//xml格式数据转成对象
    	//reader.setUnmarshaller(this.createMarshallerViaXStream());
    	reader.setUnmarshaller(this.createUnmarshallerViaJaxb());
		return reader;
    }
    
    /*
     * JAXB允许开发人员将java类映射为xml表示方式(Java Architecture for XML Binding)
     */
    private Jaxb2Marshaller createUnmarshallerViaJaxb() {
    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    	marshaller.setClassesToBeBound(Employee.class);
    	return marshaller;
    }
    
    /**
     * 不建议使用XStreamMarshaller<br>
     * yyyy-MM-dd格式日期不支持，只支持以下格式的日期转换
     * <p><code>
     	Default date pattern: yyyy-MM-dd HH:mm:ss.S z<br>
		Default era date pattern: yyyy-MM-dd G HH:mm:ss.S z<br>
		Alternative date pattern: yyyy-MM-dd HH:mm:ss.S z<br>
		Alternative date pattern[1]: yyyy-MM-dd HH:mm:ss.S a<br>
		Alternative date pattern[2]: yyyy-MM-dd HH:mm:ssz<br>
		Alternative date pattern[3]: yyyy-MM-dd HH:mm:ss z<br>
		Alternative date pattern[4]: yyyy-MM-dd HH:mm:ssa</code></p>
     * @return
     */
    @SuppressWarnings("unused")
	private XStreamMarshaller createMarshallerViaXStream() {
    	XStreamMarshaller marshaller = new XStreamMarshaller();
    	Map<String, Class<Employee>> aliases = new HashMap<String, Class<Employee>>();
    	aliases.put("employee", Employee.class);
    	marshaller.setAliases(aliases);
    	return marshaller;
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
    
}
