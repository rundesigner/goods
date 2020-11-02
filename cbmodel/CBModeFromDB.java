/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbmodel;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author msv
 */
public class CBModeFromDB extends AbstractListModel implements ComboBoxModel {

    List items;
    Object selected;

    public CBModeFromDB(SqlMapClient sqlMap, String queryName, String extraItems) {
        try {
            items = sqlMap.queryForList(queryName);
        } catch (SQLException ex) {
            Logger.getLogger(CBModeFromDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (extraItems.equalsIgnoreCase("tax")) {
            CBItem cbi = new CBItem();
            cbi.setId(0);
            cbi.setName("0.00%");
            cbi.setParam1(new Double(0));
            items.add(0, cbi);
        } else if (extraItems.equalsIgnoreCase("Default")) {
            CBItem cbi = new CBItem();
            cbi.setId(0);
            cbi.setName("Выберите производителя");
            items.add(0, cbi);
        } else if (extraItems.equalsIgnoreCase("discount")) {
            CBItem cbi = new CBItem();
            cbi.setId(0);
            cbi.setName("0.00");
            cbi.setParam1(new Double(0));
            cbi.setParam2(new Integer(0));
            items.add(0, cbi);
            cbi = new CBItem();
            cbi.setId(-1);
            cbi.setName("override");
            cbi.setParam1(new Double(0));
            cbi.setParam2(new Integer(1));
            items.add(cbi);
        }
    }

    public CBModeFromDB(SqlMapClient sqlMap, String queryName) {
        try {
            items = sqlMap.queryForList(queryName);
            System.out.println("itrmsize="+items.size());
        } catch (SQLException ex) {
            Logger.getLogger(CBModeFromDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CBModeFromDB(SqlMapClient sqlMap, String queryName,Integer parentId) {
        try {
            items = sqlMap.queryForList(queryName,parentId);
            System.out.println("itrmsize1="+items.size());
        } catch (SQLException ex) {
            Logger.getLogger(CBModeFromDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CBModeFromDB(String[] st){
        items=new ArrayList<CBItemString>();
        for(String s:st){
            CBItemString cbi = new CBItemString();
            cbi.setId(s);
            cbi.setName(s);
            items.add(cbi);
        }
    }

    public int getSize() {
        return items.size();
    }

    public Object getElementAt(int index) {
        return items.get(index);
    }

    public void setSelectedItem(Object anItem) {
        selected = anItem;
    }

    public Object getSelectedItem() {
        return selected;
    }
}
