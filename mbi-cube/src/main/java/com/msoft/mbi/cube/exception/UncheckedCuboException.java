package com.msoft.mbi.cube.exception;

public class UncheckedCuboException extends Exception implements CuboException {

	private static final long serialVersionUID = -7447697379268552831L;

	public UncheckedCuboException() {
		super();
	}

	public UncheckedCuboException(String message, Throwable cause) {
		super(message, cause);
	}

	public UncheckedCuboException(String message) {
		super(message);
	}

	public UncheckedCuboException(Throwable cause) {
		super(cause);
	}

}
