package entries;

public class PriceEntry {
    /*
     * Из таблицы jos_virtuemart_product_prices
     */
    private Integer virtuemart_product_price_id=0;
    private Integer virtuemart_product_id=0;
    private Integer virtuemart_shoppergroup_id=0;
    private Double  product_price=0.0;
    private Integer override=0;
    private Double  product_override_price=0.0;
    private Integer product_tax_id=0;
    private Integer product_discount_id=0;
    private Integer product_currency=0;
    private String  PRODUCT_PRICE_PUBLISH_UP = "0000-00-00 00:00:00";
    private String  PRODUCT_PRICE_PUBLISH_DOWN = "0000-00-00 00:00:00";
    private Integer price_quantity_start=0;
    private Integer price_quantity_end=0;
    private String  created_on="0000-00-00 00:00:00";
    private Integer created_by=0;
    private String  modified_on="0000-00-00 00:00:00";
    private Integer modified_by=0;
    private String  locked_on="0000-00-00 00:00:00";
    private Integer locked_by=0;

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public Integer getLocked_by() {
        return locked_by;
    }

    public void setLocked_by(Integer locked_by) {
        this.locked_by = locked_by;
    }

    public String getLocked_on() {
        return locked_on;
    }

    public void setLocked_on(String locked_on) {
        this.locked_on = locked_on;
    }

    public Integer getModified_by() {
        return modified_by;
    }

    public void setModified_by(Integer modified_by) {
        this.modified_by = modified_by;
    }

    public String getModified_on() {
        return modified_on;
    }

    public void setModified_on(String modified_on) {
        this.modified_on = modified_on;
    }

    public Integer getOverride() {
        return override;
    }

    public void setOverride(Integer override) {
        this.override = override;
    }

    public Integer getPrice_quantity_end() {
        return price_quantity_end;
    }

    public void setPrice_quantity_end(Integer price_quantity_end) {
        this.price_quantity_end = price_quantity_end;
    }

    public Integer getPrice_quantity_start() {
        return price_quantity_start;
    }

    public void setPrice_quantity_start(Integer price_quantity_start) {
        this.price_quantity_start = price_quantity_start;
    }

    public Integer getProduct_currency() {
        return product_currency;
    }

    public void setProduct_currency(Integer product_currency) {
        this.product_currency = product_currency;
    }

    public Integer getProduct_discount_id() {
        return product_discount_id;
    }

    public void setProduct_discount_id(Integer product_discount_id) {
        this.product_discount_id = product_discount_id;
    }

    public Double getProduct_override_price() {
        return product_override_price;
    }

    public void setProduct_override_price(Double product_override_price) {
        this.product_override_price = product_override_price;
    }

    public Double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Double product_price) {
        this.product_price = product_price;
    }

    public String getPRODUCT_PRICE_PUBLISH_DOWN() {
        return PRODUCT_PRICE_PUBLISH_DOWN;
    }

    public void setPRODUCT_PRICE_PUBLISH_DOWN(String idx) {
        this.PRODUCT_PRICE_PUBLISH_DOWN = idx;
    }

    public String getPRODUCT_PRICE_PUBLISH_UP() {
        return PRODUCT_PRICE_PUBLISH_UP;
    }

    public void setPRODUCT_PRICE_PUBLISH_UP(String idx) {
        this.PRODUCT_PRICE_PUBLISH_UP = idx;
    }

    public Integer getProduct_tax_id() {
        return product_tax_id;
    }

    public void setProduct_tax_id(Integer product_tax_id) {
        this.product_tax_id = product_tax_id;
    }

    public Integer getVirtuemart_product_price_id() {
        return virtuemart_product_price_id;
    }

    public void setVirtuemart_product_price_id(Integer virtuemart_product_price_id) {
        this.virtuemart_product_price_id = virtuemart_product_price_id;
    }

    public Integer getVirtuemart_shoppergroup_id() {
        return virtuemart_shoppergroup_id;
    }

    public void setVirtuemart_shoppergroup_id(Integer virtuemart_shoppergroup_id) {
        this.virtuemart_shoppergroup_id = virtuemart_shoppergroup_id;
    }

    public Integer getVirtuemart_product_id() {
        return virtuemart_product_id;
    }

    public void setVirtuemart_product_id(Integer virtuemart_product_id) {
        this.virtuemart_product_id = virtuemart_product_id;
    }
    
}
