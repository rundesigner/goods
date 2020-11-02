/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2;

import javax.swing.JProgressBar;
import priceloader.vm2.parsers.StackRadiopokupkaPriceParser;
import tools.Messages;

/**
 *
 * @author grigory
 */
public class StackRadiopokupkaPriceLoader extends AbstractPriceLoaderVM2 {

    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
        StackRadiopokupkaPriceParser pr = new StackRadiopokupkaPriceParser();
        /*
         * столбец в которой хранится наименование товара или артикул
         */
        pr.cell4getformat = 1;
        pr.user_id = 42;
        pr.startWord = "Наименование";
        //    pr.startLine=xstartLine;

        /*
         * Печатаем строку прайса необходимо для отладки.
         */
        pr.printDebug = false;
        pr.timestamp = timestamp;
        Messages.show("timestamp=" + pr.timestamp);
        pr.ws = ws;
        pr.curs = curs;
        pr.sfile = sfile;
       // Messages.show("Файл=" + sfile);
        pr.fsource = fsource;
      //  Messages.show("Файл2=" + fsource);
        /*
         * Ищем 1- по имени ; 0 - по артикулу
         */
        pr.findByName = 0;
        /*
         * Проверяем подчиненность категории
         */
        pr.checkCategory = false;



      //  Messages.show("StartLine99=" + startLine);

        Integer maxSheets = 19;

        pr.setStartLine(startLine);
        pr.setEndLine(endLine);
        pr.setSheet(sheet);
        Messages.show("Запускаем обработку sheet="+sheet);
        pr.ParseExcelPrice(bar, xml_id);
        sheet++;
        
        while (sheet <= maxSheets) {
            if(sheet<=endSheet){
            pr.setStartLine(1);
            pr.setEndLine(0);
            pr.setSheet(sheet);
            Messages.show("Запускаем обработку sheet="+sheet);
            pr.ParseExcelPrice(bar, xml_id);            
            }
            sheet++;
        }


    }
}