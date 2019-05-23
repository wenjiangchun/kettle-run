package com.haze.system.exception;

import com.haze.core.service.HazeServiceException;

/**
 * 用户登录名已存在异常
 * 
 * @author sofar
 *
 */
public class UserLoginNameExistException extends HazeServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserLoginNameExistException() {
		super();
	}

	public UserLoginNameExistException(String message) {
		super(message);
	}

}
