package com.jnovos;

import com.microsoft.alm.secret.Secret;

/**
 * Bean de credenciales
 * 
 * @author jnovos
 *
 */
public class Credencial extends Secret {

	private String username;
	private String password;
	private String alias;

	public Credencial(String username, String password, String alias) {
		this.username = username;
		this.password = password;
		this.alias = alias;
	}

	public Credencial(String username, String password) {
		this.username = username;
		this.password = password;

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public String toString() {
		return null;
	}
}
