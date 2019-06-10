package com.alegangames.master.util;


import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;

import java.text.Normalizer;

public class StringUtil {

    /**
     * @return true если зачения строк одиковы игнорируя регистр
     */
    public static boolean containsIgnoreCase(String s1, String s2) {
        return s1.toLowerCase().contains(s2.toLowerCase());
    }

    /**
     * Создает String[] из String в которой подстроки разделены запятой.
     *
     * @param string String с подстроками
     * @return Полученный String[]
     */
    public static String[] splitStringComma(String string) {
        return string.replaceAll("\\s+", "").split(",");
    }

    /**
     * Заменяет строку из Json на String
     * Если в строке Json есть символ ${newline} заменяет на новую строку
     *
     * @param string Строка из Json
     * @return Полученную строку с изменениями
     */
    public static String replaseJsonString(String string) {
        return string.replaceAll("[$][{]newline[}]", "\n");
    }

    /**
     * Получает первый элемент из String[]
     *
     * @param string
     * @return Полученную строку из первого элемента String[]
     */
    public static String getFirstStringFromArray(String string) {
        String[] images = StringUtil.splitStringComma(string);
        return images[0];
    }

    /**
     * Заменяет все буквы и символы в String на пустой символ и оставлет только цифры
     *
     * @return Значение ввиде int которые были в String
     */
    public static int getIntFromString(String string) {
        return Integer.parseInt(string.replaceAll("\\D+", ""));
    }

    public static CharSequence highLightSearchText(String search, String originalText) {
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        search = search.toLowerCase();
        int start = normalizedText.indexOf(search);
        if (start < 0) {
            return originalText;
        } else {
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());
                highlighted.setSpan(new BackgroundColorSpan(Color.parseColor("#F4E115")), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }


}
