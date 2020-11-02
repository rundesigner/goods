/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parserutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import vmre.VMREApp;
import vmre.VMREView;

/**
 *
 * @author grigory
 */
public class JustcomParser {

    public static void main(String[] args) {
        System.out.println("Тагил !!!");
        try {
            getDescription("183246");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap getDescription(String sku)
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {
        VMREView v = (VMREView) VMREApp.getApplication().getMainView();
        HashMap resultMap = new HashMap();
        resultMap.put("fulldescription", "");
        resultMap.put("description", "");
        resultMap.put("bigimage", "");
        v.statusMessageLabel.setText("Пытаемся скачать URL http://www.justcom.ru/goods/" + sku + "/");
        WebFile file = new WebFile("http://www.justcom.ru/goods/" + sku + "/");
        String MIME = file.getMIMEType();
        Object content = file.getContent();
        v.statusMessageLabel.setText("Скачан url:" + file.getURL());
        v.logger.log(Level.ALL,"Download url:" + file.getURL());
        System.out.println("download URL=" + file.getURL());


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
            Object[] found = t.evaluateXPath("//*/div[@id='tab_text_descr']/ul");
            System.out.println("taglenth=" + found.length);
            if (found.length > 0) {
              v.statusMessageLabel.setText("Большое описание найдено");
              v.logger.log(Level.ALL,"Большое описание найдено");
                t2 = (TagNode) found[0];
                System.out.println("t2=" + t2.toString());
                if (t2.hasChildren()) {
                    List linodes = t2.getChildTagList();
                    description += "<table style=\"width:100%;\">";

                    for (int k = 0; k < linodes.size(); k++) {
                        //  System.out.println("Linodes==="+k+"="+linodes.item(k).getNodeName());
                        if (((TagNode) linodes.get(k)).getName().toString().equalsIgnoreCase("li")) {
                            description += "<tr>";
                            List linodes1 = ((TagNode) linodes.get(k)).getChildTagList();
                            for (int l = 0; l < linodes1.size(); l++) {
                                    description += "<td style=\"width:300px;\">" + ((TagNode) linodes1.get(l)).getText().toString().replaceAll("\n", "<br/>") + "</td>";
                            }
                            description += "</tr>";

                        }
                    }
                    description += "</table>";
                }
            }else{
              v.statusMessageLabel.setText("Большое описание не найдено");
              v.logger.log(Level.ALL,"Большое описание не найдено");
            }

            String smalldescription = "";
            found = t.evaluateXPath("//*/td[@style='vertical-align: top; padding: 10px']/p");
            System.out.println("taglenth2=" + found.length);
            if (found.length > 0) {
                v.statusMessageLabel.setText("Малое описание найдено");
              v.logger.log(Level.ALL,"Малое описание найдено");
                t2 = (TagNode) found[0];
                smalldescription="<p>"+t2.getText().toString()+"</p>";
             }else{
                v.statusMessageLabel.setText("Большое описание найдено");
                v.logger.log(Level.ALL,"Большое описание найдено");
             }

            resultMap.put("bigimage", "http://fast.justcom.ru/good_big_pics/"+sku+".jpg");
            resultMap.put("description", smalldescription);
            resultMap.put("fulldescription", description);
            System.out.println("fulldescr=" + description);
            System.out.println("smalldescr=" + smalldescription);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return resultMap;
    }
}
