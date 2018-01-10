package com.yasia.batch.model;

import java.util.List;

import lombok.Data;

@Data
public class ReaderResponse {

	private boolean hasNext;
	
	private List<Student> students;
	
	public boolean hasNext() {
		return this.hasNext;
	}
}
