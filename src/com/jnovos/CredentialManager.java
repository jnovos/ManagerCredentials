package com.jnovos;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.alm.helpers.StringHelper;
import com.microsoft.alm.helpers.SystemHelper;
import com.microsoft.alm.storage.windows.internal.CredManagerBackedSecureStore;
import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Clase que accede al manager de windows
 * 
 * 
 * @author jnovos
 *
 */
public class CredentialManager extends CredManagerBackedSecureStore<Credencial> {

	private final CredAdvapi32<?> INSTANCE = getCredAdvapi32Instance();

	public List<Credencial> listaCredenciales() {
		CredAdvapi32.CREDENTIAL credential = null;
		boolean read = false;
		IntByReference pCount = new IntByReference();
		List<Credencial> lista = new ArrayList<Credencial>();
		PointerByReference pRref = new PointerByReference();
		try {

			synchronized (INSTANCE) {
				read = INSTANCE.CredEnumerateW(null, 0, pCount, pRref);
			}
			if (read) {
				String secret = "";
				Pointer[] ps = pRref.getValue().getPointerArray(0, pCount.getValue());
				for (int n = 0; n < pCount.getValue(); n++) {
					credential = new CredAdvapi32.CREDENTIAL(ps[n]);
					if (credential.CredentialBlobSize > 0) {
						byte[] secretBytes = credential.CredentialBlob.getByteArray(0, credential.CredentialBlobSize);
						secret = StringHelper.UTF8GetString(secretBytes);
					} else {
						secret = "NO PASS";
					}
					String username = credential.UserName;
					lista.add((Credencial) create(username, secret, credential.TargetName));
				}

			} else {
				lista = null;
			}

		} catch (final LastErrorException e) {

			lista = null;
			if (credential != null) {
				synchronized (INSTANCE) {
					INSTANCE.CredFree(credential.getPointer());
				}
			}
		}
		return lista;
	}

	private static CredAdvapi32<?> getCredAdvapi32Instance() {
		if (SystemHelper.isWindows()) {
			return CredAdvapi32.INSTANCE;
		} else {
			return new CredAdvapi32<Object>() {
				@Override
				public boolean CredRead(String targetName, int type, int flags, PCREDENTIAL pcredential)
						throws LastErrorException {
					return false;
				}

				@Override
				public boolean CredWrite(CREDENTIAL credential, int flags) throws LastErrorException {
					return false;
				}

				@Override
				public boolean CredDelete(String targetName, int type, int flags) throws LastErrorException {
					return false;
				}

				@Override
				public void CredFree(Pointer credential) throws LastErrorException {

				}

				@Override
				public boolean CredEnumerateW(String filter, int flag, IntByReference count,
						PointerByReference pCredentials) throws LastErrorException {
					return false;
				}

			};
		}
	}

	/**
	 * Metodo que crea un bean de la credencial
	 * 
	 * @param username
	 * @param secret
	 * @param alias
	 * @return
	 */
	protected Credencial create(String username, String secret, String alias) {
		return new Credencial(username, secret, alias);
	}

	@Override
	protected Credencial create(String username, String secret) {
		return new Credencial(username, secret);
	}

	@Override
	protected String getUsername(Credencial secret) {
		return secret.getUsername();
	}

	@Override
	protected String getCredentialBlob(Credencial secret) {
		return secret.getPassword();
	}

}