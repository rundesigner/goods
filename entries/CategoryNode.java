/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entries;

/**
 *
 * @author Проверка
 */
public class CategoryNode {

    private int index;
    private int parentId;
    private String name;
    private String imagePresent;
    private String publish;
    
    
    public CategoryNode() {
        imagePresent = "Y";
        publish = "Y";
        name = "";
        index = 0;
        parentId = 0;
    }

   public CategoryNode(String xname, int xid, int xparentId) {
        imagePresent = "Y";
        name = xname;
        index = xid;
        parentId = xparentId;
        publish = "Y";
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getImagePresent() {
        return imagePresent;
    }

    public void setImagePresent(String imagePresent) {
        this.imagePresent = imagePresent;
    }

    public String  getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }
    
}
