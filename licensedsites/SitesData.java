/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package licensedsites;

import java.util.HashMap;
import java.util.prefs.Preferences;
import vmre.GlobalVars;
import vmre.VMREApp;
import vmre.VMREView;

/**
 *
 * @author grigory
 */
public class SitesData {
//public static HashMap sites =

    private class sites {

        public HashMap getSitesData() {
            // yamarket
            // standart_priceloader
            // custom_priceloader
            // mysql_version
            // export_csv
            // ...
            HashMap sites = new HashMap();

            sites.put("ccs.tomsk.ru", new HashMap() {

                {
                    put("standart_priceloader", "1");
                    put("yamarket", "1");
                }
            });
            sites.put("cartstore.ru", new HashMap());

            sites.put("nakolesax.kiev.ua", new HashMap() {

                {
                    put("yamarket", "1");
                }
            });

            sites.put("dela-astra.ru", new HashMap() {

                {
                    put("standart_priceloader", "1");
                }
            });
            //дела-астра.рф
            sites.put("xn----7sbbaqg6b4bik.xn--p1ai", new HashMap() {

                {
                    put("standart_priceloader", "1");
                }
            });

            sites.put("kokolove.net", new HashMap());

            sites.put("tehnas.ru", new HashMap() {

                {
                    put("parser_", "1");
                }
            });
            sites.put("sew-centr.ru", new HashMap());
            sites.put("sv-cardiovita.ru", new HashMap());
            sites.put("msigr.com", new HashMap());
            sites.put("masterfon.kiev.ua", new HashMap() {

                {
                    put("standart_priceloader", "1");
                    put("yamarket", "1");
                }
            });
            sites.put("shop.ross.in.ua", new HashMap());
            sites.put("ross.in.ua", new HashMap());
            sites.put("klining63.ru", new HashMap());




            sites.put("alladinmag.ru", new HashMap() {

                {
                    put("yamarket", "1");
                }
            });
            sites.put("imperia-1.ru", new HashMap() {

                {
                    put("merlion_priceloader", "1");
                    put("merlion_parser", "1");
                }
            });
            sites.put("trio-yar.ru", new HashMap() {

                {
                    put("yamarket", "1");
                }
            });
            sites.put("astrel-spb.ru", new HashMap() {

                {
                    put("standart_priceloader", "1");
                }
            });
            sites.put("hg-med.ru", new HashMap() {

                {
                    put("standart_priceloader", "1");
                }
            });
            sites.put("rambach-powerbox.ru", new HashMap() {

                {
                    put("standart_priceloader", "1");
                }
            });

            sites.put("igroshop.com.ua", new HashMap());
            //идеалпара.рф
            sites.put("xn--80aaanfqy6ak.xn--p1ai", new HashMap() {

                {
                    put("standart_priceloader", "1");
                }
            });
            
             // купе-купи.рф 
              sites.put("xn----itbkfc1ad1ae.xn--p1ai", new HashMap() {

                {
                    put("standart_priceloader", "1");
                    put("yamarket", "1");
                }
            });
              sites.put("osnastka-pechati.ru", new HashMap());
              sites.put("delfyn.ru", new HashMap(){
                  {
                    put("standart_priceloader", "1");
                  }
              });
              sites.put("kazan-watch.ru", new HashMap() {
                {
                    put("yamarket", "1");
                }
            });
                    sites.put("mirtelcom.ru", new HashMap() {
                {
                    put("yamarket", "1");
                }
            });
            sites.put("sadovnik.vn.ua", new HashMap());        
            sites.put("shop.vorota73.ru", new HashMap());        
            sites.put("mavarus.ru", new HashMap());     
            sites.put("service-kluch.com", new HashMap());     
// http://www.инструмент-сервис-ключ.рф
            sites.put("xn-----elckbperjckc0bfifeheq2h7g.xn--p1ai", new HashMap());     
//http://www.автомагазин-сервисключ.рф
            sites.put("xn----7sbaaidesqkdynduj7bqef6jvg.xn--p1ai", new HashMap());     
//http://www.набор-инструментов-форс.рф
            sites.put("xn-----6kcegru6aeecggcudhjjkes4a.xn--p1ai", new HashMap());     
//http://www.инструменты-форс.рф
            sites.put("xn----itbiqecilfdheeku4i.xn--p1ai", new HashMap());     
//http://www.сервисключ.рф
            sites.put("xn--b1afkif0afe0dvd.xn--p1ai", new HashMap());     
//http://www.инструмент-матрикс.рф
            sites.put("xn----8sbnldmjcfc4agfkeedk.xn--p1ai", new HashMap());     
//http://www.инструмент-дело-техники.рф
            sites.put("xn-----klcccbpgathhmcgn0artegl2d.xn--p1ai", new HashMap());     
//http://www.автоинструменты-форс.рф
            sites.put("xn----7sbgmoygcegrfghdeeoy2l.xn--p1ai", new HashMap());     
//http://www.автомагазин-сервис-ключ.рф
             sites.put("xn-----6kcaakdfusld1aodvk9breg2k2g.xn--p1ai", new HashMap());     
//http://www.авто-дело.рф
             sites.put("xn----7sbgjf0bre4a.xn--p1ai", new HashMap());     
//http://www.цепи-на-колеса.рф
             sites.put("xn-----7kcbvdrtjqof9azd.xn--p1ai", new HashMap());     
//http://www.инструмент-для-сервиса.рф
             sites.put("xn-----8kchiedtg0ahlc3bigiefer5y.xn--p1ai", new HashMap());     
//http://www.мир--инструмента.рф
              sites.put("xn-----8kctmcyeic0aemoem.xn--p1ai", new HashMap());     
//http://www.инструмент-форс.рф
              sites.put("xn-----8kctmcyeic0aemoem.xn--p1ai", new HashMap());     
//http://www.наборы-инструментов-форс.рф
             sites.put("xn-----6kcegru6aeecggcudhjjkes4a7m.xn--p1ai", new HashMap());        
//http://www.гидравлическая-растяжка.рф
             sites.put("xn----7sbabaifilnod1aeh1fiofu0huif.xn--p1ai", new HashMap());        
//http://www.устройства-зарядное.рф
             sites.put("xn----7sbbfnglm6bciohfdmel5v.xn--p1ai", new HashMap());        
//http://www.инструменты-для-сервиса.рф
             sites.put("xn-----8kchiedtg0ahlc3bigiefer4pud.xn--p1ai", new HashMap());        
//http://www.инструмент-делотехники.рф
             sites.put("xn----htbbcanfarhglcfnyqsefl9c.xn--p1ai", new HashMap());        
//http://www.инструмент-сервисключ.рф
             sites.put("xn----dtbhbmeoicjc7afhfegep7g0g.xn--p1ai", new HashMap());        
//http://www.дело--техники.рф
             sites.put("xn-----jlcecsapexh4c0b.xn--p1ai", new HashMap());        
//http://www.авто--ключ.рф
             sites.put("xn-----6kci6bgr5a4c1d.xn--p1ai", new HashMap());        
//http://www.автозарядка.рф
             sites.put("xn--80aaaglqx2auq1m.xn--p1ai", new HashMap());        
             sites.put("oscaria.ru", new HashMap());        //priceloader
             sites.put("allmacraft.com", new HashMap());        
//русьпартнер.рф             
             sites.put("xn--80akxgecccmf4h.xn--p1ai", new HashMap());        
             sites.put("postelnoe.pro", new HashMap());        
             sites.put("sto-mars.com.ua", new HashMap());
             sites.put("505465.ru", new HashMap());   
             sites.put("postelnoe.pro", new HashMap());  
             sites.put("massandra.tw1.ru", new HashMap());
             sites.put("fores-tehno.com.ua", new HashMap());     
             sites.put("atmosphere", new HashMap());  
             
             sites.put("info-org.hol.es", new HashMap()); 
             sites.put("zigmund.net", new HashMap()); 
             sites.put("xn----7sbc0bmmi2gwb.xn--p1ai", new HashMap());  // Рыбка-моя.рф
             sites.put("itrue33.ru", new HashMap()); 
             sites.put("ivkran.ru", new HashMap()); 
             sites.put("ivkran.pmk37.ru", new HashMap()); 
             sites.put("xn--d1acalvcgirdbggh6jh.xn--p1ai", new HashMap()); //доступныесистемы.рф
             sites.put("rideordie.com.ua", new HashMap()); 
             sites.put("forhome.by", new HashMap()); 
             sites.put("intersystem.com.ua", new HashMap()); 
             sites.put("39d-old.xt-demo.ru", new HashMap()); 
             sites.put("39d-mid.xt-demo.ru", new HashMap()); 
             sites.put("39d-new.xt-demo.ru", new HashMap()); 
             sites.put("vashaapteka.com.ua", new HashMap()); 
             sites.put("nazakaz40.ru", new HashMap()); 
             sites.put("zapravka.online", new HashMap()); 
             sites.put("9v.ru", new HashMap()); 
             sites.put("etalon-mbl.ru", new HashMap()); 
             sites.put("test.wsfp.ru", new HashMap()); 
             sites.put("militarymodel.com.ua", new HashMap()); 
             sites.put("modelsworld.kiev.ua", new HashMap()); 
             sites.put("podarki-i-s.ru", new HashMap()); 
             sites.put("0mw.ru", new HashMap()); 
             sites.put("stab.by", new HashMap()); 
             sites.put("jetscart.com", new HashMap()); 
             sites.put("cdt-f.ru", new HashMap()); 
             sites.put("mabax.myjino.ru", new HashMap()); 
             sites.put("obnova.top", new HashMap()); 
             sites.put("obnova.top", new HashMap()); 
             sites.put("4glaza.pampa.pw", new HashMap()); 
             sites.put("4glaza-kirov.ru", new HashMap()); 
             sites.put("techholod.com", new HashMap()); 
             //пультофф.рф
             sites.put("xn--k1agbndja4d.xn--p1ai", new HashMap()); 
             sites.put("topidi.ru", new HashMap());
             
               sites.put("babyshick.ru", new HashMap()); 
             sites.put("zhuravka-torg.com.ua", new HashMap()); 
             sites.put("euroanker.by", new HashMap()); 
             sites.put("tiski.by", new HashMap()); 
             sites.put("hypershina.com.ua", new HashMap()); 
             sites.put("ct42337.tmweb.ru", new HashMap()); 
             sites.put("tehnolin.ru", new HashMap()); 
             sites.put("new.tehnolin.ru", new HashMap()); 
             //технолин.рф
             sites.put("xn--e1agihcdzz.xn--p1ai", new HashMap()); 
             sites.put("mobilnetics.ru", new HashMap()); 
             sites.put("only-automation.ru", new HashMap()); 
             sites.put("processtehnika.ru", new HashMap()); 
             sites.put("novoazovska.com.ua", new HashMap()); 
             sites.put("bmarket.com.ua", new HashMap()); 
             sites.put("azov-more.ru", new HashMap()); 
             
             sites.put("localhost", new HashMap()); 
             sites.put("dvernoy.info", new HashMap()); 
             sites.put("ardos-kmv.ru", new HashMap()); 
             sites.put("paraobuvi.com.ua", new HashMap()); 
             sites.put("7sound.ru", new HashMap()); 
             sites.put("ds-motor.ru", new HashMap()); 
             sites.put("ds-motor.ru", new HashMap()); 
             sites.put("xn----gtbziamcl.xn--p1ai", new HashMap()); //дс-мотор.рф
             sites.put("ikea-na-dom.ru", new HashMap());
             sites.put("avtosale54.ru", new HashMap()); 
             sites.put("trossnk.myjino.ru", new HashMap());
             sites.put("test.fores-tehno.com.ua", new HashMap());
             sites.put("niepaletytoniu.pl", new HashMap());
             sites.put("test1.ru", new HashMap());
             sites.put("carbon-center.ru", new HashMap());
             sites.put("sportnation.su", new HashMap());
             sites.put("davto.dp.ua", new HashMap());
             sites.put("eroman.by", new HashMap());
             sites.put("h99661m8.bget.ru", new HashMap());
             sites.put("honeykate.ru", new HashMap());
             sites.put("electronic-24.ru", new HashMap());
             sites.put("avtosale54.ru", new HashMap());
             sites.put("maysternya.net", new HashMap());
             sites.put("nifta-mebel.ru", new HashMap());
             sites.put("teploprok.com", new HashMap());
             sites.put("v4.experttepla.com.ua", new HashMap());
             sites.put("podarkinadom.kz", new HashMap());
             sites.put("evrometall.com", new HashMap());
             sites.put("vipsigna.ru", new HashMap());
             sites.put("kan42.ru", new HashMap());
             sites.put("kagn.ru", new HashMap());
             sites.put("ngs142.ru", new HashMap());
             sites.put("kartcrg.ru", new HashMap());
             sites.put("berestaurala.ru", new HashMap());
             sites.put("ipcam25.pe.hu", new HashMap());
             sites.put("bikepark.com.ua", new HashMap());
             sites.put("cafe31.ru", new HashMap());
             sites.put("kronos-nn.ru", new HashMap());
             sites.put("elogroup.ru", new HashMap());
             sites.put("avtopilot-ekat.ru", new HashMap());
             
            return sites;
        }
    }

    public HashMap getLicensedSite() {
        Preferences prefs = Preferences.userRoot().node(GlobalVars.name);
        String site = prefs.get("site_host", "nodomen.ru");
        site = site.replace("www\\.", "");
        //System.out.println("Site=" + site);
        sites ss = new sites();
        HashMap m = ss.getSitesData();
        //System.out.print(m.toString());
        HashMap object = (HashMap) m.get(site);
        HashMap currentSite = new HashMap() {

            {
                put("yamarket", "0");
                put("standart_priceloader", "0");
                put("export_csv", "0");
                put("justcom", "0");

            }
        };
        if (null != object) {
            currentSite.putAll(object);
        }
        currentSite.put("site", site);
        return currentSite;
    }
}
