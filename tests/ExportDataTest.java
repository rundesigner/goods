/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.prefs.Preferences;
import tools.Messages;
import vmre.GlobalVars;
import vmre.LocalDB;
import vmre.Sync;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ExportDataTest {
    
    public static void main(String[] args) {
        Messages.consolemode=1;
        Messages.show("Начали тест!!!");
        LocalDB.initSqlMap();
        ArrayList<String> tablenames = LocalDB.getExportTables();
        for (int i = 0; i < tablenames.size(); i++) {
//            Messages.show("Выгружаем таблицу:" + tablenames.get(i).toUpperCase());
            //Запрос на количество записей
            HashMap m = new HashMap();
            m.put("tablename", tablenames.get(i).toUpperCase());
            try {
                Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("getTableCnt", m);
                Integer chunk = Integer.parseInt(Preferences.userRoot().node(GlobalVars.name).get("stopsize", "50"));
                for (int j = 0; j < cnt ; j += chunk) {
                        //выбрать данные из текущей таблицы и отпрвить на сервер
                        Messages.show("Выгружаем таблицу " + tablenames.get(i).toLowerCase() + "(" + j + "/" + cnt + ");Таблица (" + (i+1) + "/" + tablenames.size() + ")");
                        HashMap m1 = new HashMap();
                        m1.put("limit", chunk);
                        m1.put("offset", j);
                        m1.put("tablename", tablenames.get(i).toUpperCase());
                        if(j==0&&i==0){
                         m1.put("dropServiceTables", 1);
                        }else{
                          m1.put("dropServiceTables", 0);
                        }
                        ArrayList<HashMap> tableData;
                        LocalDB.sqlMap.flushDataCache();
                        tableData = (ArrayList<HashMap>) LocalDB.sqlMap.queryForList("getTableData", m1);
                        //Цикл по записям ищем нулл и заменяем на ''
                        for (int k = 0; k < tableData.size(); k++) {
                            HashMap km = tableData.get(k);
                            //Щикл по хешмапу.
                            Iterator iterator = km.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                if (km.get(key) == null) {
                                    km.put(key, "");
                                }
                                km.put(key, km.get(key).toString());
                            }
                            tableData.set(k, km);
                        }
//                    System.out.println(tableData.get(0));
//                    System.out.println("table " + tablenames.get(i).toLowerCase() + ":" + tableData.size());
                        Sync sync = new Sync(Preferences.userRoot().node(GlobalVars.name));
                        Messages.show("ServerUrl="+sync.serverUrl);
                        HashMap data = new HashMap();
                        data.put("comand", "ovm3.importdata");
                        data.put("tablerecords", tableData);
                        data.put("tablename", tablenames.get(i).toLowerCase());
                        data.put("tablej", j);
                        HashMap map = sync.doXMLRPCRequest(data);
                        if (null == map) {
                            return;
                        } else {
                            Messages.show("Выгружаем таблицу " + tablenames.get(i).toLowerCase() + "(" + j + "/" + cnt + ") Выгружено!!! Таблица (" + (i+1) + "/" + tablenames.size() + ")");
                            //    return;
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
