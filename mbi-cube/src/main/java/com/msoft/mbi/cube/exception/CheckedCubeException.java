package com.msoft.mbi.cube.exception;

public class CheckedCubeException extends RuntimeException implements CubeException {

	public CheckedCubeException() {
		super();
	}

	public CheckedCubeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckedCubeException(String message) {
		super(message);
	}

	public CheckedCubeException(Throwable cause) {
		super(cause);
	}

}
