package com.nibss.cmms.security;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class Sha1PasswordEncoder implements PasswordEncoder {

	@Override
	public String encodePassword(String plainText, Object salt) {
		return getHash(plainText);
	}

	@Override
	public boolean isPasswordValid(String encryptedPass, String plainText, Object salt) {
		return getHash(plainText).equals(encryptedPass);
	}

	public static byte[] computeHash(String x) throws Exception {
		java.security.MessageDigest d = null;
		d = java.security.MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(x.getBytes());
		return d.digest();
	}

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public String getHash(String input) {
		String hash = "";
		try {
			hash = byteArrayToHexString(computeHash(input));
		} catch (Exception ex) {
			System.out.println("Security Error " + ex.toString());
		}
		return hash;
	}
}
