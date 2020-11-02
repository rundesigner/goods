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
public class DimohodPriceLoader extends AbstractPriceLoaderVM2 {

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
        pr.startWord="Диаметр";
        
        
        /*
         * Печатаем строку прайса необходимо для отладки.
         */
        pr.printDebug=false;
        pr.timestamp = timestamp;
        pr.ws = ws;
        pr.curs=curs;
        pr.sfile = sfile;
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
        pr.manufacturer = 2;
        pr.product_sku = 4;
        pr.product_name = 5;
        pr.product_price = 6;
        pr.category_name1 = 1;
        /*
         * Ид пользователя который все менял              * 
         */
        pr.user_id = 42;
        /*
         * Настраиваемые поля
         */
        HashMap customsMap = new HashMap() {

            {
                //поле Основной элемент
                put("0",
                        new HashMap() {

                            {
                                put("title", "Основной элемент");
                                put("is_cart_attribute", "1");
                                put("column", "1");
                                put("default_value", "1");
                            }
                        });
                //поле Материал
                put("1",
                        new HashMap() {

                            {
                                put("title", "Материал");
                                put("is_cart_attribute", "1");
                                put("column", "3");
                            }
                        });
                //поле Диаметр
                put("2",
                        new HashMap() {

                            {
                                put("title", "Диаметр");
                                put("is_cart_attribute", "1");
                                put("column", "0");
                            }
                        });
            }
        };

        pr.customsMap = customsMap;
        pr.ParseExcelPrice(bar, xml_id);
    }
}
