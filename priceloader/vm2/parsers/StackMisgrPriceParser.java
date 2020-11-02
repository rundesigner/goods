/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2.parsers;

import entries.CategoryNode;
import jxl.format.CellFormat;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import javax.swing.JProgressBar;
import jxl.Cell;
import jxl.format.Font;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import priceloader.vm2.AbstractPriceLoaderVM2;
import tools.Messages;
import utils.vm2.Transliterate;
import vmre.GlobalVars;
import vmre.LocalDB;
import vmre.utils.FileSystem;
import vmre.utils.GlobalClasses;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class StackMisgrPriceParser extends AbstractPriceLoaderVM2 {

    public int sheet = 0;
    /*
     * ячейка в которой хранится наименование товара или артикул
     */
    public int cell4getformat = 2;
    public int user_id = 42;
    public Boolean printDebug = false;
    public String startWord = "Наименование товаров";
    /*
     * карта выборки 
     */
    public int product_sku = 1;
    public int product_name = 2;
    public int product_price = 4;
    public int product_manufacturer = 0;
    public int product_desc = 0;
    public int product_s_desc = 6;
    public int manufacturer = 0;
    public int product_instock = 5;
    public int product_in_yaschik = 3;
    public int product_weight = 0;
    public int category_name1 = 2;
    public int category_name2 = 0;
    public int category_name3 = 0;
    public Integer maxIndex = 3;
    /*
     * Настраиваемые поля
     */
    public HashMap customsMap = new HashMap();
    /*
     * Различные настройки
     */
    public int findByName = 0;
    public Boolean checkCategory = false;
    /*
     * Кеш для настраиваемых полей
     */
    public HashMap customIdCache = new HashMap();
    /*
     * Массив префиксов
     */
    public List<String> prefixes = null;
    
    /*
     * Пробуем потоковую загрузку
     */
     private static int THREAD_NUMBER = 15;
     private ExecutorService exec = Executors.newFixedThreadPool(THREAD_NUMBER);
     
    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {

        try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("uk", "UA"));
            Workbook w = Workbook.getWorkbook(new File(sfile), ws);
            Sheet s = w.getSheet(sheet);
            Cell[] row = null;
            String val;
            bar.setMinimum(0);
            bar.setMaximum(s.getRows());
            Messages.show("Количество строк в прайсе =" + s.getRows());
            Boolean start = false;
            Stack catStack= new Stack(); 


            for (int i = 0; i < s.getRows(); i++) {
                row = s.getRow(i);
                bar.setValue(i + 1);
                bar.repaint();

                if (row.length < cell4getformat) continue;

                if (!start && (row[cell4getformat].getContents().trim() == null ? startWord == null : row[cell4getformat].getContents().trim().equals(startWord))) {
                    start = true;
                    continue;
                }

                //начало прайса


                if (!start) {
                    //   Messages.show("Прайс еще не начался");
                    continue;
                }

                CellFormat c = row[cell4getformat].getCellFormat();
                if(row[cell4getformat].getContents().trim().equalsIgnoreCase("")){
                    continue;
                }
                Font f = c.getFont();
                int fontSize = f.getPointSize();
                int fontColor = f.getColour().getValue();
                boolean italic = f.isItalic();
                int bold = f.getBoldWeight();
                int backColor = c.getBackgroundColour().getValue();
                //Integer indent = c.getIndentation();
                Integer indent = 1;

                if (printDebug) {
                    Messages.show("Line:" + (i + 1)
                            + "/Lenth=" + row.length
                            + " backColor=" + backColor
                            + " fontColor=" + fontColor
                            + " fontSize=" + fontSize
                            + " isItalic=" + italic
                            + " bold=" + bold
                            + " indent=" + indent);

                    for (int j = 0; j < row.length; j++) {
                        Messages.show("Row[" + j + "]" + row[j].getContents());
                    }
                }else{
                   Messages.show("Обрабатывается строка:" + (i + 1)); 
                }
                
                if (row[cell4getformat].getContents().trim() == null || row[cell4getformat].getContents().trim().equals("")) continue;

                if (bold==700) {
                    HashMap catItem = new HashMap();
                    catItem.put("indent", indent);
                    catItem.put("catName", row[category_name1].getContents().trim());
                    insertPriceCategory(catStack, catItem);
                    syncPriceCategoryForDb(catStack, xml_id);
                    continue;
                } else {
                    m = getEmptyMap();
                    m.put("category_id", ((HashMap) catStack.peek()).get("catId"));
                    m.put("product_name", row[product_name].getContents().trim());
                    if (manufacturer > 0) {
                        m.put("manufacturer", row[manufacturer].getContents().trim());
                    }
                    //price
                    Double val2;

                 //   Messages.show("price type=" + row[product_price].getType());
                    if (row[product_price].getType() != CellType.NUMBER) {
                        Messages.show("Ячейка цены не является число строку не обрабатываем" + i+" type="+row[product_price].getType());
                        continue;
                    }
                    //val2 = getDouble(row[product_price])); 

                    val2 = Double.valueOf(((NumberCell) row[product_price]).getValue());


                    try {
                        m.put("product_price", val2);
                    } catch (Exception e) {
                        Messages.sboi(e);
                    }
                    m.put("product_sku", row[product_sku].getContents().trim());
                    if (product_desc > 0) {
                        m.put("desc", row[product_desc].getContents().trim());
                    }
                    if (product_s_desc > 0) {
                        try{
                        m.put("sdesc", row[product_s_desc].getContents().trim());
                        }catch(Exception ex){
                        m.put("sdesc", "");    
                        }
                    }
                    if (product_instock > 0) {
                        m.put("instock", row[product_instock].getContents().trim());
                        if(null==row[product_instock].getContents().trim()){
                            m.put("instock", 0);
                        }
                    } else {
                        m.put("instock", 0);
                    }
                    
                    m.put("inyaschik",row[product_in_yaschik].getContents().trim());
                    Preferences prefs = GlobalClasses.getPrefs();
                    m.put("currency",prefs.get("currency","199") );
                    m.put("xml_id", xml_id);
                    m.put("xtime", timestamp);
                    
                   // farmPrice1ProductThread(m);
                    farmPrice1Product(m);
                }



                
                
            }
            Preferences prefs = Preferences.userRoot().node(GlobalVars.name);
            prefs.put("price_loaded", timestamp.toString().substring(0, 19));
            
            //Убираем с показа те товары, которых не было в прайсе.
            unpublishNonPriceProducts();
            
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }
    
    public void unpublishNonPriceProducts(){
       try{
           LocalDB.sqlMap.update("unpublishNonPriceProducts",timestamp+"");
       }catch(Exception e){
           Messages.sboi(e);
       } 
    }
    
    public void farmPrice1ProductThread(HashMap m) {
        final HashMap z=m;
        exec.execute(new Runnable() {
            public void run() {
                Messages.show("Обрабатываем товар:"+z.get("product_name")+z.toString());
                farmPrice1Product(z);
            }
        });
    }

    public void insertPriceCategory(Stack catStack, HashMap catItem) {
                    Integer indent = (Integer) catItem.get("indent");
                    if (catStack.empty()) {
                        catStack.push(catItem);
                        return;
                    } else {
                        HashMap catItemPrev = (HashMap) catStack.peek();
                        Integer indentPrev = (Integer) catItemPrev.get("indent");
                        if (indent>indentPrev) {
                            catStack.push(catItem);
                            return;
                        } else if (indent==indentPrev) {
                            catStack.pop();
                            catStack.push(catItem);
                            return;
                        } else if (indent<indentPrev) {
                            catStack.pop();
                            insertPriceCategory(catStack, catItem);
                            return;
                        }
                    }
    }
    public HashMap syncPriceCategoryForDb(Stack catStack, String xml_id) {
        //root category
        p = new HashMap();
        CategoryNode n;
        
        HashMap catItem = (HashMap) catStack.pop();
        p.put("category_name", catItem.get("catName").toString().trim());
        if (catStack.empty()) {
            p.put("parent_id", 0);
        } else {
            HashMap catItemPrev = (HashMap) catStack.peek();
            p.put("parent_id", catItemPrev.get("catId"));
        }
        p.put("xml_id", xml_id);
        try {

            Integer cnt1 = 0;
            cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntCategory", p);
            if (cnt1 == 0) {

                // Messages.show(" Категория "+m.get("category1")+" не найдена в базе - добавляем ...");

                //Добавляем категорию в таблицу
                //jos_virtuemart_categories
                LocalDB.sqlMap.insert("VM2_insCat", p);
                // jos_virtuemart_categories_ru_ru
                p.put("slug", getCategorySlug(p));
                LocalDB.sqlMap.insert("VM2_insCatName", p);
                // таблица связей jos_virtuemart_category_categories
                LocalDB.sqlMap.insert("VM2_insCatXref", p);
                // category_xref
                LocalDB.sqlMap.insert("VM2_insCatXrefLocal", p);

                catItem.put("catId", p.get("virtuemart_category_id"));
                //category_state
                p.put("xtime", timestamp);
                p.put("xml_id", xml_id);
                LocalDB.sqlMap.insert("VM2_insCatNameLocal", p);

            } else {
           //     Messages.show(" Категория " + m.get("category1") + " уже есть в базе !!!");
                cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCategoryId", p);
                catItem.put("catId", cnt1);
            }
            catStack.push(catItem);
        } catch (SQLException e) {
            Messages.sboi(e);
        }
       // Messages.show(" Категории данной строки успешно обработаны. Возвращаем результат." + m.toString());
        return m;
    }

    public Double getDouble(Cell cell) {
        Double ret = 0.0;
        if (cell.getType() == CellType.NUMBER
                || cell.getType() == CellType.NUMBER_FORMULA) {
            ret = Double.valueOf(((NumberCell) cell).getValue());
        }
        return ret;
    }

    public void farmPrice1Product(HashMap m) {
        if (null == m) {
            return;
        }

        p = new HashMap();
        p.put("product_sku", m.get("product_sku").toString());
        p.put("virtuemart_product_id", 0);
        p.put("user_id", user_id);

        p.put("product_name", delPrefix(m.get("product_name").toString()));
        
        p.put("product_s_desc", m.get("sdesc").toString());
        p.put("product_desc", m.get("desc").toString());

        p.put("xml_id", m.get("xml_id"));
        p.put("published", "1");
        p.put("xtime", m.get("xtime"));
        p.put("product_in_stock", m.get("instock").toString());

        p.put("product_price", Double.parseDouble(m.get("product_price").toString()) * curs);
        p.put("inyaschik", m.get("inyaschik").toString());

        if (checkCategory) {
            p.put("checkcategory", 1);
        } else {
            p.put("checkcategory", 0);
        }
        p.put("findByName", findByName);


        try {

            Integer cnt1 = 0;
            cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_IssetProduct", p);


            //create new product,
            if (cnt1 == 0) {
                //    Messages.show("Товар не найден создаем");
                LocalDB.sqlMap.insert("VM2_insProductFromTable", p);
                String slug = getProductSlug(p);
                p.put("slug", slug);
                LocalDB.sqlMap.insert("VM2_insProductFromTableRURU", p);
                //LocalDB.sqlMap.update("VM2_updPriceProductFull", p);
                p.put("xml_name", m.get("product_name"));
                p.put("xml_sku", m.get("product_sku"));
                LocalDB.sqlMap.insert("VM2_insProductLocal", p);
                m.put("virtuemart_product_id", p.get("virtuemart_product_id"));
                HashMap x = new HashMap();
                x.put("virtuemart_product_id", p.get("virtuemart_product_id"));
                x.put("virtuemart_category_id", m.get("category_id"));
                //Messages.show("x="+x.toString());
                LocalDB.sqlMap.insert("VM2_insProductCategoryXref", x);
                LocalDB.sqlMap.insert("VM2_insProductCategoryXrefLocal", x);
                Integer shgrpid = (Integer) LocalDB.sqlMap.queryForObject("VM2_getDefaultShG");
                p.put("virtuemart_shoppergroup_id", shgrpid);
                p.put("product_currency", m.get("currency"));
                LocalDB.sqlMap.insert("VM2_insPriceFromTable", p);

            } else {
                //   Messages.show("Товар найден получаем данные по товару");
                Integer product_id = (Integer) LocalDB.sqlMap.queryForObject("VM2_getProduct", p);
                p.put("virtuemart_product_id", product_id);
                m.put("virtuemart_product_id", product_id);
                LocalDB.sqlMap.insert("VM2_updPriceProductLocal", p);
                LocalDB.sqlMap.insert("VM2_updPriceValue", p);
                LocalDB.sqlMap.insert("VM2_updProductStock", p);
                LocalDB.sqlMap.insert("VM2_updProductSdesc", p);
            }

            //  Messages.show("m="+m.toString());

            m.put("product_id", m.get("virtuemart_product_id"));
            
            p.put("product_params","min_order_level=\"\"|max_order_level=\"\"|step_order_level=\"\"|product_box=\""+p.get("inyaschik")+"\"|");
            LocalDB.sqlMap.insert("VM2_updProductStockInYaschik", p);

            //Производитель
            if (manufacturer > 0) {
                performManufacturer();
            }
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public void performManufacturer() {
        try {
            //Проверяем производителя если нет - создаем. Если есть - обновляем.
            Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntManufacturers", m);
            //создаем произовдителя
            if (cnt == 0) {
                //   Messages.show("Производителя не нашли - создаем нового");
                // jos_virtuemart_manufacturers
                LocalDB.sqlMap.insert("VM2_insManufacturer", m);
                m.put("mf_slug", getManufacturerSlug());
                //jos_virtuemart_manufacturers_ru_ru
                LocalDB.sqlMap.insert("VM2_insManufacturerName", m);
            } else {
                //  Messages.show("Производителя нашли - получаем ид");

                Integer mf_id = (Integer) LocalDB.sqlMap.queryForObject("VM2_getManufacturerId", m);
                m.put("virtuemart_manufacturer_id", mf_id);
            }
            //Проверяем есть ли связь товар-произовдитель
            // Messages.show("manuf m="+m.toString());
            cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntProductManufacturerXref", m);
            if (cnt == 0) {
                LocalDB.sqlMap.insert("VM2_insProductManufacturerXref", m);
            }

        } catch (Exception e) {
            Messages.sboi(e);
        }
    }


    public String getManufacturerSlug() {
        String ret = "";
        ret = Transliterate.toFolderName(m.get("manufacturer").toString());
        Boolean check = false;
        String suffix = "";
        Integer index = 0;
        while (!check) {
            try {
                Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntManufacturerSlug", ret + suffix);
                if (cnt == 0) {
                    check = true;
                } else {
                    index++;
                    suffix = "-" + index;
                }
            } catch (Exception e) {
                Messages.sboi(e);
            }

        }
        return ret;
    }

    public String getCategorySlug(HashMap p) {
        String ret = "";
        ret = Transliterate.toFolderName(p.get("category_name").toString());
        Boolean check = false;
        String suffix = "";
        Integer index = 0;
        while (!check) {
            try {
                Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntCategorySlug", ret + suffix);
                if (cnt == 0) {
                    check = true;
                } else {
                    index++;
                    suffix = "-" + index;
                }
            } catch (Exception e) {
                Messages.sboi(e);
            }

        }
        return ret;
    }

    public String getProductSlug(HashMap p) {
        String ret = "";
        ret = Transliterate.toFolderName(p.get("product_name").toString());
        Boolean check = false;
        String suffix = "";
        Integer index = 0;
        while (!check) {
            try {
                Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntProductSlug", ret + suffix);
                if (cnt == 0) {
                    check = true;
                } else {
                    index++;
                    suffix = "-" + index;
                }
            } catch (Exception e) {
                Messages.sboi(e);
            }

        }
        return ret;
    }

    public HashMap getEmptyMap() {
        HashMap m = new HashMap();
        m.put("product_sku", "");
        m.put("product_name", "");
        m.put("sdesc", "");
        m.put("desc", "");
        m.put("instock", 0);
        m.put("product_price", 0.0);
        return m;
    }

    public Integer getCustomId(HashMap custom) {
        Integer ret = 0;

        String name = custom.get("title").toString();
        String key = Transliterate.toFolderName(name);
        if (null == customIdCache.get(key)) {
            try {
                Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntCustomIdByName", name);
                if (cnt == 0) {
                    LocalDB.sqlMap.insert("VM2_insCustomByName", custom);
                }
                ret = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCustomIdByName", name);
                customIdCache.put(key, ret);
            } catch (Exception e) {
                Messages.sboi(e);
            }
        } else {
            ret = Integer.parseInt(customIdCache.get(key).toString());
        }
        return ret;
    }
    

    @Override
    public void copyFile(String fsource, Boolean convert) {
        super.copyFile(fsource, convert);
        File f = new File(fsource);
        String path = f.getParent();
        String fsource1 = path + File.separator+ "prefix.xls";
        Messages.show(fsource1);
        String fdest = System.getProperty("user.dir") + File.separator + "csvprices" + File.separator + FileSystem.getFileName("prefix.xls");
        f = new File(fsource1);
        if (f.exists()) {
               FileSystem.copyfile(fsource1, fdest);
               loadPrefixes(fdest);
        }
    }
    
    private void loadPrefixes(String f) {
        try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            Workbook w = Workbook.getWorkbook(new File(f), ws);
            Sheet s = w.getSheet(sheet);
            Cell[] row = null;
            prefixes = new ArrayList<String>();
            for (int i = 0; i < s.getRows(); i++) {
                row = s.getRow(i);
                if (row.length == 0) continue;
                prefixes.add(row[0].getContents().trim().toUpperCase());
            }
        } catch (Exception e) {
            Messages.sboi(e);
        }
        
    }
    
    private String delPrefix(String name) {
        if (prefixes==null) return name;
        for (Iterator<String> it = prefixes.iterator(); it.hasNext();) {
            String pref = it.next();
            if (name.toUpperCase().startsWith(pref)) return name.substring(pref.length());
        }
        return name;
    }
    
    
    
}
