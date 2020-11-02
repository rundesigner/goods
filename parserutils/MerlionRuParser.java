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
public class MerlionRuParser {

    public static Integer consolmode = 0;
    public static VMREView v = null;

    public static void main(String[] args) {
        System.out.println("Тагил !!!");
        try {
            consolmode = 1;
            getDescription(new HashMap(){
                {
                put("sku","673312");
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap getDescription(HashMap param)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        String sku=param.get("sku").toString();
        HashMap resultMap = new HashMap();
        resultMap.put("fulldescription", "");
        resultMap.put("description", "");
        resultMap.put("bigimage", "");
        setMessage("Пытаемся скачать URL http://www.merlion.ru/goods/catalog/product/" + sku + "/");
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
        props.setPruneTags("script,style");
//        TagNode t ; // = cleaner.clean(new URL("http://www.merlion.ru/goods/catalog/product/" + sku + "/"),"cp1251");
//        TagNode t2;
//        
//        String description = "";
//        try {
//           Object[] found = t.evaluateXPath("//div[@id='container_item']//table");
//            System.out.println("taglenth=" + found.length);
//            if (found.length > 0) {
//                t2 = (TagNode) found[0];
//                setMessage("Большое описание найдено");
//                PrettyHtmlSerializer ps = new PrettyHtmlSerializer(props);
//                StringWriter sw = new StringWriter();
//                ps.write(t2, sw, "utf-8");
//                String paragraph = sw.toString().replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
//                description += paragraph;
//            } else {
//                setMessage("Большое описание не найдено");
//            }
//
//            String smalldescription = "";
//
//            resultMap.put("bigimage", "http://img.merlion.ru/items/"+sku+"_v01_b.jpg");
//            resultMap.put("bigimage2", "http://img.merlion.ru/items/"+sku+"_v01_m.jpg");
//            resultMap.put("description", smalldescription);
//            resultMap.put("fulldescription", description);
//            //System.out.println("fulldescr=" + description);
//            
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
}
