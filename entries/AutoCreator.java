package entries;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vmre.LocalDB;

public class AutoCreator {

    private static String prefix;
    private static List<HashMap> columns;
    
    public static void createEntriesAndQueries(String sPrefix, List<String> listTables) {
        System.out.println("*******createEntriesAndQueries");
        prefix = sPrefix;
        for (String table : listTables){
            System.out.println(table);
            perform(table);
        }
    }
    
    private static void perform (String table) {
        getTableData(table);
        createEntry(table);
        createQuery(table);
    }
    
    private static void getTableData(String table) {
        columns = new ArrayList<HashMap>();
        try {
            columns = LocalDB.sqlMap.queryForList("getColumnsTable", prefix+table);
        }
        catch (Exception e) {}
    }
    
    private static String getClassField (String tableField, boolean isFirstBig) {
        String result="";
        String tmp=tableField;
        boolean nextBig=isFirstBig;
        while (!tmp.equals("")) {
            String c1 = tmp.substring(0,1);
            if (c1.equals("_"))
                nextBig = true;
            else {
                if (nextBig)
                    result = result+c1.toUpperCase();
                else
                    result = result+c1.toLowerCase();
                nextBig = false;
            }
            tmp = tmp.substring(1);
        }
        return result;
    }
    
    private static String getClassType (String tableType) {
        if (tableType.toUpperCase().equals("BIGINT")||
            tableType.toUpperCase().equals("BINARY")||
            tableType.toUpperCase().equals("INTEGER")||
            tableType.toUpperCase().equals("SMALLINT"))
            return "Integer";
        if (tableType.toUpperCase().equals("DECIMAL"))
            return "Double";
        if (tableType.toUpperCase().equals("BOOLEAN"))
            return "Boolean";
        if (tableType.toUpperCase().equals("CHAR")||
            tableType.toUpperCase().equals("TIMESTAMP")||
            tableType.toUpperCase().equals("VARCHAR"))
            return "String";
        return "?";
    }
    
    private static String getClassDefaultValue (String tableType) {
        if (tableType.toUpperCase().equals("BIGINT")||
            tableType.toUpperCase().equals("BINARY")||
            tableType.toUpperCase().equals("INTEGER")||
            tableType.toUpperCase().equals("SMALLINT"))
            return "0";
        if (tableType.toUpperCase().equals("DECIMAL"))
            return "0.0";
        if (tableType.toUpperCase().equals("BOOLEAN"))
            return "false";
        if (tableType.toUpperCase().equals("CHAR")||
            tableType.toUpperCase().equals("VARCHAR"))
            return "\"\"";
        if (tableType.toUpperCase().equals("TIMESTAMP"))
            return "\"0000-00-00 00:00:00\"";
        return "?";
    }

    private static void createEntry(String table) {
        String className = getClassField(table, true)+"Entry";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                        new OutputStreamWriter(
                        new FileOutputStream("c:\\"+className+".java"), "windows-1251"));
            
            writer.write("package entries;\n");
            writer.write("\n");
            writer.write("public class "+className+" {\n");
            writer.write("\n");
            for (HashMap column : columns)
                writer.write("    private "+getClassType((String)column.get("type"))+
                                        " "+getClassField((String)column.get("name"), false)+
                                        " = "+getClassDefaultValue((String)column.get("type")) +
                                        ";\n");
            
            for (HashMap column : columns) {
                writer.write("\n");

                writer.write("    public "+getClassType((String)column.get("type"))+
                                     " get"+getClassField((String)column.get("name"), true)+
                                     "() {\n");
                writer.write("        return this."+getClassField((String)column.get("name"), false)+
                                       ";\n    }\n");

                writer.write("\n");
                
                writer.write("    public void set"+getClassField((String)column.get("name"), true)+
                                     "("+getClassType((String)column.get("type"))+" "+
                                   getClassField((String)column.get("name"), false)+") {\n");
                writer.write("        this."+getClassField((String)column.get("name"), false)+
                                       " = "+getClassField((String)column.get("name"), false)+
                                       ";\n    }\n");
            }
            
            writer.write("}");
            
            writer.close();
        }
        catch (Exception e) {}
    }
    
    private static void createQuery(String table) {
        String className = getClassField(table, true);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                        new OutputStreamWriter(
                        new FileOutputStream(className+".xml"), "windows-1251"));
            
            //INSERT
            writer.write("    <insert id=\"ins"+className+"\" parameterClass=\"entries."+
                    className+"Entry>\n");
            writer.write("        INSERT INTO PUBLIC."+prefix+table+"(\n");
            
            int i=1;
            for (HashMap column : columns) {
                if (i>1) {
                    writer.write("            "+(String)column.get("name"));
                    if (i<columns.size())
                        writer.write(",");
                    writer.write("\n");
                }
                i++;
            }
            
            writer.write("        )\n        VALUES(\n");
            
            i=1;
            for (HashMap column : columns) {
                if (i>1) {
                    writer.write("            #"+getClassField((String)column.get("name"),false)+"#");
                    if (i<columns.size())
                        writer.write(",");
                    writer.write("\n");
                }
                i++;
            }
            
            writer.write("        )\n");
            writer.write("        <selectKey resultClass=\"int\" keyProperty=\"id\">\n");
            writer.write("            CALL IDENTITY()\n");
            writer.write("        </selectKey\n");
            writer.write("    </insert>\n\n");
                    
            //UPDATE
            writer.write("    <update id=\"upd"+className+"\" parameterClass=\"entries."+
                    className+"Entry>\n");
            writer.write("        UPDATE PUBLIC."+prefix+table+" SET \n");
            
            i=1;
            for (HashMap column : columns) {
                if (i>1) {
                    writer.write("            "+(String)column.get("name")+
                            " = #"+getClassField((String)column.get("name"), false) +"#");
                    if (i<columns.size())
                        writer.write(",");
                    writer.write("\n");
                }
                i++;
            }
            
            writer.write("        WHERE "+columns.get(0).get("name") +" = #"+
                    getClassField((String)columns.get(0).get("name"), false) +"#\n");
            writer.write("    </update>\n\n");
            
            //DELETE
            writer.write("    <delete id=\"del"+className+"\" parameterClass=\"entries."+
                    className+"Entry>\n");
            writer.write("        DELETE FROM PUBLIC."+prefix+table+"\n");
            
            writer.write("        WHERE "+columns.get(0).get("name") +" = #"+
                    getClassField((String)columns.get(0).get("name"), false) +"#\n");
            writer.write("    </delete>\n\n");
            
            writer.close();
        }
        catch (Exception e) {}
    }
    
}
