/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parserutils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.ParserConfigurationException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.xml.sax.SAXException;

/**
 *
 * @author grigory
 */
public class OptionScraper {
    // example XPATH queries in the form of strings - will be used later
    private static final String NAME_XPATH = "//*/div[@class='b-model-friendly']";

   // TagNode object, its use will come in later
    private static TagNode node;

    public static void main(String[] args) {
        try{
        getOptionFromName("MP3 плеер Explay T35TV 4 Гб");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
           System.out.println("END");
    }

    // a method that helps me retrieve the stock option's data based off the name (i.e. GOUAA is one of Google's stock options)
    public static void getOptionFromName(String name) throws XPatherException, ParserConfigurationException,SAXException, IOException, XPatherException {

        // the URL whose HTML I want to retrieve and parse
        String option_url = "http://market.yandex.ru/search.xml?text=" + name + "&cvredirect=1";

        // this is where the HtmlCleaner comes in, I initialize it here
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);

        // open a connection to the desired URL
     WebFile file = new WebFile("http://market.yandex.ru/search.xml?text=" + name + "&cvredirect=1");

        //use the cleaner to "clean" the HTML and return it as a TagNode object
        node = cleaner.clean(file.getContent().toString());

        // once the HTML is cleaned, then you can run your XPATH expressions on the node, which will then return an array of TagNode objects (these are returned as Objects but get casted below)
        Object[] info_nodes = node.evaluateXPath(NAME_XPATH);


        // here I just do a simple check to make sure that my XPATH was correct and that an actual node(s) was returned
        if (info_nodes.length > 0) {
            // casted to a TagNode
            TagNode info_node = (TagNode) info_nodes[0];
            // how to retrieve the contents as a string
            //String info = info_node.getChildren().iterator().next().toString().trim();

            // some method that processes the string of information (in my case, this was the stock quote, etc)
         //   System.out.println(info);
        }else{
            System.out.println("NO");
        }
        
    }

}
