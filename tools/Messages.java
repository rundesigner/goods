/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import javax.swing.JTextArea;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import vmre.VMREApp;
import vmre.VMREView;
import vmre.utils.GlobalClasses;

/**
 *
 * @author grigory
 */
public class Messages {

    public static String label = "Разработано Федориновым Григорием из Rundesigner.com";
    public static int consolemode = 0;
    public static JTextArea ta;
    public static boolean use_ta = false;

    public static void pshow(String s) {
        VMREView v;
        Preferences prefs=GlobalClasses.getPrefs();
        prefs.put("message",s);
        if (consolemode == 0) {
            v = (VMREView) VMREApp.getApplication().getMainView();
            v.statusMessageLabel.setText(s);
            v.logger.log(Level.INFO, s);
        }
        System.out.println(s);
        if (use_ta) {
            ta.append("\n"+s);
        }
    }
    
    
    public static void show(String s) {
        VMREView v;
        if (consolemode == 0) {
            v = (VMREView) VMREApp.getApplication().getMainView();
            v.statusMessageLabel.setText(s);
            v.logger.log(Level.INFO, s);
        }
        System.out.println(s);
        if (use_ta) {
            ta.append("\n"+s);
        }
    }

    public static void setDefault() {
        VMREView v;
        if (consolemode == 0) {
            v = (VMREView) VMREApp.getApplication().getMainView();
            v.statusMessageLabel.setText(label);
        }
    }

    public static void sboi(Exception e) {
        VMREView v;
        Messages.show("Произошел сбой программы :" + e.getMessage());
        if (consolemode == 0) {
            StackTraceElement[] els = e.getStackTrace();
            v = (VMREView) VMREApp.getApplication().getMainView();
            for (int i = 0; i < els.length; i++) {
                v.logger.log(Level.SEVERE, els[i].toString());
            }
        }
        if (use_ta) {
            StackTraceElement[] els = e.getStackTrace();
            for (int i = 0; i < els.length; i++) {
                ta.append("\n"+els[i].toString());
            }
        }
    }
}