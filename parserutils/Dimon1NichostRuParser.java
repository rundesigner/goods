/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parserutils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import tools.Messages;
import vmre.VMREView;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class Dimon1NichostRuParser {

    public static Integer consolmode = 0;
    public static VMREView v = null;
    public static HtmlCleaner cleaner = null;
    public static URL myUrl = null;
    public static String sendCookie = null;
    public static URLConnection urlConn = null;
    public static String htmlcontent = "";

    public static void main(String[] args) {
        try {
            Messages.consolemode = 1;
            //  setConnection();
            
            getDescription(new HashMap(){{
                put("pname","mr1070");
            }});
            //getDescription("xczcxz263071", "Dell E1912H BK/BK");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap getDescription(HashMap params)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        String pname=(String)params.get("sku");
        HashMap resultMap = new HashMap();
        HashMap blankMap = new HashMap();
        resultMap.put("fulldescription", "");
        resultMap.put("description", "");
        resultMap.put("bigimage", "");
        blankMap = resultMap;

        cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
        props.setPruneTags("script,style");
        TagNode t = getPoisk(pname);

        if (t == null) {
            return blankMap;
        }

        // printTag(t, props);
        try {
            Object[] found;

            //ищем изображение
            found = t.evaluateXPath("//div[@id=\"foto\"]//img/@src");
            if (found.length > 0) {
                String prodImage = (String) found[0];
                
                resultMap.put("bigimage", "http://dimon1.nichost.ru" + prodImage);
                Messages.show("Изображение найдено: " + resultMap.get("bigimage"));
            } else {
                Messages.show("Изображение не найдено");
            }


        } catch (Exception e) {
            Messages.show(e.getMessage());
        }
        return resultMap;
    }

    public static TagNode getPoisk(String pname) {
        TagNode t = null;
        Object[] found;
        try {
            Messages.show("Ищем товар");
            // вхождение всех слов
            // String searche = "http://www.kartmaster.ru/search/search_do/?search_string=%D0%9A%D0%B0%D1%80%D1%82%D1%80%D0%B8%D0%B4%D0%B6+08%D0%900476&search-or-mode=1&x=0&y=0";
            //вхождение хотя бы одного слова
            String searche;

            searche = "http://dimon1.nichost.ru/index.php?page=shop.browse&option=com_virtuemart&Itemid=6&keyword=" + URLEncoder.encode(pname, "utf-8");

            Messages.show(searche);
            setRequestProperties(searche);
            t = cleaner.clean(new URL(searche));
            return t;
        } catch (Exception e) {
            Messages.show("Ошибка при попытке поиска");
        }
        return t;
    }

    public static void getCookie() {
        String headerName = null;
        sendCookie = "";
        for (int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++) {
            //Messages.show(headerName+"="+urlConn.getHeaderField(i));
            if (headerName.equals("Set-Cookie")) {
                String cookie = urlConn.getHeaderField(i);
                // Messages.show("Куки=" + cookie);
                cookie = cookie.substring(0, cookie.indexOf(";"));
                String cookieName = cookie.substring(0, cookie.indexOf("="));
                String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                sendCookie += cookieName + "=" + cookieValue + "; ";
            }
        }
    }

    public static void setRequestProperties(String searche) {

        try {
            myUrl = new URL(searche);
            urlConn = myUrl.openConnection();
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:8.0) Gecko/20100101 Firefox/8.0");
            //urlConn.setRequestProperty("Cookie", sendCookie + "scroll_count=6; _visorc=w");
            urlConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            urlConn.setRequestProperty("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
            //urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConn.setRequestProperty("Accept-Encoding", "deflate");
            //urlConn.setRequestProperty("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7");
            urlConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            urlConn.connect();
        } catch (MalformedURLException e) {
            Messages.show("ОшибкаMal:" + "setRequestProperties" + searche);
        } catch (IOException ex) {
            Messages.show("ОшибкаIO:" + "setRequestProperties" + searche);
        }
    }

    public static void setConnection() {
        try {
            myUrl = new URL("http://dimon1.nichost.ru/");
            urlConn = myUrl.openConnection();
            urlConn.connect();
            sendCookie = "";
            getCookie();
            Messages.show("sendCookie=" + sendCookie);
            //urlConn.connect();
            //getCookie();
            //Messages.show("sendCookie=" + sendCookie);
            //urlConn.
        } catch (Exception e) {
            Messages.show(" Ошибка при соединении !!! " + e.getMessage());
        }
    }

    public static String printTag(TagNode t) {
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
        props.setPruneTags("script,style");
        PrettyHtmlSerializer ps = new PrettyHtmlSerializer(props);
        StringWriter sw = new StringWriter();
        try {
            ps.write(t, sw, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String paragraph = sw.toString().replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
        if (consolmode == 1) {
            System.out.println("======================================");
            System.out.println(paragraph);
        }
        return paragraph;
    }
}
