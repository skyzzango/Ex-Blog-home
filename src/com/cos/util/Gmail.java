package com.cos.util;

import com.cos.dao.SecretDAO;
import com.cos.dto.SecretVO;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Gmail extends Authenticator{
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		SecretVO secret = new SecretDAO().getSecret("google");
		return new PasswordAuthentication(secret.getId(), secret.getPassword());		
	}
}
