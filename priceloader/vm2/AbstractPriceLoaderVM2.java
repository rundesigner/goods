/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.JProgressBar;
import jxl.WorkbookSettings;
import vmre.utils.FileSystem;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class AbstractPriceLoaderVM2 {

    public String sfile;
    public String fsource;
    public HashMap CategoryStack;
    public HashMap CategoryHash;
    public HashMap p;
    public HashMap m;
    public Double curs;
    public java.sql.Timestamp timestamp;
    public WorkbookSettings ws;
    public Integer startLine=1;
    public Integer endLine=0;
    public Integer sheet=0;
    public Integer endSheet=0;

    public void AbstractPriceLoaderVM2() {
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        timestamp = new java.sql.Timestamp(now.getTime());
        ws = new WorkbookSettings();
        ws.setLocale(new Locale("ru", "RU"));
    }

    
    public void setStartLine(Integer i){
        this.startLine=i;
    }
    
    public void setEndLine(Integer i){
        this.endLine=i;
    }
    
    public void setSheet(Integer i){
        this.sheet=i;
    }
    
    public void setEndSheet(Integer i){
        this.endSheet=i;
    }
    
    public void copyFile(String fsource, Boolean convert) {
        String fdest = getDestName(fsource);
        checkDirectory();
        this.fsource=fsource;
        //System.out.println("Input xls1="+fsource);
        FileSystem.copyfile(fsource, fdest);
        sfile = fdest;
    }

    private void checkDirectory() {
        String str = System.getProperty("user.dir") + File.separator + "csvprices";
        File f = new File(str);
        if (!f.isDirectory()) {
            f.mkdir();
        }
    }

    private String getDestName(String str) {
        String fstr = System.getProperty("user.dir") + File.separator + "csvprices" + File.separator + FileSystem.getFileName(str);
        return fstr;
    }
    
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
         
     }

}
