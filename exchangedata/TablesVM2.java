/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exchangedata;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class TablesVM2 {

    public static HashMap TableNames = new HashMap() {

        {
            put("jos_virtuemart_products", "virtuemart_products" + "-" + " ORDER BY virtuemart_product_id ");
            put("jos_virtuemart_products_ru_ru", "virtuemart_products_ru_ru" + "-" + " ORDER BY  virtuemart_product_id  ");
            put("jos_virtuemart_products_en_gb", "virtuemart_products_en_gb" + "-" + " ORDER BY  virtuemart_product_id  ");
            put("jos_virtuemart_product_prices", "virtuemart_product_prices" + "-" + " ORDER BY virtuemart_product_price_id ");
            put("jos_virtuemart_categories", "virtuemart_categories" + "-" + " ORDER BY virtuemart_category_id ");
            put("jos_virtuemart_categories_ru_ru", "virtuemart_categories_ru_ru" + "-" + " ORDER BY virtuemart_category_id ");
            put("jos_virtuemart_categories_en_gb", "virtuemart_categories_en_gb" + "-" + " ORDER BY virtuemart_category_id ");
            put("jos_virtuemart_category_medias", "virtuemart_category_medias" + "-" + " ORDER BY id ");
            put("jos_virtuemart_category_categories", "virtuemart_category_categories" + "-" + " ORDER BY id ");
            put("jos_virtuemart_product_categories", "virtuemart_product_categories" + "-" + " ORDER BY id ");
            put("jos_virtuemart_shoppergroups", "virtuemart_shoppergroups" + "-" + " ORDER BY virtuemart_shoppergroup_id ");
            put("jos_virtuemart_vmuser_shoppergroups", "virtuemart_vmuser_shoppergroups" + "-" + " ORDER BY id ");
            put("jos_virtuemart_medias", "virtuemart_medias" + "-" + " ORDER BY virtuemart_media_id ");
            put("jos_virtuemart_product_medias", "virtuemart_product_medias" + "-" + " ORDER BY id ");
            put("jos_virtuemart_manufacturers", "virtuemart_manufacturers" + "-" + " ORDER BY virtuemart_manufacturer_id ");
            put("jos_virtuemart_manufacturers_ru_ru", "virtuemart_manufacturers_ru_ru" + "-" + " ORDER BY virtuemart_manufacturer_id ");
            put("jos_virtuemart_manufacturers_en_gb", "virtuemart_manufacturers_en_gb" + "-" + " ORDER BY virtuemart_manufacturer_id ");
            put("jos_virtuemart_manufacturer_medias", "virtuemart_manufacturer_medias" + "-" + " ORDER BY id ");
            put("jos_virtuemart_manufacturercategories", "virtuemart_manufacturercategories" + "-" + " ORDER BY virtuemart_manufacturercategories_id ");
            put("jos_virtuemart_manufacturercategories_ru_ru", "virtuemart_manufacturercategories_ru_ru" + "-" + " ORDER BY virtuemart_manufacturercategories_id ");
            put("jos_virtuemart_manufacturercategories_en_gb", "virtuemart_manufacturercategories_en_gb" + "-" + " ORDER BY virtuemart_manufacturercategories_id ");
            put("jos_virtuemart_product_manufacturers", "virtuemart_product_manufacturers" + "-" + " ORDER BY id ");
            put("jos_virtuemart_vendors", "virtuemart_vendors" + "-" + " ORDER BY virtuemart_vendor_id ");
            put("jos_virtuemart_vendors_ru_ru", "virtuemart_vendors_ru_ru" + "-" + " ORDER BY virtuemart_vendor_id ");
            put("jos_virtuemart_vendors_en_gb", "virtuemart_vendors_en_gb" + "-" + " ORDER BY virtuemart_vendor_id ");
            put("jos_virtuemart_vendor_medias", "virtuemart_vendor_medias" + "-" + " ORDER BY id ");
            put("jos_virtuemart_customs", "virtuemart_customs" + "-" + " ORDER BY virtuemart_custom_id ");
            put("jos_virtuemart_product_customfields", "virtuemart_product_customfields" + "-" + " ORDER BY virtuemart_customfield_id ");
            /*
             * Служебные таблицы
             */
            put("category_state", "category_state" + "-" + " ORDER BY  category_id ");
            put("category_xref", "category_xref" + "-" + " ORDER BY category_parent_id, category_child_id ");
            put("product_category_xref", "product_category_xref" + "-" + " ORDER BY category_id, product_id ");
            put("product_state", "product_state" + "-" + " ORDER BY product_id ");

        }
    };
    
    public static HashMap ServiceTableNames = new HashMap() {
        {
            put("slu_1","category_state");
            put("slu_2","category_xref");
            put("slu_3","product_category_xref");
            put("slu_4","product_state");
        }
    };
    
    public static ArrayList<String> SettingsTableNamesList = new ArrayList<String>() {

        {
            add("jos_virtuemart_products");
            add("jos_virtuemart_products_ru_ru");
            add("jos_virtuemart_products_en_gb");
            add("jos_virtuemart_product_prices");
            add("jos_virtuemart_categories");
            add("jos_virtuemart_categories_ru_ru");
            add("jos_virtuemart_categories_en_gb");
            add("jos_virtuemart_category_medias");
            add("jos_virtuemart_category_categories");
            add("jos_virtuemart_product_categories");
            add("jos_virtuemart_shoppergroups");
            add("jos_virtuemart_vmuser_shoppergroups");
            add("jos_virtuemart_medias");
            add("jos_virtuemart_product_medias");
            add("jos_virtuemart_manufacturers");
            add("jos_virtuemart_manufacturers_ru_ru");
            add("jos_virtuemart_manufacturers_en_gb");
            add("jos_virtuemart_manufacturer_medias");
            add("jos_virtuemart_manufacturercategories");
            add("jos_virtuemart_manufacturercategories_ru_ru");
            add("jos_virtuemart_manufacturercategories_en_gb");
            add("jos_virtuemart_product_manufacturers");
            add("jos_virtuemart_vendors");
            add("jos_virtuemart_vendors_ru_ru");
            add("jos_virtuemart_vendors_en_gb");
            add("jos_virtuemart_vendor_medias");
            add("jos_virtuemart_customs");
            add("jos_virtuemart_product_customfields");
        }
    };

}
