package ru.vncclient.keyboards;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class CyrillicParser {
    private static HashMap<Character, Character> ruEngList = new HashMap<Character, Character>() {{
        put('й', 'q');
        put('ц', 'w');
        put('у', 'e');
        put('к', 'r');
        put('е', 't');
        put('н', 'y');
        put('г', 'u');
        put('ш', 'i');
        put('щ', 'o');
        put('з', 'p');
        put('х', '[');
        put('ъ', ']');
        put('ф', 'a');
        put('ы', 's');
        put('в', 'd');
        put('а', 'f');
        put('п', 'g');
        put('р', 'h');
        put('о', 'j');
        put('л', 'k');
        put('д', 'l');
        put('ж', ';');
        put('э', '\'');
        put('я', 'z');
        put('ч', 'x');
        put('с', 'c');
        put('м', 'v');
        put('и', 'b');
        put('т', 'n');
        put('ь', 'm');
        put('б', ',');
        put('ю', '.');
    }};

    public static int getEngKeyCode(char rus) {
        char eng = ruEngList.get(Character.toLowerCase(rus));
        if (Character.isUpperCase(rus)) {
            eng = Character.toUpperCase(eng);
        }

        return KeyEvent.getExtendedKeyCodeForChar(eng);
    }
}