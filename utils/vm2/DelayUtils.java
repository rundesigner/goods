/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.vm2;

/**
 *
 * @author Grigory Fedorinov<info@rundesigner.com>
 */
public class DelayUtils {
    
    public static void doDelay(int i){
                try{
        Thread.sleep(i * 1000);
        }catch(Exception e){
        }
    }
    
}
