/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmodels;

import entries.CategoryNode;
import frames.Tree;
import java.io.File;
import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;
import licensedsites.SitesData;
import tools.Messages;
import utils.vm2.DelayUtils;
import vmre.GlobalHacks;
import vmre.GlobalVars;
import vmre.LocalDB;
import vmre.Sync;
import vmre.VMREAboutBox;
import vmre.VMREApp;
import vmre.VMREView;
import vmre.utils.BackupHSQLDB;
import vmre.utils.BrowserControl;
import vmre.utils.GlobalClasses;
import vmre.utils.IframeManager;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ModelsMainframe {

    public static void init() {
        Preferences pref = GlobalClasses.getPrefs();
        VMREView v = ((VMREApp) vmre.VMREApp.getInstance()).getView();
        Boolean admin_mode = Boolean.parseBoolean(pref.get("admin_mode", "true"));
        if (admin_mode) {
            v.menuAdmin.setEnabled(true);
        } else {
            v.menuAdmin.setEnabled(false);
        }

        String demo = "";
        if (GlobalHacks.is_demo == 1) {
            demo = " --Демо версия !!!";
            v.mnOrder.setVisible(true);
        } else {
            v.mnOrder.setVisible(false);
        }
        HashMap site = null;

        SitesData sd = new SitesData();
        site = sd.getLicensedSite();


        v.getFrame().setTitle("Rundesigner.com:OfflineVM:" + GlobalVars.version + " (Rev." + GlobalVars.rev + " " + GlobalVars.data + ";VM тест:" + GlobalVars.virtuemart_test + ")"
                + " Версия для домена: " + site.get("site").toString() + demo);

        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(15000);
    }

    public static void prepareLoadPrice() {
        //Удаляем мусор в служебных таблицах
        try {
            LocalDB.sqlMap.delete("delMusorCategoryXref");
        } catch (Exception e) {
            Messages.sboi(e);
        }

        try {
            LocalDB.sqlMap.delete("delMusorCategoryState");
        } catch (Exception e) {
            Messages.sboi(e);
        }

        try {
            LocalDB.sqlMap.delete("delMusorProductState");
        } catch (Exception e) {
            Messages.sboi(e);
        }

        try {
            LocalDB.sqlMap.delete("delMusorPCXCategory");
        } catch (Exception e) {
            Messages.sboi(e);
        }

        try {
            LocalDB.sqlMap.delete("delMusorPCXProduct");
        } catch (Exception e) {
            Messages.sboi(e);
        }

        //Выставляем службные таблицы
        try {
            LocalDB.sqlMap.insert("InsertEmptyCategoryStates");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        try {
            LocalDB.sqlMap.insert("InsertEmptyProductStates");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        try {
            LocalDB.sqlMap.insert("populateCategoryXref");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        try {
            LocalDB.sqlMap.update("setPriceHandlerProduct");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        try {
            LocalDB.sqlMap.update("setPriceHandlerCategory");
        } catch (Exception e) {
            Messages.sboi(e);
        }
        Messages.show("Установка признака обработчика прайсов завершена!!!");
    }

    public static void downloadSiteData() {

        if (JOptionPane.showConfirmDialog(null,
                " Вы действительно хотите загрузить данные с сайта. "
                + "\n При загрузке  данные локальной базы программы будут замещены данными из базы сайта. ",
                "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
        }

        BackupHSQLDB.backupDB();
        if (checkSite()) {
            Sync sync = new Sync(GlobalClasses.getPrefs());
            IframeManager.showFrame("Settings", "xframes");
            xmodels.ModelSettings.init();
            Messages.show("Идет процесс загрузки данных с сайта и импорта их в локальную базу. Пожалуйста ждите...");
            String res = sync.fullSynchro();
            if (res.equals("Full_Synchronization_failed")) {
                JOptionPane.showMessageDialog(new javax.swing.JFrame(), "Выгрузка данных на сайт произвошла с ошибками", "Выгрузка данных на сайт произвошла с ошибками", JOptionPane.INFORMATION_MESSAGE);
            } else {
                IframeManager.showStartFrame("Tree");
                basemodels.ModelTree.init();
                Messages.show("Процесс загрузки данных с сайта и импорт в локальную базу успешно завершен!");
            }

        }
    }

    public static void uploadSiteImages() {

        if (JOptionPane.showConfirmDialog(null,
                " Вы действительно хотите выгрузить изображения на сайт для товаров выделенной категории. "
                + "\n Если категория не выделена будут выгружены изображения для всех товаров каталога. "
                + "",
                "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
        }

        JInternalFrame frm = IframeManager.getByName("Tree");
        Tree tree = (Tree) IframeManager.getComponentByClass(frm, "frames.Tree");

        if (checkSite()) {
            Boolean is_authorized = xmodels.ModelSettings.checkJoomlaBool();
            if (is_authorized) {
                TreePath tp = tree.tree.getSelectionPath();
                Integer selectedCatId = 0;
                CategoryNode nd = new CategoryNode("Магазин", 0, 0);
                if (tp != null) {
                    nd = (CategoryNode) tp.getLastPathComponent();
                    selectedCatId = nd.getIndex();
                }
                Integer res = exchangedata.ExchangeImages.uploadImages(selectedCatId);
            } else {
                JOptionPane.showMessageDialog(frm, "Ошибка авторизации!!!", "Ошибка авторизации!!!", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public static void downloadSiteImages() {

        if (JOptionPane.showConfirmDialog(null,
                " Вы действительно хотите загрузить изображения с сайта для товаров выделенной категории. "
                + "\n Если категория не выделена будут загружены изображения для всех товаров каталога "
                + "",
                "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
        }

        JInternalFrame frm = IframeManager.getByName("Tree");
        Tree tree = (Tree) IframeManager.getComponentByClass(frm, "frames.Tree");
        if (checkSite()) {
            Boolean is_authorized = xmodels.ModelSettings.checkJoomlaBool();
            if (is_authorized) {

                TreePath tp = tree.tree.getSelectionPath();
                Integer selectedCatId = 0;
                CategoryNode nd = new CategoryNode("Магазин", 0, 0);
                if (tp != null) {
                    nd = (CategoryNode) tp.getLastPathComponent();
                    selectedCatId = nd.getIndex();
                }
                Integer res = exchangedata.ExchangeImages.downloadImages(selectedCatId);

                if (res == Sync.ERR_OPEN_CONN) {
                    JOptionPane.showMessageDialog(frm, "Ошибка подключения к локальной базе!!!", "Ошибка подключения к локальной базе!!!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (res == Sync.ERR_SQL_GET_IMAGES) {
                    JOptionPane.showMessageDialog(frm, "Ошибка вы запросе получения подкатегорий категории!!!", "Ошибка вы запросе получения подкатегорий категории!!!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Messages.show("Изображения успешно загружены на локальный компьютер");

            } else {
                JOptionPane.showMessageDialog(frm, "Ошибка авторизации!!!", "Ошибка авторизации!!!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void uploadLocalDb() {
        if (JOptionPane.showConfirmDialog(null,
                " Вы действительно хотите выгрузить данные локальной базы на сайт. "
                + "\n При выгрузке данные базы на сайте будут замещены данными из локальной базы программы "
                + "",
                "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            return;
        }

        BackupHSQLDB.backupDB();
        if (checkSite()) {
            LocalDB.fullDumpChunkXMLRPC();
        }
    }

    public static void clearLocalDb() {
        IframeManager.showFrame("Settings", "xframes");
        xmodels.ModelSettings.init();
        Messages.show("Идет процесс удаления и инициализации локальной базы! Пожалуйста ждите...");
        BackupHSQLDB.backupDB();
        LocalDB.initDataBase(true);
        DelayUtils.doDelay(3);
        Messages.show("Процесс удаления и инициализации локальной базы успешно завершен!");
        IframeManager.showStartFrame("Tree");
        basemodels.ModelTree.init();
    }

    public static Boolean checkSite() {
        Sync sync = new Sync(GlobalClasses.getPrefs());
        Messages.show("Проверяем лицензию сайта");
        if (!sync.siteValidate()) {
            JOptionPane.showMessageDialog(new javax.swing.JFrame(), "Неверный сайт", "Неверный сайт", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    public static void about() {

        JFrame mainFrame = VMREApp.getApplication().getMainFrame();
        VMREAboutBox aboutBox = new VMREAboutBox(mainFrame);
        aboutBox.setLocationRelativeTo(mainFrame);
        VMREApp.getApplication().show(aboutBox);
    }

    public static void support() {
        BrowserControl.displayURL("http://www.rundesigner.com/vladimir-kovalev");
    }

    public static void contact() {
        BrowserControl.displayURL("http://www.rundesigner.com/vladimir-kovalev");
    }

    public static void backup() {
        BackupHSQLDB.backupDB();
        Messages.show("Резерваня копия локальной базы успешно создана!!!");
    }

    public static void priceloader() {
        IframeManager.showFrame("PriceLoader", "xframes");
        xmodels.ModelPriceLoader.init();
    }

    public static void pricetemplate() {
        String documentPath = System.getProperty("user.dir") + File.separator + "pricelist_template.xls";
        String path = "file://" + documentPath;
        BrowserControl.displayURL(path);
    }

    public static void orderOfflineVm3() {
        String path = "http://www.rundesigner.com/zakazat-offlinevm3";
        BrowserControl.displayURL(path);
    }

    public static void dbCall() {        
        String s = (String) JOptionPane.showInputDialog(
                null,
                "Введите имя запроса:\n",
                "Выполнение запроса на базе",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");

//If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            Messages.show("Выполняем запрос="+s);
            try{
                LocalDB.sqlMap.insert(s);
            }catch(Exception e){
                Messages.sboi(e);
            }
            Messages.show("Запрос "+s+" выполнен");
            return;
        }
    }
}
