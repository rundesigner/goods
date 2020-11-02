/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package priceloader.vm2.parsers;

import entries.CategoryNode;
import jxl.format.CellFormat;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JProgressBar;
import jxl.Cell;
import jxl.format.Font;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import tools.Messages;
import utils.vm2.Transliterate;
import vmre.GlobalVars;
import vmre.LocalDB;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class TBVisavisPriceParser {

    public String sfile;
    public String fsource;
    public HashMap CategoryStack;
    public HashMap CategoryHash;
    public HashMap p;
    public HashMap p1;
    public HashMap m;
    public HashMap m1;
    public HashMap ch;
    public HashMap ch1;
    public Double curs;
    public java.sql.Timestamp timestamp;
    public WorkbookSettings ws;
    public int sheet = 0;
    /*
     * ячейка в которой хранится наименование товара или артикул
     */
    public int cell4getformat = 0;
    public int user_id = 525;
    public Boolean printDebug = false;
    public String startWord = "";
    /*
     * карта выборки 
     */
    public int product_sku = 0;
    public int product_name = 0;
    public int product_price = 0;
    public int product_manufacturer = 0;
    public int product_desc = 0;
    public int product_s_desc = 0;
    public int manufacturer = 0;
    public int product_instock = 0;
    public int product_weight = 0;
    public int category_name1 = 0;
    public int category_name2 = 0;
    public int category_name3 = 0;
    public int priznak = 0;
    public int skidka = 0;
    public int skidka2 = 0;
    public int product_price2 = 0;
    public int inbox = 0;
    public int razmer = 0;
    public int razmer1 = 0;
    public int razmer2 = 0;
    public int razmer3 = 0;
    public int razmer4 = 0;
    public int razmer5 = 0;
    public int razmer6 = 0;
    public int image = 0;
    public int color = 0;
    public String xml_ids = "1";
    public Integer maxIndex = 3;
    public Integer startLine = 1;
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

    public void setStartLine(Integer i) {
        this.startLine = i;
    }

    public void ParseExcelPrice(JProgressBar bar, String xml_id) {
        xml_ids = xml_id;
        setEmptyMaps();
        Messages.show("TBVisavi price parser StartLine3=" + startLine);
        try {
            ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            Workbook w = Workbook.getWorkbook(new File(sfile), ws);
            Sheet s = w.getSheet(sheet);
            Cell[] row = null;
            Cell[] rowSub = null;
            String val;
            bar.setMinimum(0);
            bar.setMaximum(s.getRows());
            Messages.show("Количество строк в прайсе =" + s.getRows());
            Boolean start = false;
            Integer LineNumber = 0;
            for (int i = 0; i < s.getRows(); i++) {
                row = s.getRow(i);
                bar.setValue(i + 1);
                bar.repaint();
                //     Messages.show(row[cell4getformat].getContents().trim()+"=="+startWord);
                if (row.length <= cell4getformat) {
                    continue;
                }
                if (!start && row[cell4getformat].getContents().trim().equalsIgnoreCase(startWord)) {
                    start = true;
                    continue;
                }
                //начало прайса
                if (!start) {
                    //   Messages.show("Прайс еще не начался");
                    continue;
                }
                LineNumber = (i + 1);

                if (startLine > 0 && i < startLine) {
                    // Messages.show("Еще не достигли начальной строки, заданной в настройках startLine="+startLine+" > i ="+i);
                    continue;
                }
                Messages.show("Line:" + LineNumber + " StartLine=" + startLine);
//                if (startLine > LineNumber) {
//                    continue;
//                }

                Messages.show(i + "");
                CellFormat c = row[cell4getformat].getCellFormat();
                Font f = c.getFont();
                int fontSize = f.getPointSize();
                int fontColor = f.getColour().getValue();
                boolean italic = f.isItalic();
                int backColor = c.getBackgroundColour().getValue();


                if (printDebug) {
                    Messages.show("Line:" + (i + 1)
                            + "/Lenth=" + row.length
                            + " backColor=" + backColor
                            + " fontColor=" + fontColor
                            + " fontSize=" + fontSize
                            + " isItalic=" + italic);
                    for (int j = 0; j < row.length; j++) {
                        c = row[j].getCellFormat();
                        f = c.getFont();
                        fontSize = f.getPointSize();
                        fontColor = f.getColour().getValue();
                        italic = f.isItalic();
                        backColor = c.getBackgroundColour().getValue();
//                        Messages.show("Row[" + j + "]" + row[j].getContents() + "format=" + " backColor=" + backColor
//                                + " fontColor=" + fontColor
//                                + " fontSize=" + fontSize
//                                + " isItalic=" + italic);
                    }
                    continue;
                }
                if (row.length > 0) {
                    //           System.out.println("Line:" + (i + 1) + "/Lenth=" + row.length);
                    //get categories
                    if (null != row[cell4getformat] && !row[cell4getformat].getContents().trim().equalsIgnoreCase("")) {

                        // Если это строка содержит начало товара номер товара по прайсу
                        if (row[0].getContents().trim().equalsIgnoreCase("#") || Integer.parseInt(row[0].getContents().trim()) > 0) {
                            m = (HashMap) m1.clone();
                            m.put("product_name", row[product_name].getContents().trim());
                            //Выбор категории по артикулу
                            m.put("categoryId", 30); //значение по умолчанию
                            //назначаем категорию по артикулу
                            if (row[product_sku].getContents().length() < 3) {
                                continue;
                            }

                            //    Messages.show("sub_sku="+sub_sku);
                            //price
                            Double val2;
                            //   Messages.show("price type=" + row[product_price].getType());
                            if (row[product_price].getType() != CellType.NUMBER) {
                                Messages.show("Ячейка цены не является число строку не обрабатываем" + i + " type=" + row[product_price].getType());
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
                                m.put("sdesc", row[product_s_desc].getContents().trim());
                            }

                            /*
                             * Выбираем строки дочерних товаров
                             */
                            int sub_i = i + 1; // внутренний индекс строк для перебора строк эксель
                            int cnt_sub = 1; // счетчик цветов 
                            String idx_product_str = row[0].getContents().trim();
                            if (idx_product_str.equalsIgnoreCase("#")) {
                                // Messages.show("Загрузка прайса Visavi завершена");
                                break;
                            }
                            int idx_product = Integer.parseInt(idx_product_str); // Номер по порядку для прайса
                            //      Messages.show("Номер товара впрайсе=" + idx_product);
                            // Размеры
                            int ri = 1;
                            int cnt_razmer = 0;
                            int cell_razmer = 0;
                            for (ri = 1; ri <= 7; ri++) {
                                cell_razmer = 9 + ri;
                                String xrazmer = row[cell_razmer].getContents().trim();
                                if (!xrazmer.equalsIgnoreCase("")) {
                                    m.put("razmer" + ri, xrazmer);
                                    cnt_razmer++;
                                }
                            }
                            m.put("cnt_razmer", cnt_razmer);
                            rowSub = s.getRow(sub_i);
                            int idx_product_next = idx_product + 1;

                            int idx_product_subrow = getSubrowIdx(rowSub);
                            //  Messages.show("idx_product_subrow="+idx_product_subrow);

                            int all_kolvo = 0;
                            do {
                                //забиваем из саброу данные в массив
                                m.put("cnt_sub", cnt_sub);
                                //выбираем значение цвет
                                m.put("sub_color_" + cnt_sub, rowSub[2].getContents().trim());

                                //выбираем значение размеров                                
                                for (ri = 1; ri <= cnt_razmer; ri++) {
                                    cell_razmer = 9 + ri;
                                    //    Messages.show("cell_razmer=" + cell_razmer);
                                    c = rowSub[cell_razmer].getCellFormat();
                                    // 192 - есть на складе много
                                    // 23 - нет на складе
                                    // желтый - ограниченое количество
                                    backColor = c.getBackgroundColour().getValue();
                                    int kolvo = 10;
                                    if (backColor == 192) {
                                        kolvo = 1000;
                                    }
                                    if (backColor == 23) {
                                        kolvo = 0;
                                    }
                                    m.put("sub_color_ramer_kolvo" + cnt_sub + "_" + ri, kolvo);
                                    all_kolvo += kolvo;
                                }
                                m.put("instock", all_kolvo);
                                // находим   idx_product_subrow для следующей строки   
                                sub_i++;
                                cnt_sub++;
                                rowSub = s.getRow(sub_i);
                                idx_product_subrow = getSubrowIdx(rowSub);
                                if (idx_product_subrow == 999999) {
                                    //Messages.show("Загрузка прайса Visavi завершена");
                                    break;
                                }
                                //       Messages.show("idx_product_subrow=" + idx_product_subrow + "===" + idx_product_next);
                            } while (idx_product_next != idx_product_subrow);

                            // уменьшает основной счетчик строк 
                            i = sub_i - 1;
                            //Если выбрали все по товару запускаем запросы в базу                          
                            //Messages.show("m=" + m.toString());
                            farmPrice1Product();

                        }
                    }
                }
            }
            Preferences prefs = Preferences.userRoot().node(GlobalVars.name);
            prefs.put("price_loaded", timestamp.toString().substring(0, 19));
            //prefs.put("price_loaded", "");
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public int getSubrowIdx(Cell[] rowSub) {
        int ret = 0;
        String val = rowSub[0].getContents().trim();
        if (val.equalsIgnoreCase("#")) {
            ret = 999999;
        } else {
            if (!val.equalsIgnoreCase("")) {
                ret = Integer.parseInt(val);
            }
        }

        return ret;
    }

    public Double getDouble(Cell cell) {
        Double ret = 0.0;
        if (cell.getType() == CellType.NUMBER
                || cell.getType() == CellType.NUMBER_FORMULA) {
            ret = Double.valueOf(((NumberCell) cell).getValue());
        }
        return ret;
    }

    public int getDefaultShopperGroup() {
        int ret = 2;
        try {
            ret = (Integer) LocalDB.sqlMap.queryForObject("VM2_getDefaultShG");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public void insertChildProductPrice() {
        try {
            Integer shgrpid = getDefaultShopperGroup();
            ch.put("virtuemart_shoppergroup_id", shgrpid);
            LocalDB.sqlMap.insert("VM3_insPriceFromTableTB", ch);
            LocalDB.sqlMap.insert("VM3_insPriceFromTableTB_Opt", ch);
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public void InsertProductPrice() {
        try {
            Integer shgrpid = getDefaultShopperGroup();
            p.put("virtuemart_shoppergroup_id", shgrpid);
            p.put("product_currency", GlobalVars.currency);
            LocalDB.sqlMap.insert("VM3_insPriceFromTableTB", p);
            // для оптовиков цены
            LocalDB.sqlMap.insert("VM3_insPriceFromTableTB_Opt", p);
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public void InsertProductCategoryXref() {
        try {
            HashMap x = new HashMap();
            x.put("virtuemart_product_id", p.get("virtuemart_product_id"));
            String sub_sku = m.get("product_sku").toString().trim().substring(0, 2);
            String sub_sku_3 = m.get("product_sku").toString().trim().substring(0, 3);

            m.put("categoryId", 30);
            //#носки
            if (sub_sku.equalsIgnoreCase("CL")) {
                m.put("categoryId", 33);
            }

            //#корректирующее белье
            if (sub_sku.equalsIgnoreCase("DU")
                    || sub_sku_3.equalsIgnoreCase("LHP")
                    || sub_sku.equalsIgnoreCase("SG")) {
                m.put("categoryId", 31);
            }

            //#футболки для девочек 'GF%' OR p.product_sku like 'GM%' 
            if (sub_sku.equalsIgnoreCase("GF")
                    || sub_sku.equalsIgnoreCase("GM")) {
                m.put("categoryId", 35);
            }

            // #трусы для девочек


            //нижнее белье
            if (sub_sku.equalsIgnoreCase("AC")
                    || sub_sku.equalsIgnoreCase("BF")) {
                m.put("categoryId", 23);
            }


            x.put("virtuemart_category_id", m.get("categoryId"));
            LocalDB.sqlMap.insert("VM2_insProductCategoryXref", x);
            LocalDB.sqlMap.insert("VM2_insProductCategoryXrefLocal", x);
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public int getCntChildProduct() {
        int ret = 0;
        try {
            ret = (Integer) LocalDB.sqlMap.queryForObject("VM3_getcntchildproductTB", ch);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public int insertChildProduct() {
        int ret = 0;
        try {
            LocalDB.sqlMap.insert("VM2_insProductFromTable", ch);
            String slug = getProductSlug(ch);
            ch.put("slug", slug);
//            Messages.show("ch=" + ch.toString());
//            (#virtuemart_product_id#, #product_s_desc#, #product_desc#, #product_name#, #slug#, #metadesc#, #metakey#);
            ch.put("product_s_desc", p.get("product_s_desc"));
            ch.put("product_desc", p.get("product_desc"));
            ch.put("product_name", p.get("product_name"));
            ch.put("metadesc", p.get("metadesc"));
            ch.put("metakey", p.get("metakey"));
            LocalDB.sqlMap.insert("VM2_insProductFromTableRURU", ch);
//             #product_price#, 0,         0.00000, 0, -1, #product_currency#
            ch.put("product_currency", p.get("product_currency"));
            ch.put("user_id", p.get("user_id"));
            LocalDB.sqlMap.insert("VM3_insPriceFromTableTB", ch);
            ch.put("xml_name", ch.get("product_name"));
            ch.put("xml_sku", ch.get("product_sku"));
            LocalDB.sqlMap.insert("VM2_insProductLocal", ch);
//            m.put("virtuemart_product_id", p.get("virtuemart_product_id"));
            ch.put("product_id", ch.get("virtuemart_product_id"));
//            ret = Integer.parseInt(p.get("virtuemart_product_id").toString());

            //вставляем настраиваемые поля
            ch.put("customfield_params", "addEmpty=0|selectType=0|");
            ch.put("customfield_value", ch.get("color"));
            ch.put("virtuemart_custom_id", 21);
            LocalDB.sqlMap.insert("VM3_insProductCustomValue", ch);
            ch.put("customfield_value", ch.get("razmer"));
            ch.put("virtuemart_custom_id", 22);
            //  Messages.show("ch razmer="+ch.toString());
            LocalDB.sqlMap.insert("VM3_insProductCustomValue", ch);



            //добавляем настраиваемое поле stockablecustomfields к родительскому товару
            String cfparams = "parentOrderable=0|custom_id=\"\"|child_product_id=\"" + ch.get("virtuemart_product_id") + "\"|";
            p.put("customfield_params", cfparams);
            p.put("virtuemart_custom_id", 23);
            p.put("customfield_value", "stockablecustomfields");
            LocalDB.sqlMap.insert("VM3_insProductCustomValue", p);

        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public void setZeroStockAndUpdatePriceAllChild() {
        try {
            LocalDB.sqlMap.insert("VM3_setChildZeroStock", ch);
            LocalDB.sqlMap.insert("VM3_setChildPrice", ch);
            //для оптовиков
            LocalDB.sqlMap.insert("VM3_setChildPrice_Opt", ch);
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public void performChildProducts() {

        //TO DO
//SUBS!!!        
        //cnt_razmer=2,        Количество размеров
        int cnt_razmer = Integer.parseInt(m.get("cnt_razmer").toString());
//cnt_sub=3,           Количество цветов
        int cnt_sub = Integer.parseInt(m.get("cnt_sub").toString());
        int i = 0;
        int j = 0;
        int cnt = 0;
        String sub_color_ramer_kolvo = "sub_color_ramer_kolvo";
        String sub_color = "sub_color";
        String sub_razmer = "razmer";

        String sub_color_ramer_kolvo_key = "";
        String sub_color_key = "";
        String razmer_key = "";

        ch = (HashMap) ch1.clone();
        ch.put("parent_id", m.get("product_id"));
        ch.put("product_price", m.get("product_price"));

        setZeroStockAndUpdatePriceAllChild();

        // Обнуляем количество на складе и выставляем новую цену у всех дочерних товаров
        for (i = 1; i <= cnt_razmer; i++) {
            for (j = 1; j <= cnt_sub; j++) {
                sub_color_ramer_kolvo_key = sub_color_ramer_kolvo + j + "_" + i;
                sub_color_key = sub_color + "_" + i;
                razmer_key = sub_razmer + i;

                ch.put("color", m.get(sub_color_key));
                //Messages.show("razmer_key="+razmer_key);
                // Messages.show("m="+m.toString());
                ch.put("razmer", m.get(razmer_key));
                ch.put("product_sku", p.get("product_sku"));
                if (null == m.get(razmer_key)
                        || null == m.get(sub_color_key)) {
                    //    Messages.show("Ключи "+"|razmer_key="+razmer_key+"|sub_color_key="+sub_color_key+" неполные данные");
                    continue;
                }
                //Messages.show("Ключ количества="+sub_color_ramer_kolvo_key);
                ch.put("product_in_stock", m.get(sub_color_ramer_kolvo_key));

                cnt = getCntChildProduct();
                if (cnt == 0) {
                    insertChildProduct();
                    insertChildProductPrice();
                } else {
                    //   Messages.show("Товар найден получаем данные по товару");
                    getLocalChildProduct(); //получаем ид в базе                    
                    updateLocalChildProduct(); // обновляем количество на складе и цену
                }
            }
        }
//sub_color_ramer_kolvo1_1=10,        
//sub_color_ramer_kolvo1_2=10,        
//sub_color_ramer_kolvo2_1=1000,        
//sub_color_ramer_kolvo2_2=10,        
//sub_color_ramer_kolvo3_1=10,        
//sub_color_ramer_kolvo3_2=10
//sub_color_1=L.GREY MELANGE B,        
//sub_color_2=L.GREY MELANGE C,        
//sub_color_3=WHITE C
//razmer1=80, 
//razmer2=85, 
    }

    public Integer getCntproduct() {
        int ret = 0;
        try {
            ret = (Integer) LocalDB.sqlMap.queryForObject("VM3_IssetProductTB", p);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public Integer InsertProduct() {
        int ret = 0;
        try {
            p.put("xtime", timestamp);
            LocalDB.sqlMap.insert("VM2_insProductFromTable", p);
            String slug = getProductSlug(p);
            p.put("slug", slug);
            //  Messages.show("p=" + p.toString());
            LocalDB.sqlMap.insert("VM2_insProductFromTableRURU", p);
            //LocalDB.sqlMap.update("VM2_updPriceProductFull", p);
            p.put("xml_name", m.get("product_name"));
            p.put("xml_sku", m.get("product_sku"));
            LocalDB.sqlMap.insert("VM2_insProductLocal", p);
            m.put("virtuemart_product_id", p.get("virtuemart_product_id"));
            m.put("product_id", m.get("virtuemart_product_id"));
            ret = Integer.parseInt(p.get("virtuemart_product_id").toString());
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public void updateLocalChildProduct() {
        try {
            LocalDB.sqlMap.insert("VM2_updPriceProductLocal", ch);
//            LocalDB.sqlMap.insert("VM3_setChildPrice", ch);
//              product_in_stock = #product_in_stock#
//        ,published = #published#
//        WHERE virtuemart_product_id = #virtuemart_product_id#
            ch.put("virtuemart_shoppergroup_id", 1);
            LocalDB.sqlMap.insert("VM3_updPriceValueTB", ch);
            ch.put("virtuemart_shoppergroup_id", 4);
            LocalDB.sqlMap.insert("VM3_updPriceValueTB", ch);
            LocalDB.sqlMap.insert("VM2_updProductStock", ch);
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public void updateLocalProduct() {
        try {
            LocalDB.sqlMap.insert("VM2_updPriceProductLocal", p);
            p.put("virtuemart_shoppergroup_id", 1);
            LocalDB.sqlMap.insert("VM3_updPriceValueTB", p);
            p.put("virtuemart_shoppergroup_id", 4);
            LocalDB.sqlMap.insert("VM3_updPriceValueTB", p);
            LocalDB.sqlMap.insert("VM2_updProductStock", p);
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public Integer getLocalChildProduct() {
        int ret = 0;
        try {
            Integer product_id = (Integer) LocalDB.sqlMap.queryForObject("VM3_getChildProductTB", ch);
            ch.put("virtuemart_product_id", product_id);
            ch.put("product_id", product_id);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public Integer getLocalProduct() {
        int ret = 0;
        try {
            Integer product_id = (Integer) LocalDB.sqlMap.queryForObject("VM3_getProductTB", p);
            p.put("virtuemart_product_id", product_id);
            m.put("virtuemart_product_id", product_id);
            m.put("product_id", product_id);
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }

    public void farmPrice1Product() {
        if (null == m) {
            return;
        }
//m={
//  product_name=BF0160 бюстгальтер,        
//, sdesc=95% хлопок, 5% эластан        
//, desc=Нежность натурал.хлопка!        
//, weight=0,         
//, product_price=315.0,        
//categoryId=23,
//xml_id=1,
//currency=RUB,        
//product_sku=BF0160,        
//manufacturer=,
//instock=1050
        //Формируем Главный товар
        p = (HashMap) p1.clone();
        p.put("product_sku", m.get("product_sku").toString());
        p.put("product_name", m.get("product_name").toString());
        p.put("product_s_desc", m.get("sdesc").toString());
        p.put("product_desc", m.get("desc").toString());
        p.put("xml_id", m.get("xml_id"));
        p.put("published", "1");
        p.put("xtime", timestamp);
        p.put("product_in_stock", m.get("instock").toString());
        p.put("product_price", Double.parseDouble(m.get("product_price").toString()) * curs);
        p.put("checkcategory", 0);
        p.put("findByName", findByName);
        p.put("metakey", m.get("product_name").toString());
        p.put("metadesc", m.get("product_name").toString());
        //Есть ли в локальной базе товар
        Integer cnt1 = getCntproduct();
        //create new product,
        if (cnt1 == 0) {
            //  Messages.show("Товар не найден создаем");
            InsertProduct(); //создаем товар в базе
            InsertProductCategoryXref(); //создаем связь товар-категория
            InsertProductPrice(); //создаем связь товар-категория
        } else {
         //    Messages.show("Товар найден получаем данные по товару");
            getLocalProduct(); //получаем ид в базе
            updateLocalProduct(); // обновляем количество на складе и цену
        }
       //     Messages.show("m="+m.toString());
        //Производитель            
        performManufacturer();
        // добавляем в базу дочерние товары
        performChildProducts();
    }

    public void performManufacturer() {

        Integer cnt = 0;
        
        try {
            if (!(Integer.parseInt(m.get("virtuemart_manufacturer_id").toString()) > 0)) {
                //Проверяем производителя если нет - создаем. Если есть - обновляем.
                cnt = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntManufacturers", m);
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
        ret = ret + suffix;
        //   Messages.show("Возвращаем slug="+ret);
        return ret;
    }

    public void setEmptyMaps() {
        ch1 = p1 = m1 = new HashMap();

        m1.put("product_sku", "");
        m1.put("product_name", "");
        m1.put("sdesc", "");
        m1.put("desc", "");
        m1.put("weight", 0);
        m1.put("instock", 0);
        m1.put("product_price", 0.0);
        m1.put("manufacturer", "");
        m1.put("xml_id", xml_ids);
        m1.put("categoryId", 30);
        //m1.put("currency", "RUB");
        m1.put("currency", GlobalVars.currency);
        //"Vis-a-vis" id=9
        m1.put("virtuemart_manufacturer_id", 9);

        p1.put("product_sku", "");
        p1.put("virtuemart_product_id", 0);
        p1.put("user_id", user_id);
        p1.put("parent_id", 0);
        p1.put("product_name", "");
        p1.put("product_s_desc", "");
        p1.put("product_desc", "");
        p1.put("xml_id", xml_ids);
        p1.put("published", "1");
        p1.put("xtime", timestamp);
        p1.put("product_in_stock", 0);
        p1.put("product_price", 0.0);
        p1.put("checkcategory", 0);
        p1.put("findByName", findByName);
        p1.put("metakey", "");
        p1.put("metadesc", "");
        p1.put("product_currency", GlobalVars.currency);

        ch1.put("parent_id", 0);
        ch1.put("color", "");
        ch1.put("razmer", "");
        ch1.put("price", 0.0);
        ch1.put("published", 1);
        ch1.put("product_sku", "");
        ch1.put("product_in_stock", 0);
        ch1.put("xtime", timestamp);
        ch1.put("xml_id", xml_ids);
        ch1.put("xml_name", "");
        ch1.put("xml_sku", "");
        ch1.put("slug", "");
        ch1.put("product_s_desc", "");
        ch1.put("product_desc", "");
        ch1.put("product_name", "");
        ch1.put("metadesc", "");
        ch1.put("metakey", "");
        ch1.put("currency", GlobalVars.currency);
        ch1.put("product_currency", GlobalVars.currency);

////sub_color_ramer_kolvo1_1=10,        
////sub_color_ramer_kolvo1_2=10,        
////sub_color_ramer_kolvo2_1=1000,        
////sub_color_ramer_kolvo2_2=10,        
////sub_color_ramer_kolvo3_1=10,        
////sub_color_ramer_kolvo3_2=10
////sub_color_1=L.GREY MELANGE B,        
////sub_color_2=L.GREY MELANGE C,        
////sub_color_3=WHITE C
////razmer1=80, 
////razmer2=85, 


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
}
