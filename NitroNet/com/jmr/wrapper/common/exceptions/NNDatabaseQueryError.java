package com.jmr.wrapper.common.exceptions;

/**
 * Networking Library
 * NEDatabaseQueryError.java
 * Purpose: Thrown when trying to run a query but an SQLException is thrown.
 *
 * @author Jon R (Baseball435)
 * @version 1.0 7/19/2014
 */

@SuppressWarnings("serial")
public class NNDatabaseQueryError extends Exception {
	
	public NNDatabaseQueryError(String message) {
		super("Can not run the query. " + message);
	}
	
}
