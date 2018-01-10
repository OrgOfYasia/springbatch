package com.yasia.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.yasia.batch.model.Student;
import com.yasia.batch.repository.StudentRepository;

/**
 * Demo14
 * @author LinYunZhi
 * @since 2018-01-09 00:18:30
 */
//@Configuration
public class Jpa2XmlFileBatchConfig {

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Bean ListItemReader<Student> reader(){
		return new ListItemReader<>(studentRepository.findAll());
	}
	
	@Bean Step jpa2XmlFileStep() throws Exception {
		return this.stepBuilderFactory.get("jpa2XmlFileStep")
		.<Student, Student>chunk(5)
		//.reader(reader())
		//.processor(processor())
		//.writer(db2XmlFileWriter())
		.allowStartIfComplete(true)
		.build();
	}
	
	@Bean public ItemWriter<Student> db2XmlFileWriter() throws Exception{
		StaxEventItemWriter<Student> writer = new StaxEventItemWriter<>();
		writer.setRootTagName("tagName");
		writer.setMarshaller(this.createMarshallerViaXStream());
		//writer.setMarshaller(this.createMarshallerViaJaxb());
		String workFolder = System.getProperty("user.dir");
		writer.setResource(new FileSystemResource(workFolder.concat("/output/data.xml")));
		writer.afterPropertiesSet();//用于校验必须初始化的属性
		System.err.println("writer to file: "+workFolder.concat("/output/data.xml"));
		return writer;
		
	}
	
	private Jaxb2Marshaller createMarshallerViaJaxb() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Student.class);
		return marshaller;
	}
	
	private XStreamMarshaller createMarshallerViaXStream() {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class<Student>> aliases = new HashMap<String, Class<Student>>();
		aliases.put("student", Student.class);
		marshaller.setAliases(aliases);
		return marshaller;
	}
	
	@Bean Job jpaXml2FileJob() throws Exception {
		return this.jobBuilderFactory.get("jpa2XmlFileJob")
				.start(jpa2XmlFileStep())
				.build();
	}
}
