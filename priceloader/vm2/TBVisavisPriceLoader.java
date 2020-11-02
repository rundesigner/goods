/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2;

import java.util.HashMap;
import javax.swing.JProgressBar;
import priceloader.vm2.parsers.TBVisavisPriceParser;

/**
 *
 * @author grigory
 */
public class TBVisavisPriceLoader extends AbstractPriceLoaderVM2 {
  
    public Integer xstartLine = 1;
    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
        TBVisavisPriceParser pr = new TBVisavisPriceParser();
        /*
         * Печатаем строку прайса необходимо для отладки.
         */
        pr.printDebug=false;
        /*
         * Столбец из которой брать формат
         */
        pr.cell4getformat=0;
        /*
         * Слово с которого начинается таблица товаров из ячейки  cell4getformat
         */
        pr.startWord="№";
        
        
        
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
        pr.product_sku = 1;
        pr.product_name = 2;
        pr.product_s_desc = 3;
      //  pr.priznak = 4;
        pr.product_price = 5;      //базовая цена  
        pr.skidka = 6;        
        pr.product_price2 = 7;  // цена со скидкой
        pr.skidka2 = 8;  // максимальная скидка
        pr.inbox = 9;  
        pr.razmer = 10;  
        pr.razmer1 = 11;  
        pr.razmer2 = 12;  
        pr.razmer3 = 13;  
        pr.razmer4 = 14;  
        pr.razmer5 = 15;  
        pr.razmer6 = 16;  
        pr.image = 1;  
        pr.color = 2;  
        
        pr.category_name1 = -1;
        pr.category_name2 = -1;
        pr.category_name3 = -1;
        
        
        
        pr.product_instock = -1;
        pr.manufacturer = -1;
        
        pr.product_desc = 4;
        /*
         * Ид пользователя который все менял              * 
         */
        pr.user_id = 42;
        pr.startLine = xstartLine;
        /*
         * Настраиваемые поля
         */
        HashMap customsMap = new HashMap() ;

        pr.customsMap = customsMap;
        
        pr.ParseExcelPrice(bar, xml_id);
        
    }
    
    public void setStartLine(Integer i) {
        this.xstartLine = i;
    }

}