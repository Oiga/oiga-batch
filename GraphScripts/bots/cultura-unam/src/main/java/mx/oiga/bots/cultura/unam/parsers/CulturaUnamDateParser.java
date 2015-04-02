package mx.oiga.bots.cultura.unam.parsers;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CulturaUnamDateParser {
	private static Logger logger = LoggerFactory
			.getLogger(CulturaUnamDateParser.class);
	private static final String INTERVAL_EXP = "'Del' dd 'de' MMMM 'del' yyyy";
	private static final String DATE_EXP = "dd 'de' MMMM 'del' yyyy";

	private static final SimpleDateFormat INTERVAL = new SimpleDateFormat(
			INTERVAL_EXP, new Locale("es", "MX"));
	private static final SimpleDateFormat DATE = new SimpleDateFormat(DATE_EXP,
			new Locale("es", "MX"));

	public static Date[] parse(String text){
		String[] split = text.split(" al ");
		Date d1;
		Date d2;
		if(split.length >= 2){
			try{
				d1 = INTERVAL.parse(split[0]);
				d2 = getEnd(DATE.parse(split[1]));
				return new Date[]{d1, d2};	
			}catch(ParseException e){
				logger.error("No se pudo parsear el texto",e);
			}
		}
		if(split.length >= 1){
			try{
				d1 = DATE.parse(split[0]);
				d2 = getEnd(d1);
				return new Date[]{d1, d2};
			}catch(ParseException e){
				logger.error("No se pudo parsear el texto",e);
			}
		}
		return null;
	}

	public static Date clearTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static Date getEnd(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

}
