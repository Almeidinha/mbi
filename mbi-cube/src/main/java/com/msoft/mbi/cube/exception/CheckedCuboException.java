package com.msoft.mbi.cube.exception;

import java.io.Serial;

public class CheckedCuboException extends RuntimeException implements CuboException {

	@Serial
	private static final long serialVersionUID = 6546925147624599723L;

	public CheckedCuboException() {
		super();
	}

	public CheckedCuboException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckedCuboException(String message) {
		super(message);
	}

	public CheckedCuboException(Throwable cause) {
		super(cause);
	}

}
