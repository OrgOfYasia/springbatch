package com.yasia.batch.exception;

public class InvalidDataException extends Throwable {

	private static final long serialVersionUID = -8041394555513889198L;

	private static volatile InvalidDataException invalidDataException;
	private InvalidDataException() {}
	
	public static synchronized InvalidDataException newInstance() {
		if(null==invalidDataException) {
			invalidDataException = new InvalidDataException();
		}
		return invalidDataException;
	}
}
