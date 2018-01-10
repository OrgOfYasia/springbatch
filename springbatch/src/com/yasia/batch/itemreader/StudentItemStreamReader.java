package com.yasia.batch.itemreader;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.yasia.batch.model.ReaderResponse;
import com.yasia.batch.model.Student;
import com.yasia.batch.repository.StudentRepository;
import com.yasia.batch.service.StudentService;

public class StudentItemStreamReader implements ItemStreamReader<List<Student>>{

	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	StudentService studentService;
	
	private int page;
	
	private ReaderResponse response = null;
	@Value("${spring.batch.read.size:100}")
	private int pageSize;
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("open..."+this.page);
		if(executionContext.containsKey("curPage")) {
			this.page = executionContext.getInt("curPage");
		}else {
			this.page = 0;
			executionContext.put("curPage", this.page);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		System.out.println("update..."+this.page);
		executionContext.put("curPage", this.page);
		
	}

	@Override
	public void close() throws ItemStreamException {}

	@Override
	public List<Student> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(-1==this.page) return null;
		if(null==response || response.hasNext()){
			response = fetchStudentData();
			this.page++;
			return response.getStudents();
		}
		this.page = -1;
		response = null;
		return null;
	}
	
	private ReaderResponse fetchStudentData() {
		return studentService.findStudentsWithPagination(this.page, this.pageSize);
	}
}
