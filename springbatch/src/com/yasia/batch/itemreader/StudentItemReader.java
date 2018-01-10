package com.yasia.batch.itemreader;

import java.util.List;

import org.springframework.batch.item.ItemReader;

import com.yasia.batch.model.Student;
import com.yasia.batch.repository.StudentRepository;

public class StudentItemReader implements ItemReader<Student>{

	private List<Student> list;

	/**
	 * reader下不能通过@Autowired StudentRepository studentRepository装配,
	 * 只能通过传参的方式
	 * @param studentRepository
	 */
	public StudentItemReader(StudentRepository studentRepository) {
		this.list = studentRepository.findAll();
	}

    @Override
	public Student read() {
		if (!list.isEmpty()) {
			return list.remove(0);
		}
		return null;
	}
	
}
