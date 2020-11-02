/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parserutils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import tools.Messages;
import vmre.VMREView;

/**
 *
 * @author grigory
 */
public class MarketYandexRuParser {

     public static Integer consolmode = 0;
    public static VMREView v = null;
    public static HtmlCleaner cleaner = null;
    public static URL myUrl = null;
    public static String sendCookie = null;
    public static URLConnection urlConn = null;
    public static String htmlcontent = "";
    
    public static void main(String[] args) {
        try {
//            getDescription("VOXTEL RX20");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
        public static TagNode getPoisk(String pname) {
        TagNode t = null;
        Object[] found;
        try {
            Messages.show("Ищем товар");
            String searche;
            searche = "http://market.yandex.ru/search.xml?cvredirect=2&text=" + URLEncoder.encode(pname, "utf-8");
            Messages.show(searche);
            setRequestProperties(searche);
            t = cleaner.clean(new URL(searche));

            found = t.evaluateXPath("//div/div/div/div[4]/ul/li/a/@href");
            Messages.show(String.valueOf(found.length));
            if (found.length > 0) {
               
            } else {
                t=null;
                Messages.show("Нет результатов поиска");
            }

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

    public static HashMap getDescription(HashMap param)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {
        HashMap resultMap = new HashMap();
        resultMap.put("fulldescription", "");
        resultMap.put("description", "");
        resultMap.put("bigimage", "");
        String product = (String) param.get("pname");
        product = product.replace(" ", "+");
        product = product.replace("\\+\\+", "");
        WebFile file = new WebFile("http://market.yandex.ru/search.xml?text=" + product + "&cvredirect=1");
        String MIME = file.getMIMEType();
        Object content = file.getContent();
        System.out.println("URL=" + file.getURL());
        String str;
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
        TagNode t = cleaner.clean(content.toString());
        TagNode t2;
        String description = "";
        try {
            Object[] found = t.evaluateXPath("//*/div[@class='b-model-friendly']/ul");
            System.out.println("taglenth=" + found.length);
            if (found.length > 0) {
                t2 = (TagNode) found[0];
                if (t2.hasChildren()) {
                    List linodes = t2.getChildTagList();
                    description += "<ul>";

                    for (int k = 0; k < linodes.size(); k++) {
                        //  System.out.println("Linodes==="+k+"="+linodes.item(k).getNodeName());
                        if (((TagNode) linodes.get(k)).getName().toString().equalsIgnoreCase("li")) {
                            description += "<li>" + ((TagNode) linodes.get(k)).getText().toString().replaceAll("\n", "<br/>") + "</li>";
                        }
                    }
                    description += "</ul>";
                }
            } else {
                // получим ссылку на товар
                found = t.evaluateXPath("//*/a[@class='b-offers__name']");
                t2 = (TagNode) found[0];
                if (found.length > 0) {
                    System.out.println("Найдена ссылка на товар" + t2.getAttributeByName("href"));
                    file = new WebFile("http://market.yandex.ru" + t2.getAttributeByName("href"));
                    content = file.getContent();
                    cleaner = new HtmlCleaner();
                    props = cleaner.getProperties();
                    props.setAllowHtmlInsideAttributes(true);
                    props.setAllowMultiWordAttributes(true);
                    props.setRecognizeUnicodeChars(true);
                    props.setOmitComments(true);
                    t = cleaner.clean(content.toString());
                    found = t.evaluateXPath("//*/div[@class='b-model-friendly']/ul");
                    System.out.println("taglenth=" + found.length);
                    if (found.length > 0) {
                        t2 = (TagNode) found[0];
                        if (t2.hasChildren()) {
                            List linodes = t2.getChildTagList();
                            description += "<ul>";

                            for (int k = 0; k < linodes.size(); k++) {
                                //  System.out.println("Linodes==="+k+"="+linodes.item(k).getNodeName());
                                if (((TagNode) linodes.get(k)).getName().toString().equalsIgnoreCase("li")) {
                                    description += "<li>" + ((TagNode) linodes.get(k)).getText() + "</li>";
                                }
                            }
                            description += "</ul>";
                        }
                    }
                } else {
                    System.out.println("Не Найдена ссылка на товар");
                }
            }
            //получим изображения
            TagNode t3;
            TagNode t4;
            String bigpicture;
            String smallpicture;
            found = t.evaluateXPath("//*/span[@class='b-model-pictures__big']");
            if (found.length > 0) {
                t2 = (TagNode) found[0];
                if (t2.hasChildren()) {
                    t3 = t2.findElementByName("a", true);
                    if (null != t3) {
                        bigpicture = t3.getAttributeByName("href");
                        resultMap.put("bigimage", bigpicture);
                        //    System.out.println("bigpicture=" + bigpicture);
                    } else {
                        t3 = t2.findElementByName("img", true);
                        if (null != t3) {
                            smallpicture = t3.getAttributeByName("src");
                            resultMap.put("bigimage", smallpicture);
                            //resultMap.put("smallimage", smallpicture);
                               System.out.println("bigpicture=" + smallpicture);
                        }
                    }
                }
            }
            //получим все тех характеристики
            String detailUrl = file.getURL().toString().replace("model.xml", "model-spec.xml");
            file = new WebFile(detailUrl);
            content = file.getContent();
            cleaner = new HtmlCleaner();
            props = cleaner.getProperties();
            props.setAllowHtmlInsideAttributes(true);
            props.setAllowMultiWordAttributes(true);
            props.setRecognizeUnicodeChars(true);
            props.setOmitComments(true);
            t = cleaner.clean(content.toString());
            found = t.evaluateXPath("//*/table[@class='b-properties']/tbody");
            String fulldescription = "";
            List linodes;
            Iterator it;
            if (found.length > 0) {
                t2 = (TagNode) found[0];
                if (t2.hasChildren()) {
                    fulldescription += "<table><tbody>";
//                    linodes = t2.getChildren();
                 //   it = linodes.iterator();
//                    while (it.hasNext()) {
//                        t3 = (TagNode) it.next();//tr
//                        t4 = t3.findElementByName("td", true);
//                        fulldescription += "<tr>";
//                        if (null == t4) {
//                            t4 = t3.findElementByName("th", true);
//                            fulldescription += "<th class=\"b-properties__title\" colspan=\"2\">" + t3.getText() + "</th>";
//                        } else {
//                            t4 = t3.findElementByName("th", true);
//                            fulldescription += "<th class=\"b-properties__label b-properties__label-title\"><span>" + t4.getText() + "</span></th>";
//                            t4 = t3.findElementByName("td", true);
//                            fulldescription += "<td>" + t4.getText() + "</td>";
//                        }
//                        //        System.out.println("name=" + t4.getName());
//                        fulldescription += "</tr>";
//                    }
                    fulldescription += "</tbody></table>";
                }
            }
            resultMap.put("description", description);
            resultMap.put("fulldescription", fulldescription);
            System.out.println("fulldescr=" + description);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return resultMap;
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
            myUrl = new URL("http://market.yandex.ru/");
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
    
}