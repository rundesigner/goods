/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2;

import java.util.HashMap;
import javax.swing.JProgressBar;
import priceloader.vm2.parsers.StackRambahPriceParser;

/**
 *
 * @author grigory
 */
public class StackRambahPriceLoader extends AbstractPriceLoaderVM2 {

    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
        StackRambahPriceParser pr = new StackRambahPriceParser();
        /*
         * ячейка в которой хранится наименование товара или артикул
         */
        pr.cell4getformat = 1;
        pr.user_id = 42;
        pr.startWord = "Категории";
        
        
         /*
         * Печатаем строку прайса необходимо для отладки.
         */
        pr.printDebug=false;
        pr.timestamp = timestamp;
        pr.ws = ws;
        pr.curs=curs;
        pr.sfile = sfile;
        pr.fsource = fsource;
       
         /*
         * Ищем 1- по имени ; 0 - по артикулу
         */
        pr.findByName = 1;
        /*
         * Проверяем подчиненность категории
         */
        pr.checkCategory = false;
        
        /*
         * карта выборки 
         */
        pr.product_sku = 0;
        pr.product_name = 0;
        pr.product_price = 15;
        pr.product_manufacturer = 0;
        pr.product_desc = 4;
        pr.product_s_desc = 4;
        pr.manufacturer = 0;
        pr.product_instock = 0;
        pr.product_weight = 0;
        pr.category_name1 = 1;
        pr.category_name2 = 2;
        pr.category_name3 = 0;
        pr.maxIndex = 3;
        pr.product_metadesc = 1;
        pr.product_metakey = 1;
        
        HashMap cf = new HashMap();
        cf.put("column", 5);
        cf.put("title", "Объем двигателя, см3");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c01", cf);

        cf = new HashMap();
        cf.put("column", 6);
        cf.put("title", "Мощность (оригинал), кВт");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c02", cf);

        cf = new HashMap();
        cf.put("column", 7);
        cf.put("title", "Объем двигателя (оригинал), л.с.");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c03", cf);
        
        cf = new HashMap();
        cf.put("column", 8);
        cf.put("title", "Крутящий момент (оригинал), Нм");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c04", cf);

        cf = new HashMap();
        cf.put("column", 10);
        cf.put("title", "Мощность (с RPB), кВт");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c05", cf);

        cf = new HashMap();
        cf.put("column", 11);
        cf.put("title", "Объем двигателя (с RPB), л.с.");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c06", cf);
        
        cf = new HashMap();
        cf.put("column", 12);
        cf.put("title", "Крутящий момент (с RPB), Нм");
        cf.put("is_cart_attribute", 1);
        pr.customsMap.put("c07", cf);
        
        cf = new HashMap();
        cf.put("column", 9);
        cf.put("title", "Вид топлива");
        cf.put("is_cart_attribute", 0);
        pr.customsMap.put("c08", cf);

        pr.ParseExcelPrice(bar, xml_id);
    }
}