/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmodels;

import javax.swing.JInternalFrame;
import tools.Messages;
import vmre.utils.IframeManager;
import xframes.PriceLoader;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class ModelPriceLoader {

    public static void init() {
        JInternalFrame frm = IframeManager.getByName("PriceLoader");
        PriceLoader pr = (PriceLoader) IframeManager.getComponentByClass(frm, "xframes.PriceLoader");
        Messages.show("Пытаемся выполнить глобальные хаки");
        pr.performGlobalHacks();
    }
}
