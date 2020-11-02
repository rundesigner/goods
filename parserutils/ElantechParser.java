/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parserutils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import vmre.VMREApp;
import vmre.VMREView;

/**
 *
 * @author grigory
 */
public class ElantechParser {

    public static Integer consolmode = 0;
    public static VMREView v = null;

    public static void main(String[] args) {
        try {
            consolmode = 1;
            getDescription("637079-421");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap getDescription(String sku)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        HashMap resultMap = new HashMap();
        resultMap.put("fulldescription", "");
        resultMap.put("description", "");
        resultMap.put("bigimage", "");
      //  setMessage("Пытаемся скачать URL http://www.elantech.ru/search.php?q=" + sku);
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
        props.setPruneTags("script,style");
        //http://www.elantech.ru/search.php?q=
        // ProLiant+DL320G6+E5520+%281URack+X2%2C26GHzQuadCore%2F8Mb%2F2x2GbUD%2FP212%28ZM%2FRAID1%2B0%2F1%2F0%29%2F1x500GbSATA+LFFHDD%284%29%2FDVDRW%2FiLO2+std%2F2xGigEth%29&s=%CF%EE%E8%F1%EA
//        TagNode t = cleaner.clean(new URL("http://www.elantech.ru/search.php?q=" + sku), "cp1251");
//       // printTag(t, props);
//        try {
//            Object[] found = t.evaluateXPath("//*/div[@class='text']//a/@href");
//            System.out.println("taglenth=" + found.length);
//            if (found.length > 0) {
//                setMessage("Товар найден");
//                String prodUrl = (String) found[0];
//                t = cleaner.clean(new URL("http://www.elantech.ru" + prodUrl), "cp1251");
//                found=t.evaluateXPath("//*/div[@class='imgblock']/a/@href");
//                if(found.length>0){
//                String prodImage = (String)found[0];
//                setMessage("Изображение найдено: http://www.elantech.ru" + prodImage);
//                resultMap.put("bigimage", "http://www.elantech.ru" + prodImage);
//                }
//                found=t.evaluateXPath("//*/div[@class='datasheet']/table");
//                if(found.length>0){
//                String prodFullDescription = printTag(((TagNode)found[0]),props);
//                setMessage("Полное описание найдено");
//                resultMap.put("fulldescription", prodFullDescription);
//                }
//                
//            } else {
//                setMessage("Товар не найден !!!");
//                return resultMap;
//            }
            
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }


        return resultMap;
    }

    public static void setMessage(String s) {
        if (consolmode == 0) {
            if (null == v) {
                v = (VMREView) VMREApp.getApplication().getMainView();
            }
            v.statusMessageLabel.setText(s);
            v.logger.log(Level.ALL, s);
        } else {
            System.out.println(s);
        }
    }

    public static String printTag(TagNode t, CleanerProperties props) {
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
        if(consolmode==1){
        System.out.println("======================================");
        System.out.println(paragraph);
        }
        return paragraph;
    }
}
