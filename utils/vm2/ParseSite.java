/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.vm2;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.prefs.Preferences;
import tools.Messages;
import vmre.utils.FileSystem;
import vmre.utils.GlobalClasses;
import vmre.utils.ImageResize;

/**
 *
 * @author msv
 */
public class ParseSite {
    
    private static String getClassName(String siteName) {
        String result = "";
        String tmp = siteName;
        boolean nextBig = true;
        while (!tmp.equals("")) {
            String c1 = tmp.substring(0, 1);
            if (c1.equals(".")) {
                nextBig = true;
            } else {
                if (nextBig) {
                    result = result + c1.toUpperCase();
                } else {
                    result = result + c1.toLowerCase();
                }
                nextBig = false;
            }
            tmp = tmp.substring(1);
        }
        return result;
    }
    
    public static HashMap ParseProduct(entries.ProdEntry pe, entries.MediasEntry me, String donorsitename) {
        
        HashMap ret = new HashMap();
        Preferences prefs = GlobalClasses.getPrefs();
        Boolean loadimage = Boolean.parseBoolean(prefs.get("loadimageym", "true"));
        Boolean loadsdesc = Boolean.parseBoolean(prefs.get("loadsdescym", "true"));
        Boolean loadfdesc = Boolean.parseBoolean(prefs.get("loadfdescym", "true"));
        
        System.out.println("Desc" + pe.getVirtuemart_product_id() + "|" + pe.getProduct_name());
        try {
            HashMap desc = new HashMap();
            HashMap param = new HashMap();
            param.put("pname", pe.getProduct_name());
            param.put("donorsite", donorsitename);
            param.put("sku", pe.getProduct_sku());
            param.put("product_id", pe.getVirtuemart_product_id());
            
            try {
                Messages.show("parserutils." + getClassName(donorsitename) + "Parser");
                String parserClass = "parserutils." + getClassName(donorsitename) + "Parser";
                Messages.show("parserclass=" + parserClass);
                Class<?> c = Class.forName(parserClass);
                
                Method m = c.getDeclaredMethod("getDescription", HashMap.class);
                desc = (HashMap) m.invoke(null, param);
            } catch (Exception e) {
                Messages.sboi(e);
            }
            
            if (null == desc) {
                return ret;
            }
            
            desc.put("product_id", pe.getVirtuemart_product_id());
            
            Boolean pe_changed = false;
            if (!desc.get("description").toString().equalsIgnoreCase("") && loadsdesc) {
                pe.setProduct_s_desc(desc.get("description").toString());
                pe_changed = true;
            }
            
            if (!desc.get("fulldescription").toString().equalsIgnoreCase("") && loadfdesc) {
                pe.setProduct_desc(desc.get("fulldescription").toString());
                pe_changed = true;
            }
            
            if (pe_changed) {
                ret.put("pe", pe);
                pe_changed = true;
            }

            //copy images
            if (!(desc.get("bigimage").toString().equalsIgnoreCase("")) && loadimage) {
//                v.statusMessageLabel.setText("Загрузка большого изображения:" + desc.get("bigimage").toString());
                String bigImage = desc.get("bigimage")+"";
                String bigImage2 = desc.get("bigimage2")+"";
                String ext = bigImage.substring(bigImage.lastIndexOf('.') + 1);
                if (ext.length() != 3) {
                    ext = "jpg";
                }
                String path = exchangedata.ExchangeImages.getLocalImagePath();
                String folder = path + File.separatorChar + "images" + File.separator + "stories" + File.separator + "virtuemart" + File.separator + "product";
                File xf;
                xf = new File(folder);
                if (!xf.exists()) {
                    xf.mkdirs();
                }
                
                String filename4resize = Transliterate.toFolderName(pe.getProduct_name()) + pe.getVirtuemart_product_id();
                String filename = filename4resize + "." + ext;
                String fullImg = folder + File.separatorChar + filename;
                Messages.show("xxxfullimage=" + fullImg);
                
                if (donorsitename.equalsIgnoreCase("merlion.ru")) {
                    try {
                        FileSystem.downloadFileException(bigImage, fullImg);
                    } catch (Exception e) {
                        //Messages.sboi(e);
                        FileSystem.downloadFile(bigImage2, fullImg);
                    }
                } else {
                     FileSystem.downloadFile(bigImage, fullImg);
                }
                xf = new File(fullImg);
                if (xf.exists()) {
                    Messages.show("Загрузка большого успешно завершена");
                } else {
                    Messages.show("Загрузка большого изображения не удалась");
                }
                
                ImageResize ir = new ImageResize(fullImg, null, null, prefs);
                HashMap m = ir.doThumbImageProduct(new File(fullImg), path, filename4resize);
                
                if (null != m.get("setThumbImage")) {
                } else {
                    System.out.println("Thumbnail=null");
                }
                
                me.setFileUrl("images/stories/virtuemart/product/" + filename);
                me.setFileUrlThumb("images/stories/virtuemart/product/resized/" + filename);
                me.setPublished(1);
                me.setFileTitle(pe.getProduct_name());
                if ("png".equalsIgnoreCase(ext)) {
                    me.setFileMimetype("image/png");
                } else if ("gif".equalsIgnoreCase(ext)) {
                    me.setFileMimetype("image/gif");
                } else {
                    me.setFileMimetype("image/jpeg");
                }
                me.setFileType("product");
                
                Messages.show("me.getFileUrl()=" + me.getFileUrl());
                
                ret.put("me", me);
            }
            
        } catch (Exception e) {
            Messages.sboi(e);
        }
        return ret;
    }
}
