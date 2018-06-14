/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.print;

/**
 * This is an Exception class and is used to encapsulate all errors thrown
 * during the the execution of activities related to report printing.
 */
public class PrintingException extends RuntimeException {

	public PrintingException() {
		super();
	}
	
	/**
	 * Constructor for passing exception along with message
	 * @param message relating to circumstances due to which exception occurred
	 * @param t {@link Throwable} exception object with trace
	 */
	public PrintingException(String message, Throwable t) {
		super(message, t);
	}

	public PrintingException(Throwable t) {
		super(t);
	}
}
