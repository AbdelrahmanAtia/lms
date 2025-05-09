package com.atos.piam.lms.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.unboundid.ldap.sdk.Filter;

public class LdapUtils {
	private static final DateTimeFormatter GENERALIZED_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyyMMdd'000000Z'");

	/**
	 * converts the java LocalDate type into ldap GeneralizedTime string format
	 */
	public static String toGeneralizedTime(LocalDate date) {
		if (date == null) {
			return null;
		}
		return date.format(GENERALIZED_TIME_FORMATTER);
	}

	public static LocalDate fromGeneralizedTime(String generalizedTime) {
		if (generalizedTime == null) {
			return null;
		}
		// Extract the date portion (YYYYMMDD) and parse it
		String datePart = generalizedTime.substring(0, 8);
		return LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
	}
	
    public static String escapeForFilter(String value) {
        if (value == null) {
            return null;
        }
        return Filter.encodeValue(value);
    }	
}