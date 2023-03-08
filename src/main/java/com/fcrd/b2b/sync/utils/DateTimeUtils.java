package com.fcrd.b2b.sync.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeUtils {
	
	public static OffsetDateTime xmlGregorianCalendarToOffsetDateTime(XMLGregorianCalendar xmlGregorianCalendar) throws Exception {
		java.time.OffsetDateTime javaOffsetDateTime = xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toOffsetDateTime();
		return javaOffsetDateTime;
	}
	
	public static String offsetDateTimeToString(OffsetDateTime offsetDateTime, String datetimePattern) throws Exception {
		return offsetDateTime.format(DateTimeFormatter.ofPattern(datetimePattern));
	}
	
	public static XMLGregorianCalendar offsetDateTimeToXmlGregorianCalendar(OffsetDateTime offsetDateTime) throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(offsetDateTime.toInstant().toEpochMilli());
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		xmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		
		return xmlGregorianCalendar;
	}
	
	public static XMLGregorianCalendar minXMLGregorianCalendar() throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(0L);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		xmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		
		return xmlGregorianCalendar;
	}
	
}
