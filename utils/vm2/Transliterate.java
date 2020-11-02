/**
 * Класс переводит русский текст в транслит. Например, строка "Текст" будет
 * преобразована в "Tekst".
 * User: Deady
 * Date: 04.12.2007
 * Time: 15:56:47
 */
package utils.vm2;

/**
 *
 * @author grigory
 */
@SuppressWarnings("empty-statement")
public class Transliterate {

    private static final char[][] charTable = new char[65536][];

    static {
        charTable['А'] = "A".toCharArray();
        charTable['Б'] = "B".toCharArray();
        charTable['В'] = "V".toCharArray();
        charTable['Г'] = "G".toCharArray();
        ;
        charTable['Д'] = "D".toCharArray();
        ;
        charTable['Е'] = "E".toCharArray();
        ;
        charTable['Ё'] = "E".toCharArray();
        ;
        charTable['Ж'] = "ZH".toCharArray();
        ;
        charTable['З'] = "Z".toCharArray();
        ;
        charTable['И'] = "I".toCharArray();
        ;
        charTable['Й'] = "I".toCharArray();
        ;
        charTable['К'] = "K".toCharArray();
        ;
        charTable['Л'] = "L".toCharArray();
        ;
        charTable['М'] = "M".toCharArray();
        ;
        charTable['Н'] = "N".toCharArray();
        ;
        charTable['О'] = "O".toCharArray();
        ;
        charTable['П'] = "P".toCharArray();
        ;
        charTable['Р'] = "R".toCharArray();
        ;
        charTable['С'] = "S".toCharArray();
        ;
        charTable['Т'] = "T".toCharArray();
        ;
        charTable['У'] = "U".toCharArray();
        ;
        charTable['Ф'] = "F".toCharArray();
        ;
        charTable['Х'] = "H".toCharArray();
        ;
        charTable['Ц'] = "C".toCharArray();
        ;
        charTable['Ч'] = "CH".toCharArray();
        ;
        charTable['Ш'] = "SH".toCharArray();
        ;
        charTable['Щ'] = "SH".toCharArray();
        ;
        charTable['Ъ'] = "".toCharArray();
        ;
        charTable['Ы'] = "Y".toCharArray();
        ;
        charTable['Ь'] = "".toCharArray();
        charTable['Э'] = "E".toCharArray();
        charTable['Ю'] = "U".toCharArray();
        charTable['Я'] = "YA".toCharArray();

        for (int i = 0; i < charTable.length; i++) {
            char idx = (char) i;
            char lower = Character.toLowerCase(idx);
            if (charTable[i] != null) {
                charTable[lower] = toLowerCase(charTable[i]);
            }
        }
    }

    /**
     * Переводит русский текст в транслит. В результирующей строке
     * каждая русская буква будет заменена на соответствующую английскую.
     * Не русские символы останутся прежними.
     * аббревиатуру.
     *
     * @param text исходный текст с русскими символами
     * @return результат
     */
    public static void main(String[] args) {
        String test = "Привет, Мир. Это Длинная Строка с Разными символами русского алфавита.";
        System.out.println("toTranslit(test) = " + toTranslit(test));
        System.out.println("toFieldName(test) = " + toFieldName(test));
    }

    private static char[] toLowerCase(char[] chars) {
        char[] r = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            r[i] = Character.toLowerCase(chars[i]);

        }
        return r;
    }

    public static String toTranslit(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char[] replace = charTable[text.charAt(i)];
            if (replace == null) {
                sb.append(text.charAt(i));
            } else {
                sb.append(replace);
            }
        }
        return sb.toString();
    }

    public static String toFieldName(String text) {
        String t = toTranslit(text);
        t = t.replaceAll("\\W", "");
        if(t.length()>40){
        return t.toLowerCase().substring(0, 35);
        }else{
           return "f_"+t.toLowerCase();
        }
    }

    public static String toFolderName(String text) {
        String t = toTranslit(text);
        t = t.replaceAll("\\W", "");
        if(t.length()>40){
        return t.toLowerCase().substring(0, 35);
        }else{
           return t.toLowerCase();
        }
    }

}
