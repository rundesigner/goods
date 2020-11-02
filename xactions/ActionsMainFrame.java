/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xactions;

import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import plugin.factory.Plugin;
import tools.Messages;
import vmre.VMREApp;
import vmre.utils.GlobalClasses;
import vmre.utils.IframeManager;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ActionsMainFrame  extends JPanel{

    @Action
    public static Task Start() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                //Вызываем плагины
                if (Plugin.onStartSystem == 0) {
                    Plugin.CallMethod("onStartSystem");
                    Plugin.onStartSystem = 1;
                }
                xmodels.ModelSettings.check();
                Preferences pref = GlobalClasses.getPrefs();
                String message = pref.get("message", "");
                Messages.show("message=" + message + "|" + "success=" + xmodels.ModelSettings.success);
                if (message.equalsIgnoreCase(xmodels.ModelSettings.success)) {
                    Messages.show("Успешный старт");
//                    xactions.ActionsMainFrame.showTree().execute();
                    IframeManager.showFrame("Tree");
                    basemodels.ModelTree.init();
                } else {
                    Messages.show("Нет подключения к базе -  выводим настройки");
                    IframeManager.showFrame("Settings", "xframes");
                    xmodels.ModelSettings.init();
                }

                return null;
            }
        };
    }

    @Action
    public static Task showTree() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем дерево категорий");
                  IframeManager.showFrame("Tree");
                    basemodels.ModelTree.init();
                return null;
            }
        };
    }

    @Action
    public static Task Settings() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {
            @Override
            protected Object doInBackground() {
                Messages.show("Открываем настройки");
                IframeManager.showFrame("Settings", "xframes");
                xmodels.ModelSettings.init();
                return null;
            }
        };
    }

    @Action
    public static Task prepareLoadPrice() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {
            @Override
            protected Object doInBackground() {
                Messages.show("Подготавливаем локальную базу к загрузке прайсов");
                xmodels.ModelsMainframe.prepareLoadPrice();
                return null;
            }
        };
    }

    @Action
    public static Task downloadSiteData() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {
            @Override
            protected Object doInBackground() {
                Messages.show("Загружаем данные с сайта");
                xmodels.ModelsMainframe.downloadSiteData();
                return null;
            }
        };
    }
    
        @Action
    public static Task downloadSiteImages() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Загружаем изображения с сайта");
                xmodels.ModelsMainframe.downloadSiteImages();
                return null;
            }
            
        };
    }
        
    @Action
    public static Task uploadSiteImages() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Выгружаем изображения на сайт");
                xmodels.ModelsMainframe.uploadSiteImages();
                return null;
            }
            
        };
    }
                

    @Action
    public static Task uploadLocalDb() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Выгружаем данные на сайт");
                xmodels.ModelsMainframe.uploadLocalDb();
                return null;
            }
        };
    }

    @Action
    public static Task clearLocalDb() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Удаляем локальную базу");
                xmodels.ModelsMainframe.clearLocalDb();
                return null;
            }
        };
    }

    @Action
    public static Task about() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Показываем о программе");
                xmodels.ModelsMainframe.about();
                return null;
            }
        };
    }

    @Action
    public static Task support() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем форму техподдержки");
                xmodels.ModelsMainframe.support();
                return null;
            }
        };
    }

    @Action
    public static Task contact() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем контактную форму");
                xmodels.ModelsMainframe.contact();
                return null;
            }
        };
    }

    @Action
    public static Task backup() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Делаем резервную копию локальной базы");
                xmodels.ModelsMainframe.backup();
                return null;
            }
        };
    }

    @Action
    public static Task priceloader() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем форму загрузки прайса");
                xmodels.ModelsMainframe.priceloader();
                return null;
            }
        };
    }
    
        @Action
    public static Task pricetemplate() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем файл шаблона стандартного прайса");
                xmodels.ModelsMainframe.pricetemplate();
                return null;
            }
        };
    }
   
                @Action
    public static Task orderOfflineVm3() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем страницу заказа OfflineVm3");
                xmodels.ModelsMainframe.orderOfflineVm3();
                return null;
            }
        };
    }
        
                     @Action
    public static Task dbCall() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Открываем диалог для ввода имени запроса");
                xmodels.ModelsMainframe.dbCall();
                return null;
            }
        };
    }
                     
}
