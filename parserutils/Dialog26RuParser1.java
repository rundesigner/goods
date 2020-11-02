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
import java.util.List;
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
 * @author grigory
 */
public class Dialog26RuParser1 {

    public static Integer consolmode = 0;
    public static VMREView v = null;
    public static HtmlCleaner cleaner = null;
    public static URL myUrl = null;
    public static String sendCookie = null;
    public static URLConnection urlConn = null;
    public static String htmlcontent = "";
    public static String baseurl = "dialog26.ru";

    public static void main(String[] args) {
        try {
            consolmode = 1;
            Messages.consolemode = 1;
            HashMap params = new HashMap();
            params.put("sku", "BF0528P");
            //params.put("pname", "ga");
            HashMap results;
            results = getDescription(params);
            Messages.show("results=" + results.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static TagNode getPoisk(String pname) {
        TagNode t = null;
        Object[] found;
        try {
            Messages.show("Ищем товар");
            setConnection();
            String searche;
            String clientId = "";
            //clientId = "&client_id=3681"; //Ищем по сотмаркету Тула
            //clientId = "&client_id=4770"; //Ищем по сотмаркету Казань
            //searche = "http://" + baseurl + "/search/?q=" + URLEncoder.encode(pname.toLowerCase(), "utf-8") + clientId;
            searche = "http://" + baseurl + "/index.php?route=product/search&filter_name=" + URLEncoder.encode(pname.toLowerCase(), "utf-8");
            t = getTag(searche);
            //  printTag(t);
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
        String product = (String) param.get("sku");
//        product = product.replace(" ", "+");
//        product = product.replace("\\+\\+", "");

        TagNode t = getPoisk(product);
        TagNode t2;
        String description = "";
        String fulldescription = "";
        Object[] found;
        try {

            // получим ссылку на товар в списке товаров
            found = t.evaluateXPath("//div[@class='name']/a");            
            if (found.length > 0) {
                t2 = (TagNode) found[0];
                Messages.show("Найдена ссылка на товар" + t2.getAttributeByName("href"));
                String file = t2.getAttributeByName("href");
                 file=file.replaceAll("\\&amp;", "&");
                t = getTag(file);
//                //малое описание
//                found = t.evaluateXPath("//p[@itemprop='description']");
//                description = ((TagNode) (found[0])).getText() + "";
//
//                //полное описание (характеристики)
//                found = t.evaluateXPath("//li[@class=\"b-goods-specifications-row g-clearfix\"]");
//                Messages.show("Количество характеристик=" + found.length);
//                if (found.length > 0) {
//
//                    fulldescription += "<table><tbody>";
//                    for (int z = 0; z < found.length; z++) {
//                        fulldescription += "<tr>";
//                        t2 = (TagNode) found[z];
//                        if (t2.hasChildren()) {
//                            Object[] found2 = t2.evaluateXPath("/div");
//                            fulldescription += "<th class=\"b-properties__label b-properties__label-title\"><span>" + ((TagNode) found2[0]).getText() + "</span></th>";
//                            fulldescription += "<td>" + ((TagNode) found2[1]).getText() + "</td>";
//                        }
//                        fulldescription += "</tr>";
//                    }
//                    fulldescription += "</tbody></table>";
//                }
                //получим изображения            
                String bigpicture;
                //находим большое изображение на карточке сотамаркета
                // http://img-sotmarket.ru/1200x1200/img/dom/chasy/naruchnye-chasy/fossil-ch2903-0.jpg
                found = t.evaluateXPath("//div[@class='image']/a");


                if (found.length > 0) {
                                    t2 = (TagNode) found[0];
             //   Messages.show("cnt img="+t2.getAttributeByName("href"));
                    //String data_href = ((String) found[0]).split("=")[1]; //dom/chasy/naruchnye-chasy/fossil-ch2903-0
                    bigpicture = t2.getAttributeByName("href");
                    resultMap.put("bigimage", bigpicture);
                }else{
             //       Messages.show("Изображение не найдено");
                }

            } else {
                Messages.show("Не Найдена ссылка на товар");
            }


            resultMap.put("description", description);
            resultMap.put("fulldescription", fulldescription);

        } catch (Exception e) {
            e.printStackTrace();
            Messages.sboi(e);
        }
        return resultMap;
    }

    public static TagNode getTag(String url) {

        TagNode t = null;
        try {
        //    Messages.show(" Пытаемся скачать URL " + url + "");
            setRequestProperties(url);
            //setConnection();
            t = cleaner.clean(urlConn.getInputStream());
        } catch (Exception e) {
            Messages.sboi(e);
        }
      //  Messages.show("Возвращаем страницу");
        return t;
    }

    public static void setRequestProperties(String searche) {

        try {
            myUrl = new URL(searche);
            urlConn = myUrl.openConnection();
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0");
            //   urlConn.setRequestProperty("Host", "suggest.market.yandex.ru");
            //urlConn.setRequestProperty("Cookie", sendCookie + "scroll_count=6; _visorc=w");
            //    urlConn.setRequestProperty("Cookie", "yandexuid=1685868351381567977; yabs-frequency=/4/0W0t05cWH5IPepPK/fZcmS6GRGTdFSN1a6s4GEh1mP1id9JkmS60RX2Wxi71W6uGcEx1mO1k4pZkmS60RfBmxi71S6wJ8Ex1mN1kahJkmS5mRXAexi71S6uIpEx1mN1k4i3kmS5mRf2Sxi71O6wGlEx1mM1kaApkmS5WRf0Wqi71C6oVI0wzmJnly____p3kmS6GRe4axi71a6w0jzWi3_ney0001oryEGVyQN0000lmvi71a6s37Ch1mPnly____n1IlS4yR_F___m00/; fuid01=4f32d3d2000b96ff.foQ1jk9XLP0oR1wjvCPBHB5hSMPqercW7MWUK9_R-u57MVfHcqDMH4f_XbZF1IwqjCrzzOOEfuHzR4Kx5bn9Ba6BPavbr05kJAVd7bGzW_gPSxqEEuQYqglF-9pfb9N7; my=YygBDzYBAQA=; L=Dgk/U2tnTVdpSWNjQXsDBWdZf1tRVldLfCohAFN3KxRPfgBhRgQ1CQYRIAAdTwdZaRwyQy4lOBUIGwNPRxw+Ag==.1402902855.11012.25796.00f2b221ecfcc256e1872f68437b5900; yandex_gid=15; markethistory=<h><cm>109743-6449163</cm><cm>116736-1011401</cm><cm>2431654-10967734</cm><m>2431654-10967734</m><m>116736-1011401</m><m>109743-6449163</m><m>6427101-10890860</m><m>160043-10404627</m><m>160043-10725078</m><m>432460-7867982</m><m>1590061-1592518</m><m>160043-9323459</m><m>2431654-8423347</m><m>2431654-6085349</m><m>2431654-3611336</m><m>160043-10498348</m><m>432460-10634393</m><m>432460-10482376</m><m>432460-10500325</m><m>432460-10604426</m><m>432460-10604425</m><m>432460-10582545</m><m>432460-8524998</m><m>432460-10750956</m><m>432460-10576198</m><m>432460-8353399</m><m>432460-10545050</m><m>432460-10565513</m><m>432460-8229995</m><m>100514-8503513</m><m>100514-9239104</m><m>100514-9288849</m><m>100514-9342348</m><c>100514</c><c>432460</c></h>; marketpers=%7C%7Cc62f9765; yandexmarket=10,RUR,1,,,,2,0,0; Session_id=3:1413612124.5.0.1402902855000:R7bDbQ:8.0|21373176.0.2|118158.4228.3-VX_r_WDD4Rmn04Q8pADQ1GXrE; yandex_login=skynet8080; yp=2147483647.ygo.43:15#1718262855.udn.c2t5bmV0ODA4MA%3D%3D#1426026839.mu.1#1414052066.cnps.9368242353:max; utm_campaign=ru; utm_medium=cpc; utm_source=face; utm_term=accessories; ps_gch=6322802340434573312; pof=505; _ym_visorc_722818=b; ys=wprid.1413832567186149-1523985905161431595917358-ws6-037-NEWS_STORY; cuts=1413833670774; _ym_visorc_160656=b");
//            urlConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            urlConn.setRequestProperty("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
//            //urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
//            urlConn.setRequestProperty("Accept-Encoding", "deflate");
//            //urlConn.setRequestProperty("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7");
//            urlConn.setRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
//            urlConn.setRequestProperty("Connection", "keep-alive");
//            urlConn.setRequestProperty("Cache-Control", "max-age=0");
            urlConn.connect();
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public static void setConnection() {
        try {

            cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setAllowHtmlInsideAttributes(true);
            props.setAllowMultiWordAttributes(true);
            props.setRecognizeUnicodeChars(true);
            props.setOmitComments(true);
            props.setPruneTags("head,noscript,script,style,meta,link");
            myUrl = new URL("http://" + baseurl + "/");
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
        props.setPruneTags("head,noscript,script,style,meta,link");
        PrettyHtmlSerializer ps = new PrettyHtmlSerializer(props);
        StringWriter sw = new StringWriter();
        try {
            ps.write(t, sw, "UTF-8");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        String paragraph = sw.toString().replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
        if (consolmode == 1) {
            System.out.println("======================================");
            System.out.println(paragraph);
        }
        return paragraph;
    }
}