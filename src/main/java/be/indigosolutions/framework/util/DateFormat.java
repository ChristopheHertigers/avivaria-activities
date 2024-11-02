package be.indigosolutions.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 15-aug-2008
 */
public enum DateFormat {
    DEFAULT("dd/MM/yyyy"),
    LONG("dd MMMM yyyy"),
    SYSTEM("yyyy-MM-dd"),
    YEAR("yyyy");

    private final String format;

    DateFormat(String format) {
        this.format = format;
    }

    public static Date startOfDay(Date date) {
        if (date == null) return null;
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.setLenient(false);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date endOfDay(Date date) {
        if (date == null) return null;
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.setLenient(false);
        cal.set(Calendar.HOUR, cal.getMaximum(Calendar.HOUR));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }

    public String getFormat() {
        return format;
    }

    public SimpleDateFormat getFormatter() {
        return new SimpleDateFormat(getFormat(), new Locale("nl", "BE"));
    }

    public String format(Date date) {
        return getFormatter().format(date);
    }

    public Date parse(String date) {
        try {
            return getFormatter().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Unparseable date string " + date);
        }
    }
}