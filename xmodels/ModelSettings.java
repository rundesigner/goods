/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmodels;

import java.io.File;
import java.io.FileInputStream;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import tools.Messages;
import vmre.LocalDB;
import vmre.Sync;
import vmre.VMREApp;
import vmre.VMREView;
import vmre.utils.BrowserControl;
import vmre.utils.GlobalClasses;
import vmre.utils.IframeManager;
import vmre.utils.WriteXmlFile;
import xframes.Settings;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ModelSettings {

    public static String success = "Соединение успешно установлено !!!";

    public static void init() {
        JInternalFrame frm = IframeManager.getByName("Settings");
        Settings se = (Settings) IframeManager.getComponentByClass(frm, "xframes.Settings");
        Preferences pref = GlobalClasses.getPrefs();
        se.tfPropSite.setText(pref.get("site_host", ""));
        se.tfPropDBPath.setText(pref.get("hsqldb_file", System.getProperty("user.dir") + File.separator + "data" + File.separator + "vmre"));
        se.tfPropImgPath.setText(pref.get("img_local_path", System.getProperty("user.dir") + File.separator + "Images"));
        se.tfPropImgWidth.setText(pref.get("thumb_x_size", "200"));
        se.tfPropImgHeight.setText(pref.get("thumb_y_size", "250"));
        se.tfPropCurrency.setText(pref.get("currency", "RUB"));
        se.boxAdminMode.setSelected(Boolean.parseBoolean(pref.get("admin_mode", "false")));
        se.chCreateService.setSelected(Boolean.parseBoolean(pref.get("create_service", "false")));
        se.tfPropChunkSize.setText(pref.get("stopsize", "300"));
        se.tfPropChunkCount.setText(pref.get("stopcount", "50"));
        se.tfPropJoomlaUser.setText(pref.get("joomla_user", ""));
        se.tfPropJoomlaPass.setText(pref.get("joomla_pass", ""));
        Messages.show("Инициализируем настройки2!!!");
        //обмен данными
        ListModel m = new listmodel.settingsListTables();
        Messages.show("Инициализируем настройки3!!!");
        se.lstData.setModel(m);
        se.lstData.clearSelection();
        String listData = pref.get("datatables", "0,1,3,4,5,8,9,10,11,14,15,18,19,21,22,23,26,27");
        Messages.show("Массив таблиц=" + listData);
        String[] idxs = listData.split(",");
        int[] idcs = new int[idxs.length];
        int i = 0;
        for (i = 0; i < idxs.length; i++) {
            idcs[i] = Integer.parseInt(idxs[i]);
        }
        Messages.show("Выставляем таблицы");
        se.lstData.setSelectedIndices(idcs);

    }

    public static void save() {
        VMREView v = ((VMREApp) vmre.VMREApp.getInstance()).getView();
        JInternalFrame frm = IframeManager.getByName("Settings");
        Settings se = (Settings) IframeManager.getComponentByClass(frm, "xframes.Settings");
        Preferences prefs = GlobalClasses.getPrefs();
        String x = se.tfPropSite.getText().trim().replace("http://", "");
        if (x.endsWith("/")) {
            x = x.substring(0, x.length() - 1);
        }
        se.tfPropSite.setText(x);
        prefs.put("site_host", x);
        prefs.put("proto", "HTTP");
        prefs.put("hsqldb_file", se.tfPropDBPath.getText());
        prefs.put("img_local_path", se.tfPropImgPath.getText());
        prefs.put("thumb_x_size", se.tfPropImgWidth.getText());
        prefs.put("thumb_y_size", se.tfPropImgHeight.getText());
        prefs.put("currency", se.tfPropCurrency.getText());

        if (se.boxAdminMode.isSelected()) {
            prefs.put("mode_admin", "true");
        } else {
            prefs.put("mode_admin", "false");
        }

        if (se.chCreateService.isSelected()) {
            prefs.put("create_service", "true");
        } else {
            prefs.put("create_service", "false");
        }

        if (Integer.parseInt(se.tfPropChunkSize.getText()) > 0) {
            prefs.put("stopsize", se.tfPropChunkSize.getText());
        } else {
            prefs.put("stopsize", "500");
        }

        if (Integer.parseInt(se.tfPropChunkCount.getText()) > 0) {
            prefs.put("stopcount", se.tfPropChunkCount.getText());
        } else {
            prefs.put("stopcount", "100");
        }

        prefs.put("joomla_user", se.tfPropJoomlaUser.getText());
        String pass = new String(se.tfPropJoomlaPass.getPassword());
        //  Messages.show("pass="+pass);
        prefs.put("joomla_pass", pass);

        if (se.boxAdminMode.isSelected()) {
            prefs.put("admin_mode", "true");
            v.menuAdmin.setEnabled(true);
        } else {
            prefs.put("admin_mode", "false");
            v.menuAdmin.setEnabled(false);
        }

        // Get the index of all the selected items
        int[] selectedIx = se.lstData.getSelectedIndices();

// Get all the selected items using the indices
        String lst = "";
        for (int i = 0; i < selectedIx.length; i++) {
            if (i != 0) {
                lst += ",";
            }
            lst += selectedIx[i];
        }
        //Messages.show("Выделенные таблицы="+lst);
        prefs.put("datatables", lst);
        try {
            prefs.flush();
        } catch (Exception e) {
            Messages.sboi(e);
        }
        Messages.show("Настройки сохранены");
    }

    public static void check() {
        //save();
        try {
            Messages.show("Начинаем проверку соединения с локальной базой!!!");
            Preferences pref = GlobalClasses.getPrefs();
            LocalDB.initSqlMap();
            String message = pref.get("message", "");

            if (message.equalsIgnoreCase(LocalDB.success_init_conn)) {
                LocalDB.initDataBase(false);
                message = pref.get("message", "");
                if (message.equalsIgnoreCase(LocalDB.success_init_base)) {
                    Messages.pshow(success);
                } else {
                    Messages.show("Ошибка инициализации  локальной базы !!!");
                }
            } else {
                Messages.show("Ошибка подключения к локальной базе!!!");
            }
            //sqlMap=null;
        } catch (Exception ex) {
            //sqlMap = null;
            Messages.sboi(ex);
            return;
        }

    }

    public static Boolean checkJoomlaBool() {
        Sync sync = new Sync();
        String res = sync.CheckJoomla();
        Messages.show("Ответ авторизации=" + res);
        if (null == res || res.equals("site_auth_failed")) {
            return false;
        } else {
            return true;
        }
    }

    public static void checkJoomla() {
        save();
        if (xmodels.ModelsMainframe.checkSite()) {
            JInternalFrame frm = IframeManager.getByName("Settings");
            Boolean res = checkJoomlaBool();
            if (!res) {
                JOptionPane.showMessageDialog(frm, "Ошибка авторизации!!!", "Ошибка авторизации!!!", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frm, "Авторизация успешно пройдена", "Авторизация успешно пройдена", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void importSettings() {
        try {
            Preferences prefs = GlobalClasses.getPrefs();
            JFileChooser fch = new JFileChooser();
            FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
            fch.setFileFilter(xmlfilter);
            File f = null;
            switch (fch.showOpenDialog(null)) {
                case JFileChooser.APPROVE_OPTION:
                    f = fch.getSelectedFile();
                    break;
                default:
                    return;
            }
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(f.getAbsolutePath()));
            XPath xPath = XPathFactory.newInstance().newXPath();
            String xpath = "";
            xpath = "//site_host/text()";
            String site_host = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//hsqldb_file/text()";
            String hsqldb_file = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//img_local_path/text()";
            String img_local_path = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//thumb_x_size/text()";
            String thumb_x_size = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//thumb_y_size/text()";
            String thumb_y_size = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//currency/text()";
            String currency = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//mode_admin/text()";
            String mode_admin = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//create_service/text()";
            String create_service = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//stopsize/text()";
            String stopsize = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//stopcount/text()";
            String stopcount = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//joomla_user/text()";
            String joomla_user = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//joomla_pass/text()";
            String joomla_pass = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//admin_mode/text()";
            String admin_mode = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);
            xpath = "//datatables/text()";
            String datatables = (String) xPath.compile(xpath).evaluate(document, XPathConstants.STRING);

            prefs.put("site_host", site_host);
            prefs.put("proto", "HTTP");
            prefs.put("hsqldb_file", hsqldb_file);
            prefs.put("img_local_path", img_local_path);
            prefs.put("thumb_x_size", thumb_x_size);
            prefs.put("thumb_y_size", thumb_y_size);
            prefs.put("currency", currency);
            prefs.put("mode_admin", mode_admin);
            prefs.put("create_service", create_service);
            prefs.put("stopsize", stopsize);
            prefs.put("stopcount", stopcount);
            prefs.put("joomla_user", joomla_user);
            prefs.put("joomla_pass", joomla_pass);
            prefs.put("admin_mode", admin_mode);
            prefs.put("datatables", datatables);

            try {
                prefs.flush();
            } catch (Exception e) {
                Messages.sboi(e);
            }

            Messages.show("Настройки импортированы");
            init();
        } catch (Exception e) {
            Messages.sboi(e);
        }
    }

    public static void exportSettings() {        
          String file = WriteXmlFile.exportSetting();
          BrowserControl.displayURL("file://"+file);
    }
    
}
