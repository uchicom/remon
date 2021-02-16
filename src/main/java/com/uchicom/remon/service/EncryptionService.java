// (c) 2021 uchicom
package com.uchicom.remon.service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionService {

	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	private final IvParameterSpec iv;
	private final SecretKeySpec key;

	public EncryptionService(String encryptKey, String initVector) {
		this.key = new SecretKeySpec(encryptKey.getBytes(), "AES");
		this.iv = new IvParameterSpec(initVector.getBytes());
	}

	public byte[] encrypt(byte[] target) throws Exception {

		Cipher encrypter = Cipher.getInstance(ALGORITHM);
		encrypter.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
		return encrypter.doFinal(target);
	}

	public byte[] decrypt(byte[] encryptedBytes, int offset, int length) throws Exception {

		Cipher decrypter = Cipher.getInstance(ALGORITHM);
		decrypter.init(Cipher.DECRYPT_MODE, this.key, this.iv);

		return decrypter.doFinal(encryptedBytes, offset, length);
	}
}
