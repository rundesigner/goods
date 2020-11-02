/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exchangedata;

import entries.CategoryNode;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import tools.Messages;
import vmre.LocalDB;
import vmre.Sync;
import vmre.utils.GlobalClasses;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ExchangeImages {

    public static void main(String[] args) {
        Messages.consolemode = 1;
        String img_url = "images/stories/virtuemart/product/g_508.jpg";
        getLocalPath(img_url);


    }
    
    public static String getLocalImagePath() {
        String ret="";
        Preferences pref = GlobalClasses.getPrefs();
        ret=pref.get("img_local_path", System.getProperty("user.dir") + File.separator + "images");
        File f = new File(ret);
        if(!f.exists()){
            f.mkdirs();
        }
        return ret;
    }

    public static void uploadImagesForCategory(int catId) {
        Sync sync = new Sync();
        List imgNames = null;
        Preferences pref = GlobalClasses.getPrefs();
        String local_imgpath = getLocalImagePath();
        sync.transport.openConnection();
        try {

            imgNames = LocalDB.sqlMap.queryForList("getAllProductImagesForCategory", catId);
            for (int i = 0; i < imgNames.size(); i++) {
                HashMap imgName = (HashMap) imgNames.get(i);
//                        Messages.show("HashMap imgName =" + imgName);
                String FILE_URL_THUMB = imgName.get("FILE_URL_THUMB").toString();
                String FILE_URL = imgName.get("FILE_URL").toString();
                if (FILE_URL_THUMB.toString().equalsIgnoreCase("")
                        && FILE_URL.equalsIgnoreCase("")) {
                    continue;
                }

                        
                
                if (!FILE_URL_THUMB.equalsIgnoreCase("")) {
                    String fileName = getImageName(FILE_URL_THUMB);
                    String localPath = getLocalPath(FILE_URL_THUMB);
                    String localimgName = local_imgpath + File.separator + localPath + File.separator + fileName;
                    File f = new File(localimgName);
                    if (f.exists()) {
                        Messages.show("Выгружаем малое изображение:" + fileName);
                        sync.transport.uploadImage(localimgName,fileName,"product|RESIZED");
                    }
                }

                if (!FILE_URL.equalsIgnoreCase("")) {
                    String fileName = getImageName(FILE_URL);
                    String localPath = getLocalPath(FILE_URL);
                    String localimgName = local_imgpath + File.separator + localPath + File.separator + fileName;
                    File f = new File(localimgName);
                    if (f.exists()) {
//  Messages.show("Скачиваем файл:" + fileName);
                        sync.transport.uploadImage(localimgName,fileName,"product|FULL");
                    }
                }

            }

        } catch (SQLException ex) {
            Messages.sboi(ex);
        }
        sync.transport.closeConnection();


    }

    public static int uploadImages(int catId) {
        Sync sync = new Sync();
        if (LocalDB.sqlMap == null) {
            if (!LocalDB.initSqlMap()) {
                JOptionPane.showMessageDialog(new javax.swing.JFrame(), java.util.ResourceBundle.getBundle("vmre/resources/VMREView").getString("Error_iBatis"), java.util.ResourceBundle.getBundle("vmre/resources/VMREView").getString("DB_connection"), JOptionPane.ERROR_MESSAGE);
                return Sync.ERR_OPEN_CONN;
            }
        }
        try {
            int childCount = 0;
            childCount = (Integer) LocalDB.sqlMap.queryForObject("getChildCountCat", catId);
            if (childCount == 0) {
                uploadImagesForCategory(catId);
            } else {
                for (int i = 0; i
                        < childCount; i++) {
                    Map param = new HashMap(3);
                    param.put("parent_id", catId);
                    param.put("startIndex", 0);
                    CategoryNode nd = (CategoryNode) LocalDB.sqlMap.queryForObject("getChildCat", param);
                    uploadImages(nd.getIndex());
                }
                uploadImagesForCategory(catId);
            }
        } catch (SQLException ex) {
            Messages.sboi(ex);
            return Sync.ERR_SQL_GET_IMAGES;
        }
        Messages.show("Процесс выгрузки на сайт изображений успешно завершен");
        return Sync.ERR_NONE;
    }

    public static int downloadImages(int catId) {
        Sync sync = new Sync();
        if (LocalDB.sqlMap == null) {
            if (!LocalDB.initSqlMap()) {
                return Sync.ERR_OPEN_CONN;
            }
        }
        try {
            int childCount = 0;
            childCount = (Integer) LocalDB.sqlMap.queryForObject("getChildCountCat", catId);
            
            if (childCount == 0) {
                downloadImagesForCategory(catId);
            } else {
                for (int i = 0; i < childCount; i++) {
                    Map param = new HashMap(3);
                    param.put("parent_id", catId);
                    param.put("startIndex", i);
                    CategoryNode nd = (CategoryNode) LocalDB.sqlMap.queryForObject("getChildCat", param);
                    downloadImages(nd.getIndex());
                }
                downloadImagesForCategory(catId);
            }
        } catch (SQLException ex) {
            Messages.sboi(ex);
            return Sync.ERR_SQL_GET_IMAGES;
        }
        Messages.show("Процесс скачивания с сайта изображений успешно завершен");
        return Sync.ERR_NONE;
    }

    public static void downloadImagesForCategory(int catId) {
        Sync sync = new Sync();
        List imgNames = null;
        Preferences pref = GlobalClasses.getPrefs();
        String local_imgpath = getLocalImagePath();
        sync.transport.openConnection();
        
        Messages.show(" Запустили  downloadImagesForCategory="+catId);
        
        try {
            imgNames = LocalDB.sqlMap.queryForList("getAllProductImagesForCategory", catId);
            Messages.show(" Количество изображений="+imgNames.size());
            for (int i = 0; i < imgNames.size(); i++) {
                
                HashMap imgName = (HashMap) imgNames.get(i);
                String FILE_URL_THUMB = imgName.get("FILE_URL_THUMB").toString();
                String FILE_URL = imgName.get("FILE_URL").toString();
                if (FILE_URL_THUMB.toString().equalsIgnoreCase("")
                        && FILE_URL.equalsIgnoreCase("")) {
                    continue;
                }

                if (!FILE_URL_THUMB.equalsIgnoreCase("")) {
                    String fileName = getImageName(FILE_URL_THUMB);
                    String localPath = getLocalPath(FILE_URL_THUMB);
                    String localimgName = local_imgpath + File.separator + localPath + File.separator + fileName;
                    File f = new File(local_imgpath + File.separator + localPath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    Messages.show("Скачиваем файл:" + fileName);
                    sync.transport.downloadImage(FILE_URL_THUMB, localimgName);
                }

                if (!FILE_URL.equalsIgnoreCase("")) {
                    String fileName = getImageName(FILE_URL);
                    String localPath = getLocalPath(FILE_URL);
                    String localimgName = local_imgpath + File.separator + localPath + File.separator + fileName;
                    File f = new File(local_imgpath + File.separator + localPath);
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    //  Messages.show("Скачиваем файл:" + fileName);
                    sync.transport.downloadImage(FILE_URL, localimgName);
                }

            }
        } catch (Exception e) {
            Messages.sboi(e);
            return;
        }
        sync.transport.closeConnection();

    }

    public static String getImageName(String img_url) {
        String ret = "";
        // images/stories/virtuemart/product/g_508.jpg
        if (img_url.equalsIgnoreCase("")) {
            return "";
        }
        ret = img_url.substring(img_url.lastIndexOf("/") + 1);
        //Messages.show("ret=" + ret);
        return ret;
    }

    public static String getLocalPath(String img_url) {
        String ret = "";
        // images/stories/virtuemart/product/g_508.jpg
        if (img_url.equalsIgnoreCase("")) {
            return "";
        }
        ret = img_url.substring(0, img_url.lastIndexOf("/")).replaceAll("\\/", "\\\\");
        // Messages.show("ret=" + ret);
        return ret;
    }
}
