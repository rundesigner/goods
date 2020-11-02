/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2;

import java.util.HashMap;
import javax.swing.JProgressBar;
import priceloader.vm2.parsers.SimplePriceParser;

/**
 *
 * @author grigory
 */
public class StandartPriceLoader extends AbstractPriceLoaderVM2 {

    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
        SimplePriceParser pr = new SimplePriceParser();
        /*
         * Столбец из которой брать формат
         */
        pr.cell4getformat=0;
        /*
         * Слово с которого начинается таблица товаров из ячейки  cell4getformat
         */
        pr.startWord="Категория 1";
        
        
        /*
         * Печатаем строку прайса необходимо для отладки.
         */
        pr.printDebug=true;
        pr.timestamp = timestamp;
        pr.ws = ws;
        pr.curs=curs;
        pr.sfile = sfile;
        pr.fsource = fsource;
        /*
         * Ищем 1- по имени ; 0 - по артикулу
         */
        pr.findByName = 0;
        /*
         * Проверяем подчиненность категории
         */
        pr.checkCategory = false;
        /*
         * Карта столбцов
         */
        pr.manufacturer = -1;
        pr.product_sku = 3;
        pr.product_name = 4;
        pr.product_price = 5;
        pr.category_name1 = 0;
        pr.category_name2 = 1;
        pr.category_name3 = 2;
        pr.product_instock = 7;
        /*
         * Ид пользователя который все менял              * 
         */
        pr.user_id = 42;
        /*
         * Настраиваемые поля
         */
        HashMap customsMap = new HashMap() ;

        pr.customsMap = customsMap;
        
        pr.ParseExcelPrice(bar, xml_id);
        
    }
}