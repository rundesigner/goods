/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.vm2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import tools.Messages;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class DataConversion {

    public static void main(String[] args) {
        Messages.consolemode = 1;
        Messages.show("s=" + fromDb2Field2("2012-10-20"));
    }

    public static String fromDb2Field(String s) {
        if (s == null) {
            return "0000.00.00";
        }
        String ret = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = format.parse(s);
            SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String fromDb2PrintReport(String s) {
        String ret = "";
        try {
            String day = getDay2(s);
            String sklonMonth = getSklonMonth2(s);
            String year = getYear2(s);
            ret = day + " " + sklonMonth + " " + year;
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String fromDb2Field2(String s) {
        String ret = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(s);
            SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String toDb(String s) {
        String ret = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Date date = format.parse(s);
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String toFile(Date date) {
        String ret = "";
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd_hh_mm");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static Date parseFromDb(String s) {
        Date ret = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = format.parse(s);
            ret = date;
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static Date parseFromDb2(String s) {
        Date ret = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(s);
            ret = date;
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static Date parseFromField(String s) {
        Date ret = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Date date = format.parse(s);
            ret = date;
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getDay(String s) {
        String ret = "";
        Date date = parseFromField(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("dd");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getMonth(String s) {
        String ret = "";
        Date date = parseFromField(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("MM");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getYear(String s) {
        String ret = "";
        Date date = parseFromField(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getSklonMonth(String s) {
        String ret = "";
        Date date = parseFromField(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("MM");
            ret = format2.format(date);
            if (ret.equalsIgnoreCase("01")) {
                ret = "января";
            }
            if (ret.equalsIgnoreCase("02")) {
                ret = "февраля";
            }
            if (ret.equalsIgnoreCase("03")) {
                ret = "марта";
            }
            if (ret.equalsIgnoreCase("04")) {
                ret = "апреля";
            }
            if (ret.equalsIgnoreCase("05")) {
                ret = "мая";
            }
            if (ret.equalsIgnoreCase("06")) {
                ret = "июня";
            }
            if (ret.equalsIgnoreCase("07")) {
                ret = "июля";
            }
            if (ret.equalsIgnoreCase("08")) {
                ret = "августа";
            }
            if (ret.equalsIgnoreCase("09")) {
                ret = "сентября";
            }
            if (ret.equalsIgnoreCase("10")) {
                ret = "октября";
            }
            if (ret.equalsIgnoreCase("11")) {
                ret = "ноября";
            }
            if (ret.equalsIgnoreCase("12")) {
                ret = "декабря";
            }
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getDay2(String s) {
        String ret = "";
        Date date = parseFromDb2(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("dd");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getMonth2(String s) {
        String ret = "";
        Date date = parseFromDb2(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("MM");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getYear2(String s) {
        String ret = "";
        Date date = parseFromDb2(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy");
            ret = format2.format(date);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getSklonMonth2(String s) {
        String ret = "";
        Date date = parseFromDb2(s);
        try {
            SimpleDateFormat format2 = new SimpleDateFormat("MM");
            ret = format2.format(date);
            if (ret.equalsIgnoreCase("01")) {
                ret = "января";
            }
            if (ret.equalsIgnoreCase("02")) {
                ret = "февраля";
            }
            if (ret.equalsIgnoreCase("03")) {
                ret = "марта";
            }
            if (ret.equalsIgnoreCase("04")) {
                ret = "апреля";
            }
            if (ret.equalsIgnoreCase("05")) {
                ret = "мая";
            }
            if (ret.equalsIgnoreCase("06")) {
                ret = "июня";
            }
            if (ret.equalsIgnoreCase("07")) {
                ret = "июля";
            }
            if (ret.equalsIgnoreCase("08")) {
                ret = "августа";
            }
            if (ret.equalsIgnoreCase("09")) {
                ret = "сентября";
            }
            if (ret.equalsIgnoreCase("10")) {
                ret = "октября";
            }
            if (ret.equalsIgnoreCase("11")) {
                ret = "ноября";
            }
            if (ret.equalsIgnoreCase("12")) {
                ret = "декабря";
            }
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public static String getCurrentHour(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date 
        String hour = "" + calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        return hour;
    }

    public static String getCurrentMinute(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date 
        String hour = "" + calendar.get(Calendar.MINUTE); // gets hour in 24h format
        return hour;
    }

    public static String getCurrentDay(Date date) {
          // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date 
        String hour = "" + calendar.get(Calendar.DAY_OF_MONTH); // gets hour in 24h format
        return hour;
    }

    public static String getCurrentMonth(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date 
        String hour = "" + calendar.get(Calendar.MONTH); // gets hour in 24h format
        return hour;
    }

    public static String getCurrentYear(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date 
        String hour = "" + calendar.get(Calendar.MONTH); // gets hour in 24h format
        return hour;
    }
    
    public static Date datePlusYear(Date date){
        Date ret = date;
         Calendar calendar = Calendar.getInstance();  
         calendar.add( Calendar.YEAR, 1 );  
      //   calendar.( Calendar.YEAR, 1 );  
        return ret;
    }
    
}
