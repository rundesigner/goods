/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.File;
import java.util.Locale;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vmre.utils.FileSystem;
import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Blank;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author grigory
 */
public class ExcelTest {

    public static void main(String[] args) {
        try {
            String src = System.getProperty("user.dir") + File.separator + "forms" + File.separator + "torg12.xls";
            String dest = System.getProperty("user.dir") + File.separator + "torg12.xls";
            // FileSystem.copyfile(src, dest);

            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            File inputWorkbook = new File(src);
            File outputWorkbook = new File(dest);
            Workbook w1 = Workbook.getWorkbook(inputWorkbook);
            WritableWorkbook w2 = Workbook.createWorkbook(outputWorkbook, w1);
            WritableSheet sheet = w2.getSheet(0);

            WritableCell cell = null;
            CellFormat cf = null;
            Label l = null;
            WritableCellFeatures wcf = null;
            //
            Label label = new Label(0, 6, "       Тут я записал охрененную вещь");
            sheet.addCell(label);
            w2.write();
            w2.close();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
