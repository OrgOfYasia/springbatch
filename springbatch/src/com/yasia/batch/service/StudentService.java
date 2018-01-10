package com.yasia.batch.service;

import com.yasia.batch.model.ReaderResponse;

public interface StudentService {

	public ReaderResponse findStudentsWithPagination(int page, int pageSize);

}
