package qetaa.service.user.helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class Helper {
	public static int getRandomInteger(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	public static String getSecuredRandom() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public static Date addMinutes(Date original, int minutes) {
		return new Date(original.getTime() + (1000L * 60 * minutes));
	}
	
	public String getDateFormat(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
		return sdf.format(date);
	}
	
	
	public static Date getDateStartOfMonth(int year, int month) {
		Calendar cFrom = new GregorianCalendar();
		cFrom.set(year, month, 1, 0, 0, 0);
		cFrom.set(Calendar.MILLISECOND, 0);
		return new Date(cFrom.getTimeInMillis());
	}
	
	public static Date getDateEndOfMonth(int year, int month) {
		Calendar cTo = new GregorianCalendar();
		cTo.set(year, month, 1, 0, 0, 0);
		cTo.set(Calendar.MILLISECOND, 0);
		cTo.set(Calendar.DAY_OF_MONTH, cTo.getActualMaximum(Calendar.DAY_OF_MONTH));
		cTo.set(Calendar.HOUR_OF_DAY, 23);
		cTo.set(Calendar.MINUTE, 59);
		cTo.set(Calendar.SECOND, 59);
		cTo.set(Calendar.MILLISECOND, cTo.getActualMaximum(Calendar.MILLISECOND));
		return new Date(cTo.getTimeInMillis());
	}

	public static String cypher(String text) throws NoSuchAlgorithmException {
		String shaval = "";
		MessageDigest algorithm = MessageDigest.getInstance("SHA-256");

		byte[] defaultBytes = text.getBytes();

		algorithm.reset();
		algorithm.update(defaultBytes);
		byte messageDigest[] = algorithm.digest();
		StringBuilder hexString = new StringBuilder();

		for (int i = 0; i < messageDigest.length; i++) {
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		shaval = hexString.toString();

		return shaval;
	}

}