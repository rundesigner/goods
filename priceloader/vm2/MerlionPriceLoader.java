/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2;

import javax.swing.JProgressBar;
import priceloader.vm2.parsers.SimplePriceParser;
import tools.Messages;

/**
 *
 * @author grigory
 */
public class MerlionPriceLoader extends AbstractPriceLoaderVM2 {

    
    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
        SimplePriceParser pr = new SimplePriceParser();
        /*
         * ячейка в которой хранится наименование товара или артикул
         */
        pr.cell4getformat = 6;
        pr.user_id = 42;
        pr.printDebug = false;
        pr.startWord = "Наименование";
    //    pr.startLine=xstartLine;
        
         /*
         * Печатаем строку прайса необходимо для отладки.
         */
        pr.printDebug=false;
        pr.timestamp = timestamp;
        Messages.show("timestamp="+pr.timestamp);
        pr.ws = ws;
        pr.curs=curs;
        pr.sfile = sfile;
        Messages.show("Файл="+sfile);
        pr.fsource = fsource;
       Messages.show("Файл2="+fsource);
         /*
         * Ищем 1- по имени ; 0 - по артикулу
         */
        pr.findByName = 0;
        /*
         * Проверяем подчиненность категории
         */
        pr.checkCategory = false;
        
        /*
         * карта выборки 
         */
        pr.product_sku = 4;
        pr.product_name = 6;
        pr.product_price = 9;
//        pr.product_manufacturer = 0;
//        pr.product_desc = 0;
//        pr.product_s_desc = 0;
//        pr.manufacturer = 0;
//        pr.product_instock = 2;
//        pr.product_weight = 0;
        pr.category_name1 = 0;
        pr.category_name2 = 1;
        pr.category_name3 = 2;
        pr.maxIndex = 3;
        

        Messages.show("StartLine99="+startLine);

        pr.setStartLine(startLine);
        pr.ParseExcelPrice(bar, xml_id);
    }
}