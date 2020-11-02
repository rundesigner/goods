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
import java.util.Iterator;
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
import vmre.GlobalHacks;
import vmre.GlobalVars;
import vmre.LocalDB;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class SimplePriceParser {

    public String sfile;
    public String fsource;
    public HashMap CategoryStack;
    public HashMap CategoryHash;
    public HashMap p;
    public HashMap m;
    public Double curs;
    public java.sql.Timestamp timestamp;
    public WorkbookSettings ws;
    public int sheet = 0;
    /*
     * ячейка в которой хранится наименование товара или артикул
     */
    public int cell4getformat = 0;
    public int user_id = 42;
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


        Messages.show("StartLine3=" + startLine);
        try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("ru", "RU"));
            Workbook w = Workbook.getWorkbook(new File(sfile), ws);
            Sheet s = w.getSheet(sheet);
            Cell[] row = null;
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


                //    Messages.show(row[cell4getformat].getContents().trim()+"=="+startWord);
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
                Messages.show("Line:" + LineNumber + " StartLine=" + startLine);
                if (startLine > LineNumber) {
                    continue;
                }

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
                        Messages.show("Row[" + j + "]" + row[j].getContents());
                    }
                }


                if (row.length > 0) {
                    //           System.out.println("Line:" + (i + 1) + "/Lenth=" + row.length);
                    //get categories
                    if (null != row[cell4getformat] && !row[cell4getformat].getContents().trim().equalsIgnoreCase("")) {

                        m = getEmptyMap();
                        m.put("product_name", row[product_name].getContents().trim());
                        m.put("category1", row[category_name1].getContents().trim());

                        if (category_name2 > 0) {
                            m.put("category2", row[category_name2].getContents().trim());
                            m.put("notcategory2", "false");
                        } else {
                            m.put("category2", "");
                            m.put("notcategory2", "true");
                        }

                        if (category_name3 > 0) {
                            m.put("category3", row[category_name3].getContents().trim());
                            m.put("notcategory3", "false");
                        } else {
                            m.put("category3", "");
                            m.put("notcategory3", "true");
                        }

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
                        if (product_instock > 0) {
                            m.put("instock", row[product_instock].getContents().trim());
                        } else {
                            m.put("instock", 100);
                        }
                        if (product_weight > 0) {
                            m.put("weight", row[product_weight].getContents().trim());
                        } else {
                            m.put("weight", "0");
                        }
                        m.put("currency", "RUB");
                        m.put("xml_id", xml_id);
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

                        farmCategories();
                        farmPrice1Product();
                    }
                }
            }
            Preferences prefs = Preferences.userRoot().node(GlobalVars.name);
            //prefs.put("price_loaded", timestamp.toString().substring(0, 19));
            prefs.put("price_loaded", "");
        } catch (Exception e) {
            e.printStackTrace();
            Messages.sboi(e);
        }
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

        p.put("product_name", m.get("product_name").toString());
        p.put("product_s_desc", m.get("sdesc").toString());
        p.put("product_desc", m.get("desc").toString());

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
        p.put("parent_id", m.get("category_id" + (maxIndex - 1)).toString());

        p.put("metakey", m.get("product_name").toString());
        p.put("metadesc", m.get("product_name").toString());


        try {

            Integer cnt1 = 0;
            cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_IssetProduct", p);
            Messages.show("cnt1=" + cnt1 + "sku=" + p.get("product_sku").toString());
            Messages.show(p.toString());

            //create new product,
            if (cnt1 == 0) {
                //    Messages.show("Товар не найден создаем");
                LocalDB.sqlMap.insert("VM2_insProductFromTable", p);
                String slug = getProductSlug(p);
                p.put("slug", slug);
                Messages.show("p=" + p.toString());
                LocalDB.sqlMap.insert("VM2_insProductFromTableRURU", p);
                //LocalDB.sqlMap.update("VM2_updPriceProductFull", p);
                p.put("xml_name", m.get("product_name"));
                p.put("xml_sku", m.get("product_sku"));
                LocalDB.sqlMap.insert("VM2_insProductLocal", p);
                m.put("virtuemart_product_id", p.get("virtuemart_product_id"));
                HashMap x = new HashMap();
                x.put("virtuemart_product_id", p.get("virtuemart_product_id"));
                x.put("virtuemart_category_id", m.get("category_id" + maxIndex));
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
                }
            }

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

    public Boolean farmCategories() {

        if (m.get("category1").toString().trim().equalsIgnoreCase("")) {
            Messages.show("Не задано название категории 1!!! Строку не обрабатываем");
            return false;
        }

        Integer index = 1;

        farmPriceCategory(1);
        //обрабатываем остальные категории

        for (index = 2; index <= maxIndex; index++) {
            //Обрабатываем вторую категорию

            if (Boolean.parseBoolean(m.get("notcategory" + index).toString())
                    || m.get("category" + index).toString().trim().equalsIgnoreCase("")) {
                // Messages.show("Не обрабатывать категорию " + index + " уровня ...");
                //Выставляем весь стек категорий
                while (index <= maxIndex) {
                    m.put("category_id" + index, m.get("category_id" + (index - 1)));
                    index++;
                }
                return true;
            } else {
                farmPriceCategory(index);
            }

        }
        return true;
    }

    public HashMap farmPriceCategory(Integer index) {
        //root category
        p = new HashMap();
        CategoryNode n;

        //Messages.show("Задано название категории 1="+m.get("category1")+" обрабатываем ...");
        p.put("category_name", m.get("category" + index).toString().trim());
        if (index == 1) {
            p.put("parent_id", 0);
        } else {
            p.put("parent_id", m.get("category_id" + (index - 1)).toString().trim());
        }
        p.put("xml_id", m.get("xml_id"));
        // Messages.show(p.toString());
        try {

            Integer cnt1 = 0;
            cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCntCategory", p);
            if (cnt1 == 0) {

                // Messages.show(" Категория "+m.get("category1")+" не найдена в базе - добавляем ...");

                //Добавляем категорию в таблицу
                //jos_virtuemart_categories
                if (Integer.parseInt(GlobalVars.db_version) > 11
                        && Integer.parseInt(GlobalVars.db_version) < 17) {
                    LocalDB.sqlMap.insert("VM2_insCat", p);
                } else {
                    LocalDB.sqlMap.insert("VM2_insCat17", p);

                }
                // jos_virtuemart_categories_ru_ru
                p.put("slug", getCategorySlug(p));
                LocalDB.sqlMap.insert("VM2_insCatName", p);
                // таблица связей jos_virtuemart_category_categories
                LocalDB.sqlMap.insert("VM2_insCatXref", p);
                // category_xref
                LocalDB.sqlMap.insert("VM2_insCatXrefLocal", p);

                m.put("category_id" + index, p.get("virtuemart_category_id"));
                //category_state
                p.put("xtime", timestamp);
                p.put("xml_id", m.get("xml_id"));
                LocalDB.sqlMap.insert("VM2_insCatNameLocal", p);

            } else {
                //     Messages.show(" Категория " + m.get("category1") + " уже есть в базе !!!");
                cnt1 = (Integer) LocalDB.sqlMap.queryForObject("VM2_getCategoryId", p);
                m.put("category_id" + index, cnt1);
            }
        } catch (SQLException e) {
            Messages.sboi(e);
        }
        // Messages.show(" Категории данной строки успешно обработаны. Возвращаем результат." + m.toString());
        return m;
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
        ret = ret + suffix;
        //   Messages.show("Возвращаем slug="+ret);
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
}
