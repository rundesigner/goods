/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.vm2;

import java.text.SimpleDateFormat;
import java.util.Date;
import tools.Messages;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class SafeValues {

    public static Date getDate(String param, Date umolchanie) {
        Date ret = new Date();
        if (null == param) {
            ret = umolchanie;
        } else {
            if (param.equalsIgnoreCase("")) {
                ret = umolchanie;
            } else {
                try {
                    ret = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    ret = format.parse(param);
                } catch (Exception e) {
                    ret = umolchanie;
                }
            }

        }
        return ret;
    }

    public static Date getDate(String param) {
        Date ret = new Date();
        ret = getDate(param, ret);
        return ret;
    }

    public static String getTimeStamp(String s) {
        String ret = "";
        if (s.trim().equalsIgnoreCase("") || null == s
                || s.equalsIgnoreCase("0000-00-00 00:00:00")
                || s.equalsIgnoreCase("2012-10-30 00:00:00")) {
            ret = "2012-10-30 00:00:00";
        } else {
            if (s.length() == 10) {
                Messages.show("s parse=" + s);
                ret = DataConversion.toDb(s);
                ret += " 00:00:00";
            } else {
                ret = "2012-10-30 00:00:00";
            }
        }
        //"08.11.2012"
        //"yyyy-mm-dd hh:mm:ss";
        return ret;
    }

    public static String getTimeStampPublishDown(String s) {
        String ret = "";
        if (s.trim().equalsIgnoreCase("") || null == s
                || s.equalsIgnoreCase("0000-00-00 00:00:00")
                || s.equalsIgnoreCase("2012-10-30 00:00:00")) {
            ret = null;
        } else {
            if (s.length() == 10) {
                Messages.show("s parse=" + s);
                ret = DataConversion.toDb(s);
                ret += " 00:00:00";
            } else {
                ret = null;
            }
        }
        //"08.11.2012"
        //"yyyy-mm-dd hh:mm:ss";
        return ret;
    }
}
