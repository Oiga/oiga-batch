package mx.oiga.bots.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;

public class FileNamingUtils {
	public static final SimpleDateFormat FILE_DATE_PREFIX_FORMAT = new SimpleDateFormat("yyyy_MM");
	public static final SimpleDateFormat FILE_FULL_DATE_PREFIX_FORMAT = new SimpleDateFormat("yyyyMMdd");

	public static final String CULTURA_UNAM_FILE_NAME = "_cultura_unam_%s.json";
	
	public static String getName(String format){
		return  String.format(format, FILE_DATE_PREFIX_FORMAT.format(new Date()));
	}
	public static String getName(String format, Date date){
		return  String.format(format, FILE_DATE_PREFIX_FORMAT.format(date));
	}
	public static String getName(String format, Date start, Date end){
		return  String.format(format, FILE_FULL_DATE_PREFIX_FORMAT.format(start)+"_"+FILE_FULL_DATE_PREFIX_FORMAT.format(end));
	}
	public static String getCulturaUnamFileName(String prefix){
		return getName(prefix+CULTURA_UNAM_FILE_NAME);
	}
	public static String getCulturaUnamFileName(String prefix, Date date){
		return getName(prefix+CULTURA_UNAM_FILE_NAME);
	}
	public static String getCulturaUnamFileName(String prefix, Date start, Date end){
		return getName(prefix+CULTURA_UNAM_FILE_NAME, start, end);
	}
	public static String getCulturaUnamFileName(String prefix, String start, String end){
		try {
			return getName(prefix+CULTURA_UNAM_FILE_NAME,  FILE_FULL_DATE_PREFIX_FORMAT.parse(start), FILE_FULL_DATE_PREFIX_FORMAT.parse(end));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
