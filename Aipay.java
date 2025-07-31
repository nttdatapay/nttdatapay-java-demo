package com.ntt.apiclientkit;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Aipay {

	private static int pswdIterations = 65536;
	private static int keySize = 256;
	private static final byte[] ivBytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

	public static String initTransactionForAipay(String merchId, String amount, String merchTxnId, String returnURL,
			String userId, String password, String custMobile, String custEmail, String custFirstName, String custAccNo,
			String billingInfo, String product, String txnCurrency, String encReqkey, String encRespKey, String udf1,
			String udf2, String udf3, String udf4, String udf5, String environment) {

		StringBuilder response = new StringBuilder();
		StringBuffer dataToWrite = new StringBuffer();
		String checkoutUrl = null;
		String atomTokenId = null;
		String authResponse = null;
		DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		String merchTxnDate = myFormat.format(date);
		String apiUrl = null;
		String encData = null;
		String jsonData = "{\"payInstrument\":{\"headDetails\":{\"version\":\"OTSv1.1\",\"api\":\"AUTH\",\"platform\":\"FLASH\"},\"merchDetails\":{\"merchId\":\""
				+ merchId + "\",\"userId\":\"\",\"password\":\"" + password + "\",\"merchTxnId\":\"" + merchTxnId
				+ "\",\"merchTxnDate\":\"" + merchTxnDate + "\"},\"payDetails\":{\"amount\":\"" + amount
				+ "\",\"product\":\"" + product + "\",\"custAccNo\":\"" + custAccNo
				+ "\",\"txnCurrency\":\"INR\"},\"extras\":{\"udf1\":\"" + udf1 + "\",\"udf2\":\"" + udf2
				+ "\",\"udf3\":\"" + udf3 + "\",\"udf4\":\"" + udf4 + "\",\"udf5\":\"" + udf5
				+ "\"},\"custDetails\":{\"custFirstName\":\"" + custFirstName + "\",\"custEmail\":\"" + custEmail
				+ "\",\"custMobile\":\"" + custMobile + "\"}}}";
		try {
			encData = encrypt(jsonData.trim(), encReqkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			if (environment.equalsIgnoreCase("PROD")) {
				apiUrl = "https://payment1.atomtech.in/ots/aipay/auth?";
			} else {
				apiUrl = "https://caller.atomtech.in/ots/aipay/auth?";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			URL url = new URL(apiUrl + "merchId=" + merchId + "&encData=" + encData);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				System.out.println("API Response: " + response.toString());
				response.toString();
			} else {
				System.out.println("API request failed with response code: " + responseCode);
			}
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] data = response.toString().split("&");
			Map<String, String> reqMap = new HashMap<>();
			for (int i = 0; i < data.length; i++) {
				String[] data1 = data[i].split("=");
				reqMap.put(data1[0], data1[1]);
			}
			encData = reqMap.get("encData");
			merchId = reqMap.get("merchId");
			authResponse = decrypt(encData, encRespKey);
			try {
				atomTokenId = getValueFromJson(authResponse, "atomTokenId");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (environment == "local") {
					checkoutUrl = "http://localhost:8080/AtomInstaPay/AtomCheckout.js";
				}
				// UAT
				else if (environment == "UAT") {
					checkoutUrl = "https://pgtest.atomtech.in/staticdata/ots/js/atomcheckout.js";
				} else {
					// PROD
					checkoutUrl = "https://psa.atomtech.in/staticdata/ots/js/atomcheckout.js";
				}
				dataToWrite.append("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>");
				dataToWrite.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
				dataToWrite.append("</head>");
				dataToWrite.append(
						"<script type=\"text/javascript\"\r\n" + "		src=" + checkoutUrl + ">\r\n" + "	</script>");
				dataToWrite.append("<script type=\"text/javascript\">");
				dataToWrite.append("function pay(){\r\n" + "	    	    const options = {\r\n"
						+ "	      \"atomTokenId\": \"" + atomTokenId + "\",");
				dataToWrite.append("\"merchId\": \"" + merchId + "\",");
				dataToWrite.append("\"custEmail\": \"" + custEmail + "\",");
				dataToWrite.append("\"custMobile\": \"" + custMobile + "\",");
				dataToWrite.append("\"returnUrl\": \"" + returnURL + "\"");
				dataToWrite.append("}\r\n" + "	    let atom = new AtomPaynetz(options);\r\n" + "	}\r\n"
						+ "	window.onload = pay ;\r\n" + "	</script></body>\r\n" + "</html>");

				return dataToWrite.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataToWrite.toString();
	}

	public static String response(String encData, String merchId, String respKey) {
		String decryptedResponse = null;
		try {
			decryptedResponse = decrypt(encData, respKey);
			return decryptedResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedResponse;
	}

	private static String getValueFromJson(String jsonString, String key) {
		// Remove spaces and quotes from the JSON string
		jsonString = jsonString.replaceAll("\\s+", "");

		// Find the index of the key in the JSON string
		int keyIndex = jsonString.indexOf("\"" + key + "\":");

		// If the key is found, extract the corresponding value
		if (keyIndex != -1) {
			int startIndex = keyIndex + key.length() + 3; // Account for the key and the colon
			int endIndex = jsonString.indexOf(",", startIndex);
			if (endIndex == -1) {
				endIndex = jsonString.indexOf("}", startIndex);
			}

			return jsonString.substring(startIndex, endIndex);
		} else {
			return null;
		}
	}

	public static String encrypt(String plainText, String key) {
		try {
			byte[] saltBytes = key.getBytes("UTF-8");
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, pswdIterations, keySize);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
			IvParameterSpec localIvParameterSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(1, secret, localIvParameterSpec);
			byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
			return byteToHex(encryptedTextBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String encryptedText, String key) {
		try {
			byte[] saltBytes = key.getBytes("UTF-8");

			byte[] encryptedTextBytes = hex2ByteArray(encryptedText);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, pswdIterations, keySize);
			SecretKey secretKey = factory.generateSecret(spec);
			SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
			IvParameterSpec localIvParameterSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(2, secret, localIvParameterSpec);
			byte[] decryptedTextBytes = (byte[]) null;
			decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String byteToHex(byte[] byData) {
		StringBuffer sb = new StringBuffer(byData.length * 2);
		for (int i = 0; i < byData.length; ++i) {
			int v = byData[i] & 0xFF;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hex2ByteArray(String sHexData) {
		byte[] rawData = new byte[sHexData.length() / 2];
		for (int i = 0; i < rawData.length; ++i) {
			int index = i * 2;
			int v = Integer.parseInt(sHexData.substring(index, index + 2), 16);
			rawData[i] = (byte) v;
		}
		return rawData;
	}
}
