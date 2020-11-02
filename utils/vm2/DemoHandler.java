/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.vm2;

import java.util.List;
import javax.swing.JOptionPane;
import tools.Messages;
import vmre.GlobalHacks;
import vmre.LocalDB;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class DemoHandler {

    public static String zakazatUrl = "http://www.rundesigner.com/offlinevm3";
    public static Integer CategoryCntBigger6 = 0;
    public static Integer ProductCntBigger10 = 0;

    public static void checkDemoQuantity() {

        if (GlobalHacks.is_demo == 0) {
            return;
        }

        if (isCategoryBigger6()
                || isProductBigger10()) {
//            JOptionPane.showMessageDialog(new javax.swing.JFrame(),
//                    " Вы используете демо версию OfflineVM3. "
//                    + "\n Демо служит только для демонстрации возможностей платной программы OfflineVM3."
//                    + "\n Не рекомендуется использовать демо версию на рабочих сайтах !!! "
//                    + "\n демо версия имеет ограничения на размер товарной базы: "
//                    + "\n Максимальное количество категорий - 6 "
//                    + "\n Максимальное количество товаров - 10 "
//                    + "\n Сейчас размер базы превышает эти параметры. "
//                    + "\n Поэтому база будет урезана до размеров допустимой. "
//                    + "\n Чтобы полноценно использовать программу OfflineVM3 "
//                    + "\n вы можете заказать полную версию программы на сайте Rundesigner.com "
//                    + "\n Для того чтобы открыть форму заказа программы - выберите в главном меню \"Заказать OfflineVM3\" ", " Вы используете демо версию OfflineVM3 ", JOptionPane.INFORMATION_MESSAGE);
Messages.show("База превышает пределы допустимой. Начинаем приведение до товаров=10(в базе"+ProductCntBigger10+") категорий=6 (в базе"+CategoryCntBigger6+")" );
            removeBigger();
        }
    }

    public static Boolean isCategoryBigger6() {
        Boolean ret = true;
        try {
            Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("getDemoCntCategories");
            if (cnt <= 6) {
                ret = false;
            }
            CategoryCntBigger6 = cnt;
        } catch (Exception e) {
            CategoryCntBigger6 = 0;
            Messages.sboi(e);
        }
        return ret;
    }

    public static Boolean isProductBigger10() {
        Boolean ret = true;
        try {
            Integer cnt = (Integer) LocalDB.sqlMap.queryForObject("getDemoCntProducts");
            ProductCntBigger10 = cnt;
            if (cnt <= 10) {
                ret = false;
            }
        } catch (Exception e) {
            Messages.sboi(e);
            ProductCntBigger10 = 0;
        }
        return ret;
    }

    public static void removeBiggerCategory() {
        isCategoryBigger6();
        //Для начала уберем лишние категории
        if (CategoryCntBigger6 > 6) {
            vmre.utils.BackupHSQLDB.backupDB();
            Messages.show("Количество категорий больше 6. Начат процесс удаления категорий с наибольшим ИД ");
            Integer needremovecnt = CategoryCntBigger6 - 6;
            try {
                List<Integer> ids = (List<Integer>) LocalDB.sqlMap.queryForList("getDemoCategory4Remove", needremovecnt);
                for (int i = 0; i < ids.size(); i++) {
                    basemodels.ModelTree.remove(ids.get(i));
                }
                removeBiggerProduct();
            } catch (Exception e) {
                Messages.sboi(e);
            }
        }
    }

    public static void removeBiggerProduct() {
        //Для начала уберем лишние категории
        isProductBigger10();
        if (ProductCntBigger10 > 10) {
            vmre.utils.BackupHSQLDB.backupDB();
            Integer needremovecnt = ProductCntBigger10 - 10;
            Messages.show("Количество товаров больше 10. Начат процесс удаления товаров с наибольшим ИД ");
            try {
                List<Integer> ids = (List<Integer>) LocalDB.sqlMap.queryForList("getDemoProduct4Remove", needremovecnt);
                for (int i = 0; i < ids.size(); i++) {
                    Messages.show("Удаляем демо товар c ид="+ids.get(i));
                    basemodels.ModelProductList.remove(ids.get(i));
                }
                    Messages.show("Удаление демотоваров сверх лимита завершено!!!");
            } catch (Exception e) {
                Messages.sboi(e);
            }
        }
    }

    public static void removeBigger() {
        removeBiggerCategory();
        removeBiggerProduct();
    } 
}
