package com.msoft.mbi.cube.exception;

public class UncheckedCubeException extends Exception implements CubeException {

	public UncheckedCubeException() {
		super();
	}

	public UncheckedCubeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UncheckedCubeException(String message) {
		super(message);
	}

	public UncheckedCubeException(Throwable cause) {
		super(cause);
	}

}
