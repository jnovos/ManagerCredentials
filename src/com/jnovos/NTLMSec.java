package com.jnovos;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class NTLMSec {

	public static void main(String[] args) {

		CredentialManager credentialStore = new CredentialManager();
		for (Credencial credencial : credentialStore.listaCredenciales()) {
			System.out.println(credencial.getAlias() + " : " + credencial.getUsername());
		}

//		// credentialStore.add("MIKEY",new Credential("miKey","prueba"));
//		Credential credential = credentialStore.get("miKey");
//		System.out.println(credential.Username);
//		System.out.println(credential.Password);
	}

	private static String getWorkstation() {
		Map<String, String> env = System.getenv();

		if (env.containsKey("COMPUTERNAME")) {
			// Windows
			return env.get("COMPUTERNAME");
		} else if (env.containsKey("HOSTNAME")) {
			// Unix/Linux/MacOS
			return env.get("HOSTNAME");
		} else {
			// From DNS
			try {
				return InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				return "Unknown";
			}
		}
	}
}
