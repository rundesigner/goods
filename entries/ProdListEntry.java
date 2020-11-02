/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entries;

import java.util.prefs.Preferences;

/**
 *
 * @author Проверка
 */
public class ProdListEntry {
   
    private int id;
    private int idP;
    private int parentId;
    private String name;
    private String desc;
    private String fdesc;
    private String product_sku;
    private String product_url;
    private double price;
    private double weight;
    private String currency;
    private int product_in_stock;
    private boolean published;
    private boolean changed;
    private boolean changedP;
    private boolean newItem;
    private int shopper_group_id;
    private String timestamp;
    
    // new
    private int lenght;
    
    
    public ProdListEntry() {
        this.name = "";
        this.fdesc = "";
        this.desc = "";
        this.product_sku = "";
        this.product_url = "";
        this.price = 0;
        this.weight = 0;
        //Preferences prefs = Preferences.userNodeForPackage(PrefModel.class);
        this.currency = "RUB";//prefs.get("currency", "RUB");
        this.newItem = false;
        this.changed = false;
        this.changedP = false;
        this.shopper_group_id = 0;
        this.timestamp = "";
        this.published = true;
        
        // new
        this.lenght = 0;
    }

    public int getLenght() {
        return this.lenght;
    }

    public void setLenght(int idx) {
        this.lenght = idx;
    }

    
    public int getId() {
        return this.id;
    }

    public void setId(int idx) {
        this.id = idx;
    }

    public int getIdP() {
        return this.idP;
    }

    public void setIdP(int idP) {
        this.idP = idP;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int id) {
        this.parentId = id;
    }

    public String getName() {
        return this.name;
    }
    
    public String getProduct_url() {
        return this.product_url;
    }
    
    public void setProduct_url(String Url) {
        this.product_url = Url;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }
    

    public void setDesc(String name) {
        this.desc = name;
    }

    public String getFdesc() {
        return this.fdesc;
    }

    public void setFdesc(String name) {
        this.fdesc = name;
    }

    public String getProduct_sku() {
        return this.product_sku;
    }

    public int getProduct_in_stock() {
        return this.product_in_stock;
    }

    public void setProduct_sku(String name) {
        this.product_sku = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    
    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String name) {
        this.currency = name;
    }

    public int getPublished() {
        return this.published ? 1 : 0;
    }

    public void setPublished(String flag) {
        
        if (flag != null && flag.equalsIgnoreCase("1")) {
            this.published = true;
        } else {
            this.published = false;
        }
    }

    public void setProduct_in_stock(int inStock) {
        this.product_in_stock = inStock;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String s) {
        this.timestamp = s;
    }

    public Object getColumn(int col) {
        switch (col) {
            case -3:
                return this.timestamp;
            case -2:
                return this.idP;
            case -1:
                return this.id;
            case 0:
                if (this.newItem || this.changed || this.changedP) {
                    return new String("*");
                } else {
                    return new String("");
                }
            case 1:
                return this.product_sku;
            case 2:
                return this.name;
            case 3:
                return this.desc;
            case 4:
                return this.price;
            case 5:
                return this.published;
            case 6:
                return this.product_in_stock;
            default:
                return new String("field not found");
        }
    }

    public void setColumn(int col, Object o) {
        if (col != 4) {
            setChanged(true);
        } else {
            setChangedP(true);
        }
        
        switch (col) {
            case 1:
                this.product_sku = (String) o;
                return;
            case 2:
                this.name = (String) o;
                return;
            case 3:
                this.desc = (String) o;
                return;
            case 4:
                this.price = (Double) o;
                return;
            case 5:
                
                this.published = (Boolean) o;
                return;
            case 6:
                this.product_in_stock = (Integer) o;
                return;
            default:
                return;
        }
    }

    public boolean getChanged() {
        return this.changed;
    }

    public void setChanged(boolean flag) {
        this.changed = flag;
    }

    public boolean getChangedP() {
        return this.changedP;
    }

    public void setChangedP(boolean flag) {
        this.changedP = flag;
    }

    public boolean getNewItem() {
        return this.newItem;
    }

    public void setNewItem(boolean flag) {
        this.newItem = flag;
    }

    public int getShopper_group_id() {
        return this.shopper_group_id;
    }

    public void setShopper_group_id(int idx) {
        this.shopper_group_id = idx;
    }
}
