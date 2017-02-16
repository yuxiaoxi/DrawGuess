package net.duohuo.dhroid.net;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureUtil {
	public static String getSecretKey(String key) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("test-bbg", "test-bbg-secret");
		return map.get(key);
	}

	public static String hmac_sha1(String value, String key) {
		try {
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(value.getBytes());
			String hexBytes = byte2hex(rawHmac);
			return hexBytes.toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String byte2hex(final byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0xFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs;
	}
}