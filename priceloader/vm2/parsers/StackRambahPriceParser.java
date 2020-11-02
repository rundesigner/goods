/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2.parsers;

import entries.CategoryNode;
import entries.MediasEntry;
import java.io.IOException;
import jxl.format.CellFormat;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.prefs.Preferences;
import javax.swing.JProgressBar;
import jxl.Cell;
import jxl.format.Font;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import priceloader.vm2.AbstractPriceLoaderVM2;
import tools.Messages;
import utils.vm2.Transliterate;
import vmre.GlobalVars;
import vmre.LocalDB;
import vmre.utils.FileSystem;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class StackRambahPriceParser extends AbstractPriceLoaderVM2 {

    public int sheet = 0;
    /*
     * ячейка в которой хранится наименование товара или артикул
     */
    public int cell4getformat = 1;
    public int user_id = 42;
    public Boolean printDebug = true;
    public String startWord = "Номенклатура";
    /*
     * карта выборки 
     */
    public int product_sku = 0;
    public int product_name = 1;
    public int product_price = 3;
    public int product_manufacturer = 0;
    public int product_desc = 0;
    public int product_s_desc = 0;
    public int manufacturer = 0;
    public int product_instock = 2;
    public int product_weight = 0;
    public int category_name1 = 1;
    public int category_name2 = 0;
    public int category_name3 = 0;
    public Integer maxIndex = 3;
    public int product_metakey = 0;
    public int product_metadesc = 0;
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
     * Части шаблонов
     */
    String patCatDesc1 = "<p><img style=\"float: left; margin-right: 10px;\" src=\"images/stories/virtuemart/category/resized/";
    String patCatDesc2 = "\" alt=\"";
    String patCatDesc3 = "logo 160x100\" width=\"80\" height=\"50\" />Модули увеличения мощности премиум-класса <a href=\"index.php?option=com_content&amp;view=article&amp;id=1&amp;Itemid=117\">Rambach™ PowerBox</a> для автомобилей ";
    String patCatDesc4 = ".&nbsp;</p>";

    public HashMap ParseExcelCats() throws IOException, BiffException{

            String Cat1Name = "";
            HashMap Cats = new HashMap();
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            Messages.show("Прайс для парсинга=" + sfile);
            //loadPrefixes();
            Workbook w = Workbook.getWorkbook(new File(sfile), ws);
            Sheet s = w.getSheet(2);
            Cell[] row = null;
            String val;
            Messages.show("Количество строк в листе категорий =" + s.getRows());
            Boolean start = false;


            for (int i = 0; i < s.getRows(); i++) {
                row = s.getRow(i);

                if (row.length <= cell4getformat) {
                    continue;
                }

                if (!start && (row[cell4getformat].getContents().trim() == null ? startWord == null : row[cell4getformat].getContents().trim().equals("Категория"))) {
                    start = true;
                    continue;
                }

                //начало прайса


                if (!start) {
                    //   Messages.show("Прайс еще не начался");
                    continue;
                }

                CellFormat c = row[cell4getformat].getCellFormat();
                Font f = c.getFont();
                int fontSize = f.getPointSize();
                int fontColor = f.getColour().getValue();
                boolean italic = f.isItalic();
                int bold = f.getBoldWeight();
                int backColor = c.getBackgroundColour().getValue();
                Integer indent = c.getIndentation();

                Messages.show("Обрабатываем строку :" + (i + 1));
                if (printDebug) {
                    Messages.show("Line:" + (i + 1)
                            + "/Lenth=" + row.length
                            + " backColor=" + backColor
                            + " fontColor=" + fontColor
                            + " fontSize=" + fontSize
                            + " isItalic=" + italic
                            + " bold=" + bold
                            + " indent=" + indent);

//                    for (int j = 0; j < row.length; j++) {
//                        Messages.show("Row[" + j + "]" + row[j].getContents());
//                    }
                }
                if (row[cell4getformat].getContents().trim() == null || row[cell4getformat].getContents().trim().equals("")) {
                    continue;
                }
                
                if (row[category_name1].getContents().trim()!=Cat1Name && (!Cats.containsKey(row[category_name1].getContents().trim())) ) {
                    Cat1Name = row[category_name1].getContents().trim();
                    HashMap Cat = new HashMap();
                    for (Integer j = 0; j < 5; j++) {
                        Cat.put("c"+j.toString(), row[j].getContents().trim());
                        System.out.println("c"+j.toString()+"="+row[j].getContents().trim());
                    }
                    Cats.put(row[category_name1].getContents().trim(), Cat);
                    System.out.println(row[category_name1].getContents().trim()+"=");
                }

            }
            return Cats;
        
    }
    
    @Override
    public void ParseExcelPrice(JProgressBar bar, String xml_id) {

        try {
            HashMap cats = ParseExcelCats();
            String Cat1Name = "";
            String Cat2Name = "";
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            Messages.show("Прайс для парсинга=" + sfile);
            //loadPrefixes();
            Workbook w = Workbook.getWorkbook(new File(sfile), ws);
            Sheet s = w.getSheet(sheet);
            Cell[] row = null;
            String val;
            bar.setMinimum(0);
            bar.setMaximum(s.getRows());
            Messages.show("Количество строк в прайсе =" + s.getRows());
            Boolean start = false;
            Stack catStack = new Stack();


            for (int i = 0; i < s.getRows(); i++) {
                row = s.getRow(i);
                bar.setValue(i + 1);
                bar.repaint();


                
                if (row.length <= cell4getformat) {
                    continue;
                }

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
                Font f = c.getFont();
                int fontSize = f.getPointSize();
                int fontColor = f.getColour().getValue();
                boolean italic = f.isItalic();
                int bold = f.getBoldWeight();
                int backColor = c.getBackgroundColour().getValue();
                Integer indent = c.getIndentation();

                
                Messages.show("Обрабатываем строку :" + (i + 1));
                
                if (printDebug) {
                    Messages.show("Line:" + (i + 1)
                            + "/Lenth=" + row.length
                            + " backColor=" + backColor
                            + " fontColor=" + fontColor
                            + " fontSize=" + fontSize
                            + " isItalic=" + italic
                            + " bold=" + bold
                            + " indent=" + indent);

//                    for (int j = 0; j < row.length; j++) {
//                        Messages.show("Row[" + j + "]" + row[j].getContents());
//                    }
                }
                if (row[cell4getformat].getContents().trim() == null || row[cell4getformat].getContents().trim().equals("")) {
                    continue;
                }

////                if (backColor == 43) {
                    if (row[category_name1].getContents().trim()!=Cat1Name) {
                        Cat1Name = row[category_name1].getContents().trim();
                        HashMap catItem = new HashMap();
                        catItem.put("indent", 1);
                        catItem.put("catName", row[category_name1].getContents().trim());
                        catItem.put("catExt", cats.get(Cat1Name));
                        insertPriceCategory(catStack, catItem);
                        syncPriceCategoryForDb(catStack, xml_id);
                        Cat2Name = row[category_name2].getContents().trim();
                        catItem = new HashMap();
                        catItem.put("indent", 2);
                        catItem.put("catName", row[category_name2].getContents().trim());
                        catItem.put("catExt", cats.get(Cat1Name));
                        insertPriceCategory(catStack, catItem);
                        syncPriceCategoryForDb(catStack, xml_id);
                    } else if (row[category_name2].getContents().trim()!=Cat2Name) {
                        Cat2Name = row[category_name2].getContents().trim();
                        HashMap catItem = new HashMap();
                        catItem.put("indent", 2);
                        catItem.put("catName", row[category_name2].getContents().trim());
                        catItem.put("catExt", cats.get(Cat1Name));
                        insertPriceCategory(catStack, catItem);
                        syncPriceCategoryForDb(catStack, xml_id);
                    }
                    System.out.println("========="+((HashMap)cats.get(Cat1Name)).get("c4"));
 ////               if (1==1) continue;
 ////               } else if (backColor == 192) {
                    m=null;
                    m = getEmptyMap();
                    m.put("category_id", ((HashMap) catStack.peek()).get("catId"));
                    m.put("product_name", "Rambach PowerBox для "+row[4].getContents().trim()+" "+row[7].getContents().trim());
                    if (manufacturer > 0) {
                        m.put("manufacturer", row[manufacturer].getContents().trim());
                    }
                    //price
                    Double val2;

                    //   Messages.show("price type=" + row[product_price].getType());
                    if (row[product_price].getType() != CellType.NUMBER) {
                        Messages.show("Ячейка цены не является число строку не обрабатываем" + i + " type=" + row[product_price].getType());
                        continue;
                    }
                    //val2 = getDouble(row[product_price])); 

                    val2 = Double.valueOf(((NumberCell) row[product_price]).getValue());

                    Double marga=1000.0;
                    if(val2>0){
                        val2+=marga;
                    }

                    try {
                        m.put("product_price", val2);
                    } catch (Exception e) {
                        Messages.sboi(e);
                    }
//                    m.put("product_sku", row[product_sku].getContents().trim());
                    m.put("product_sku", "RPB_" + String.valueOf(i));
                    if (product_desc > 0) {
                        m.put("desc", getFullDesc(row));//row[product_desc].getContents().trim());
                    }
                    if (product_s_desc > 0) {
                        m.put("sdesc", "Блок увеличения мощности двигателя и экономии топлива премиум-класса для "
                                +row[4].getContents().trim()
                                +" ("+row[4].getContents().trim()+")\n"
                                +"Производитель: Rambach-Industrie GmbH (Германия)");
                    }
                    if (product_instock > 0) {
                        m.put("instock", row[product_instock].getContents().trim());
                    } else {
                        m.put("instock", 100);
                    }
//                    if (product_instock > 0) {
//                        m.put("weight", row[product_weight].getContents().trim());
//                    } else {
//                        m.put("weight", "0");
//                    }
                    m.put("currency", "RUB");
                    m.put("xml_id", xml_id);
                    
                    /*
                     *  metadesc, metakey 
                     */
                    if (product_metadesc > 0) {
                        m.put("metadesc", 
                                "Модуль увеличения мощности и экономии топлива Rambach PowerBox для безопасного тюнинга двигателя "
                                +row[4].getContents().trim()+" "+row[9].getContents().trim()+" ("+row[3].getContents().trim()+")");
                    } else {
                        m.put("metadesc", "");
                    }
                    if (product_metakey > 0) {
                        m.put("metakey", 
                                "rambach, power, power box, tuning, "
                                +row[2].getContents().trim()+", "+row[1].getContents().trim()+", "+row[9].getContents().trim()
                                +", тюнинг "+row[0].getContents().trim()
                                +", увеличение мощности, двигатель, как увеличить мощность, крутящий момент, экономия");
                    } else {
                        m.put("metakey", "");
                    }
                    

                 /*
                 * Обрабатываем настраиваемые поля
                 */
                    Iterator iterator = customsMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        HashMap custom = (HashMap) customsMap.get(key);
                        Integer column = Integer.parseInt(custom.get("column").toString());
                        //Если задано значение по умолчанию - берем его
                        // если нет берем из колонки
                        if (null != custom.get("default_value")) {
                            m.put("custom" + key, custom.get("default_value"));
                        } else {
                            m.put("custom" + key, row[column].getContents().trim());
                        }
                    }

                    
                    farmPrice1Product();
 ////               }




            }
            Preferences prefs = Preferences.userRoot().node(GlobalVars.name);
            //prefs.put("price_loaded", timestamp.toString().substring(0, 19));
            prefs.put("price_loaded", "");
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public void insertPriceCategory(Stack catStack, HashMap catItem) {
        Integer indent = (Integer) catItem.get("indent");
        if (catStack.empty()) {
            catStack.push(catItem);
            return;
        } else {
            HashMap catItemPrev = (HashMap) catStack.peek();
            Integer indentPrev = (Integer) catItemPrev.get("indent");
            if (indent > indentPrev) {
                catStack.push(catItem);
                return;
            } else if (indent == indentPrev) {
                catStack.pop();
                catStack.push(catItem);
                return;
            } else if (indent < indentPrev) {
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
                
                HashMap catExt = (HashMap) catItem.get("catExt");
                if (catExt!=null) {
                    p.put("category_description", patCatDesc1+catExt.get("c3")+patCatDesc2+catExt.get("c1")+patCatDesc3+catExt.get("c1")+patCatDesc4);
                    p.put("metadesc", "Модули увеличения мощности премиум-класса Rambach™ PowerBox для автомобилей "+catExt.get("c1")+" с турбиро двигателем. Выбор модели автомобиля.");
                    p.put("metakey",catExt.get("c1")+","+catExt.get("c0")+", turbo, tuning, tuning-box, мощность, двигатель, увеличение, rambach, powerbox, крутящий момент, экономия топлива");
                    LocalDB.sqlMap.update("VM2_updCategoriesRuRu", p);
                    
                    MediasEntry entMedia = new MediasEntry();
                    entMedia.setFileDescription("Rambach PowerBox для тюнинга двигателя "+catExt.get("c1"));
                    entMedia.setFileMeta(catExt.get("c1")+", "+catExt.get("c0")+", rambach, power, powerbox, тюнинг, двигатель");
                    entMedia.setFileUrl("images/stories/virtuemart/category/"+catExt.get("c4"));
                    entMedia.setFileUrlThumb("images/stories/virtuemart/category/resized/"+catExt.get("c3"));
                    entMedia.setFileTitle((String)catExt.get("c1"));
                    entMedia.setFileType("category");
                    entMedia.setFileMimetype("image/png");
                    entMedia.setCreatedOn("2000-01-01 01:01:01");
                    entMedia.setLockedOn("2000-01-01 01:01:01");
                    entMedia.setModifiedOn("2000-01-01 01:01:01");
                    LocalDB.sqlMap.insert("insMedias", entMedia);
                    catExt.put("virtuemart_media_id", entMedia.getVirtuemartMediaId());
                    p.put("virtuemart_media_id", entMedia.getVirtuemartMediaId());
                    LocalDB.sqlMap.insert("VM2_insCategoryMedias", p);
                    
                }
            } else {
                //     Messages.show(" Категория " + m.get("category1") + " уже есть в базе !!!");
                cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCategoryId", p);
                HashMap catExt = (HashMap) catItem.get("catExt");
                if (catExt!=null) {
                    p.put("category_description", patCatDesc1+catExt.get("c3")+patCatDesc2+catExt.get("c1")+patCatDesc3+catExt.get("c1")+patCatDesc4);
                    p.put("metadesc", "Модули увеличения мощности премиум-класса Rambach™ PowerBox для автомобилей "+catExt.get("c1")+" с турбиро двигателем. Выбор модели автомобиля.");
                    p.put("metakey",catExt.get("c1")+","+catExt.get("c0")+", turbo, tuning, tuning-box, мощность, двигатель, увеличение, rambach, powerbox, крутящий момент, экономия топлива");
                    p.put("virtuemart_category_id",cnt1);
                    LocalDB.sqlMap.update("VM2_updCategoriesRuRu", p);
                    if (!catExt.containsKey("virtuemart_media_id")) {
                        Integer mediaId = (Integer)LocalDB.sqlMap.queryForObject("getMediaIdForCategory", p.get("virtuemart_category_id"));
                        if (mediaId!=null) {
                            MediasEntry entMedia = (MediasEntry)LocalDB.sqlMap.queryForObject("getMediasForId",mediaId);
                            entMedia.setFileDescription("Rambach PowerBox для тюнинга двигателя "+catExt.get("c1"));
                            entMedia.setFileMeta(catExt.get("c1")+", "+catExt.get("c0")+", rambach, power, powerbox, тюнинг, двигатель");
                            entMedia.setFileUrl("images/stories/virtuemart/category/"+catExt.get("c4"));
                            entMedia.setFileUrlThumb("images/stories/virtuemart/category/resized/"+catExt.get("c3"));
                            entMedia.setFileTitle((String)catExt.get("c1"));
                            entMedia.setFileType("category");
                            entMedia.setFileMimetype("image/png");
                            LocalDB.sqlMap.update("updMedias", entMedia);
                            catExt.put("virtuemart_media_id", entMedia.getVirtuemartMediaId());
                        } else {
                            MediasEntry entMedia = new MediasEntry();
                            entMedia.setFileDescription("Rambach PowerBox для тюнинга двигателя "+catExt.get("c1"));
                            entMedia.setFileMeta(catExt.get("c1")+", "+catExt.get("c0")+", rambach, power, powerbox, тюнинг, двигатель");
                            entMedia.setFileUrl("images/stories/virtuemart/category/"+catExt.get("c4"));
                            entMedia.setFileUrlThumb("images/stories/virtuemart/category/resized/"+catExt.get("c3"));
                            entMedia.setFileTitle((String)catExt.get("c1"));
                            entMedia.setFileType("category");
                            entMedia.setFileMimetype("image/png");
//                    entMedia.setCreatedOn("2000-01-01 01:01:01");
//                    entMedia.setLockedOn("2000-01-01 01:01:01");
//                    entMedia.setModifiedOn("2000-01-01 01:01:01");
                            LocalDB.sqlMap.insert("insMedias", entMedia);
                            catExt.put("virtuemart_media_id", entMedia.getVirtuemartMediaId());
                            p.put("virtuemart_media_id", entMedia.getVirtuemartMediaId());
                            LocalDB.sqlMap.insert("VM2_insCategoryMedias", p);
                        }
                    } else {
                        Integer mediaId = (Integer)LocalDB.sqlMap.queryForObject("getMediaIdForCategory", catExt.get("virtuemart_media_id"));
                        if (mediaId!=null) {
                            MediasEntry entMedia = (MediasEntry)LocalDB.sqlMap.queryForObject("getMediasForId",mediaId);
                            entMedia.setFileDescription("Rambach PowerBox для тюнинга двигателя "+catExt.get("c1"));
                            entMedia.setFileMeta(catExt.get("c1")+", "+catExt.get("c0")+", rambach, power, powerbox, тюнинг, двигатель");
                            entMedia.setFileUrl("images/stories/virtuemart/category/"+catExt.get("c4"));
                            entMedia.setFileUrlThumb("images/stories/virtuemart/category/resized/"+catExt.get("c3"));
                            entMedia.setFileTitle((String)catExt.get("c1"));
                            entMedia.setFileType("category");
                            entMedia.setFileMimetype("image/png");
                            LocalDB.sqlMap.update("updMedias", entMedia);
                        }                        
                    }
                }
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

    public void farmPrice1Product() {
        if (null == m) {
            return;
        }

        p = new HashMap();
        p.put("product_sku", m.get("product_sku").toString());
        p.put("virtuemart_product_id", 0);
        p.put("user_id", user_id);

        //p.put("product_name", delPrefix(m.get("product_name").toString()));
        p.put("product_name", m.get("product_name"));
        p.put("product_s_desc", m.get("sdesc").toString());
        p.put("product_desc", m.get("desc").toString());
        p.put("metakey", m.get("metakey").toString());
        p.put("metadesc", m.get("metadesc").toString());

        p.put("xml_id", m.get("xml_id"));
        p.put("published", "1");
        p.put("xtime", timestamp);
        p.put("product_in_stock", m.get("instock").toString());

        p.put("product_price", Double.parseDouble(m.get("product_price").toString()) * curs);

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
                p.put("product_name", delPrefix(m.get("product_name").toString()));
                LocalDB.sqlMap.insert("VM2_insProductFromTableRURU", p);
                p.put("product_name", m.get("product_name"));
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
                p.put("product_currency", GlobalVars.currency);
                LocalDB.sqlMap.insert("VM2_insPriceFromTable", p);

            } else {
                //   Messages.show("Товар найден получаем данные по товару");
                Integer product_id = (Integer) LocalDB.sqlMap.queryForObject("VM2_getProduct", p);
                p.put("virtuemart_product_id", product_id);
                m.put("virtuemart_product_id", product_id);
                LocalDB.sqlMap.insert("VM2_updPriceProductLocal", p);
                LocalDB.sqlMap.insert("VM2_updPriceValue", p);
                LocalDB.sqlMap.insert("VM2_updProductStock", p);
            }

            //  Messages.show("m="+m.toString());

            m.put("product_id", m.get("virtuemart_product_id"));


            //Настраиваемы поля выставляем
            Iterator iterator = customsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                HashMap custom = (HashMap) customsMap.get(key);
                String value = m.get("custom" + key).toString();
                Integer custom_id = getCustomId(custom);
                HashMap customValue = new HashMap();
                // custom_value#,#virtuemart_custom_id#,#virtuemart_product_id
                customValue.put("custom_value", value);
                customValue.put("virtuemart_custom_id", custom_id);
                customValue.put("virtuemart_product_id", m.get("virtuemart_product_id"));
                //     Messages.show("customValue=" + customValue);
                cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntProductCustomValue", customValue);
                if (cnt1 == 0) {
                    LocalDB.sqlMap.insert("VM2_insProductCustomValue", customValue);
                } else {
                    LocalDB.sqlMap.update("VM2_updProductCustomValue", customValue);
                }
            }

            //Производитель
            if (cnt1 == 0) {
                if (manufacturer > 0) {
                    performManufacturer();
                }
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
        ret=ret+suffix;
        Messages.show("Возвращаем slug="+ret);
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
        ret=ret+suffix;
        Messages.show("Возвращаем slug="+ret);
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
        Messages.show("Копируем файл");
        super.copyFile(fsource, convert);
        File f = new File(fsource);
        String path = f.getParent();
        String fsource1 = path + File.separator + "prefix.xls";
        Messages.show(fsource1);
        String fdest = System.getProperty("user.dir") + File.separator + "csvprices" + File.separator + FileSystem.getFileName("prefix.xls");
        f = new File(fsource1);
        if (f.exists()) {
            FileSystem.copyfile(fsource1, fdest);
        }
        String prefixfile = System.getProperty("user.dir") + File.separator + "settings" + File.separator + "";
        //  loadPrefixes(prefixfile);
    }

    private void loadPrefixes() {
        try {
            File f1 = new File(fsource);
            String path = f1.getParent();
            String fsource1 = path + File.separator + "prefix.xls";
            //Messages.show("Загружаем префиксы");
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            Workbook w = Workbook.getWorkbook(new File(fsource1), ws);
            Sheet s = w.getSheet(sheet);
            Cell[] row = null;
            prefixes = new ArrayList<String>();
            for (int i = 0; i < s.getRows(); i++) {
                row = s.getRow(i);
                if (row.length == 0) {
                    continue;
                }
                //    Messages.show("Добавляем префикс:"+row[0].getContents().trim().toUpperCase());
                prefixes.add(row[0].getContents().trim().toUpperCase());
            }
        } catch (Exception e) {
            Messages.sboi(e);
        }

    }

    private String delPrefix(String name) {
        //    Messages.show("Удаляем префиксы= до"+name);
        if (prefixes == null) {
            return name;
        }
        String nameUpper = name.toUpperCase();

        for (Iterator<String> it = prefixes.iterator(); it.hasNext();) {
            String pref = it.next();
            if (pref.codePointAt(0) != nameUpper.codePointAt(0)) {
                continue;
            }
            if (nameUpper.startsWith(pref)) {
                return name.substring(pref.length()).trim();
            }
        }
        return name;
    }
    
    private String getFullDesc(Cell[] row) {
        return "Rambach PowerBox для "+row[4].getContents().trim()+" "+row[7].getContents().trim()+"\n\n"
                +"Блок увеличения мощности двигателя и экономии топлива премиум-класса для "
                +row[4].getContents().trim()+" ("+row[9].getContents().trim()+")\n\n"
                +"Производитель: Rambach-Industrie GmbH (Германия)\n\n"
                +"<p style=\"text-align: justify;\">Электронный модуль Rambach™ PowerBox предназначен для увеличения мощности и крутящего момента двигателя при одновременном уменьшении расхода топлива без вмешательства в программное обеспечение бортового компьютера автомобиля и защиту блока управления двигателя, установленные производителем транспортного средства "
                +row[0].getContents().trim()+".</p>\n"
                +"<h2 style=\"text-align: left;\"><strong>Преимущества работы "
                +row[4].getContents().trim()+" "+row[7].getContents().trim()+" ("+row[5].getContents().trim()+")"
                +"&nbsp;с установленным&nbsp;<span style=\"text-align: justify;\">&nbsp;Rambach™ PowerBox:</span></strong></h2>"
                +"<p style=\"text-align: left;\">&nbsp;</p><table style=\"width: 100%;\" cellspacing=\"6\" cellpadding=\"6\">"
                +"<tbody><tr><td style=\"width: 50%;\" colspan=\"2\">&nbsp;Стандартные характеристики "
                +row[4].getContents().trim()+" "+row[7].getContents().trim()
                +":</td><td colspan=\"2\"><strong>Характеристики&nbsp;"
                +row[4].getContents().trim()+" "+row[7].getContents().trim()
                +" с установленным модулем Rambach™ PowerBox:</strong></td></tr><tr><td style=\"background-color: #666666;\">"
                +"<p>&nbsp; &nbsp;<span style=\"color: #2b2b2b;\">Мощность (кВт)</span></p></td>"
                +"<td style=\"width: 18%; background-color: #666666; text-align: center;\"><span style=\"color: #2b2b2b;\">"
                +row[6].getContents().trim()
                +"</span></td><td style=\"width: 18%; background-color: #333333; text-align: center;\"><span style=\"color: #ff9900;\">"
                +row[10].getContents().trim()
                +" кВт <br /></span></td><td style=\"background-color: #333333; text-align: center;\"><span style=\"font-size: 12pt;\"><span style=\"color: #ff9900;\">+ "
                +row[13].getContents().trim()
                +"&nbsp;</span></span></td></tr><tr><td style=\"background-color: #666666;\"><p><span style=\"color: #2b2b2b;\">&nbsp; &nbsp;Мощность (л.с.)</span></p></td><td style=\"width: 18%; background-color: #666666; text-align: center;\"><span style=\"color: #2b2b2b;\">"
                +row[7].getContents().trim()
                +"</span></td><td style=\"width: 18%; background-color: #333333; text-align: center;\"><span style=\"color: #ff9900;\">"
                +row[11].getContents().trim()
                +"&nbsp; л.с.</span></td><td style=\"background-color: #333333; text-align: center;\"><span style=\"font-size: 12pt;\"><span style=\"color: #ff9900;\">+ "
                +row[13].getContents().trim()
                +"&nbsp;</span></span></td></tr><tr><td style=\"background-color: #666666;\"><p><span style=\"color: #2b2b2b;\">&nbsp; &nbsp;Крутящий момент (Нм)</span></p></td><td style=\"width: 18%; background-color: #666666; text-align: center;\"><span style=\"color: #2b2b2b;\">"
                +row[8].getContents().trim()
                +" <br /></span></td><td style=\"width: 18%; background-color: #333333; text-align: center;\"><span style=\"color: #ff9900;\">"
                +row[12].getContents().trim()
                +" Нм</span></td><td style=\"background-color: #333333; text-align: center;\"><span style=\"font-size: 12pt;\"><span style=\"color: #ff9900;\">+ "
                +row[14].getContents().trim()
                +"&nbsp;</span></span></td></tr></tbody></table><p><span style=\"text-align: justify;\">"
                +"Разработанный фирмой Rambach-Industrie GmbH и примененный в модуле Rambach™ PowerBox алгоритм оптимального впрыска позволяет достичь экономии топлива до </span>"
                +"<span style=\"color: #ff9900;\">15%</span><span style=\"text-align: justify;\"> от указанных производителем транспортного средства средних показателей.</span>"
                +"</p><p style=\"text-align: justify;\">В отличие от всех прочих технологий тюнинга турбированного двигателя (чип-тюнинг), которые предполагают замену заводскокого алгоритма управления двигателем, внесение изменений или полную замену программного обеспечения завода-изготовителя, технология Rambach не затрагивает ни одну из заводских настроек, а дополняет и корректирует их действия как внешнее устройство.</p>"
                +"<h3 style=\"text-align: justify;\">Основные преимущества Rambach Power Box:</h3><ul style=\"list-style-type: disc;\">"
                +"<li style=\"text-align: justify;\"><h4><a href=\"index.php/video-rambach-vostok?videoid=Qq6KBzNbVW8\" target=\"_blank\">Увеличение мощности "
                +row[4].getContents().trim()+" "+row[7].getContents().trim()
                +" на <span style=\"background-color: #ffffff; color: #ff9900;\">"
                +row[13].getContents().trim()
                +".</a></h4></li><li style=\"text-align: justify;\"><strong><a href=\"index.php/video-rambach-vostok?videoid=c-ax1cieJvk\" target=\"_blank\">Увеличение крутящего момента "
                +row[4].getContents().trim()+" "+row[7].getContents().trim()
                +" на <span style=\"background-color: #ffffff; color: #ff9900;\">"
                +row[14].getContents().trim()
                +".</a></strong></li><li style=\"text-align: justify;\"><strong><a href=\"index.php/video-rambach-vostok?videoid=eTWu9a2eDD4\" target=\"_blank\">Снижение расхода топлива "
                +row[4].getContents().trim()+" "+row[7].getContents().trim()
                +" на <span style=\"background-color: #ffffff; color: #ff9900;\">12%.</a></strong></li><li style=\"text-align: justify;\"><strong><a href=\"index.php/video-rambach-vostok?videoid=yJ99f8kgT3M\" target=\"_blank\">Адаптация к российскому топливу "
                +row[4].getContents().trim()+" "+row[7].getContents().trim()
                +"</a></strong></li><li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=nCVD3_nwicQ\" target=\"_blank\">Тест-драйв 30 дней.</a></li><li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=nYOj3cxwQA8\" target=\"_blank\">Сохранение моторесурса на 100%.</a></li><li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=mAWHdZEL_zI\" target=\"_blank\">Оригинальное программное обеспечение компании Rambach-Industrie GmbH конкретно для "
                +row[3].getContents().trim()+" "+row[7].getContents().trim()+" "+row[9].getContents().trim()
                +".</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=8PNl2MgjFDY\" target=\"_blank\">Неизменность оригинального программного обеспечения, серийной электроники автомобиля и штатной системы защиты двигателя.</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=c-ax1cieJvk\" target=\"_blank\">Сохранность гарантии завода-изготовителя на транспортное средство при установке Rambach™ Power Box.</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=ip0VzpWN5RE\" target=\"_blank\">Легкость установки и возможность переустановки на другой автомобиль.</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=g_ElHO0CSC4\" target=\"_blank\">Наличие официального сертификата МАДИ и TÜV.</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=E3mJu6tHNhw\" target=\"_blank\">Повышенная безопасность управления транспортным средством: установка Rambach™ PowerBox создает дополнительный запас мощности для движения в сложных дорожных и метеорологических условиях, безопасного завершения дорожного маневра или обгона, уверенного управления транспортным средством на дорогах с плохим покрытием или бездорожью.&nbsp;</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=n1XEqzAl82A\" target=\"_blank\">Повышенная безопасность использования модуля увеличения мощности: исключен риск отключения при эксплуатации, корпус устройства пыле- и водонепроницаем, устойчив к вибрации.</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=_PdNH4DJRFU\" target=\"_blank\">Техническая поддержка специалистов официального представительства Rambach-Industrie GmbH в России, при необходимости - непосредственная техническая поддержка со стороны инженеров фирмы Rambach-Industrie GmbH.</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=2wP6aOkVhCE\" target=\"_blank\">Возможность переустановки на другой автомобиль (бесплатно).</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/video-rambach-vostok?videoid=VtusnrI3Bog\">2-летняя гарантия.&nbsp;</a></li>"
                +"<li style=\"text-align: justify;\"><a href=\"index.php/home/delivery-of-rambach\" target=\"_blank\">Бесплатная доставка во все регионы России.</a></li>"
                +"</ul><h4>Комплектация:</h4><ul style=\"list-style-type: square;\"><li><a href=\"index.php/video-rambach-vostok?videoid=8PNl2MgjFDY\" target=\"_blank\">Модуль Rambach Power-Box</a></li><li>Соединительный кабель для "
                +row[4].getContents().trim()+" "+row[9].getContents().trim()
                +"</li><li>Кабельные стяжки</li><li>Проверочный блок (индикатор)</li></ul><p>Инструкцию по установке доступна в электронном виде и формате PDF&nbsp;"
                +"<a href=\"index.php?option=com_content&amp;view=article&amp;id=9&amp;Itemid=152\">на сайте</a>.&nbsp;</p><h4>Сперва попробовать - потом решить? Нет проблем!</h4>"
                +"<p style=\"text-align: justify;\"><img style=\"float: right; margin-left: 10px;\" src=\"images/web-elements/moneyback.png\" alt=\"moneyback\" width=\"100\" height=\"100\" />Официальный представитель фирмы Rambach-Industrie GmbH в России - компания ООО \"Рамбах-Восток\" предоставляет своим клиентам возможность сперва почувствовать силу Rambach в своей машине и лишь затем принять решение о окончательной покупке.</p>"
                +"<p style=\"text-align: justify;\">Помимо 2-летней гарантии от изготовителя устройства и гарантийных обязательств в соответствии с законодательством РФ, мы предоставляем своим клиентам 30-ти дневную гарантию возврата 100% денежных средств, с учетом почтовых расходов.<br /><br />Повышение мощности и крутящего момента (безопасный чип-тюнинг) "
                +row[4].getContents().trim()
                +" возможны благодаря задействованию скрытых резервов двигателя и автомобиля в целом. Производитель закладывает эти резервы по многим причинам:<br />&nbsp; - маркетинговые соображения;<br />&nbsp; - налоговые (подгон характеристик под определенные налоговые классы);<br />&nbsp; - эксплуатация автомобиля в высокогорной местности с разряженным воздухом;</p>";
    }
}

