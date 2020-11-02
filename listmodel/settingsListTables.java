/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import tools.Messages;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class settingsListTables implements ListModel {

    private Object[] data = null;

    public settingsListTables() {
        Messages.show("settingsListTables!!!");
        ArrayList<String> tablenames = exchangedata.TablesVM2.SettingsTableNamesList;
        data = new Object[tablenames.size()];
        for (int i = 0; i < tablenames.size(); i++) {
            data[i] = tablenames.get(i);
        }
        Messages.show("settingsListTables3!!!");
    }

    public Object getElementAt(int index) {
        return data[index];
    }

    public int getSize() {
        return data.length;
    }

    public void addListDataListener(ListDataListener ldl) {
        // since the list never changes, we don't need this :-)
    }

    public void removeListDataListener(ListDataListener ldl) {
        // since the
    }
}
