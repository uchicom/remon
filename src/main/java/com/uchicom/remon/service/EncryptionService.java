package com.uchicom.remon.service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionService {

	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	private static final String INIT_VECTOR = "testvector123456";

	private final IvParameterSpec iv;
	private final SecretKeySpec key;

	public EncryptionService(String encryptKey) {
		this.key = new SecretKeySpec(encryptKey.getBytes(), "AES");
		this.iv = new IvParameterSpec(INIT_VECTOR.getBytes());
	}

	public byte[] encrypt(byte[] target) throws Exception {

		Cipher encrypter = Cipher.getInstance(ALGORITHM);
		encrypter.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
		return encrypter.doFinal(target);
	}

	public byte[] decrypt(byte[] encryptedBytes) throws Exception {

		Cipher decrypter = Cipher.getInstance(ALGORITHM);
		decrypter.init(Cipher.DECRYPT_MODE, this.key, this.iv);

		return decrypter.doFinal(encryptedBytes);
	}
}
