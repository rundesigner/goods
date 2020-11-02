/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xactions;

import java.io.File;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import tools.Messages;
import vmre.VMREApp;
import vmre.utils.IframeManager;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ActionsSettings {

    @Action
    public static Task Save() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Сохраняем настройки");
                xmodels.ModelSettings.save();
                String imgPath = vmre.utils.GlobalClasses.getPrefs().get("img_local_path", "");
                if (imgPath.length() != 0) {
                    File imgs = new File(imgPath);
                    if ((!imgs.exists()) || (!imgs.isDirectory())) {
                        imgs.mkdirs();
                    }
                    imgs = new File(imgPath + "\\resized");
                    if ((!imgs.exists()) || (!imgs.isDirectory())) {
                        imgs.mkdir();
                    }
                }
                IframeManager.showFrame("Tree");
                basemodels.ModelTree.init();
                return null;
            }
        };
    }

    @Action
    public static Task checkJoomla() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Проверяем данные авторизации Joomla");
                xmodels.ModelSettings.checkJoomla();
                return null;
            }
        };
    }

    @Action
    public static Task importSettings() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Импортируем настройки из файла XML");
                xmodels.ModelSettings.importSettings();
                return null;
            }
        };
    }

    @Action
    public static Task exportSettings() {
        return new org.jdesktop.application.Task(org.jdesktop.application.Application.getInstance(VMREApp.class)) {

            @Override
            protected Object doInBackground() {
                Messages.show("Экспортируем настройки в файл XML");
                xmodels.ModelSettings.exportSettings();
                return null;
            }
        };
    }
}
