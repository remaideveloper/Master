package com.alegangames.master.util;


public class SkinUtil {


    /**
     * Цена данного скина
     *
     * @param category категория
     * @param i        номер скина в категории
     */
    public static int getSkinPrice(String category, int i) {
        boolean isPremium = false;
        if (category.equals("boy")) {
            if (i == 216 || i == 249 || i == 281 || i == 422)
                isPremium = true;
        }
        if (category.equals("girl")) {
            if (i == 250 || i == 190 || i == 60 || i == 412)
                isPremium = true;
        }
        if (category.equals("anime")) {
            if (i == 221 || i == 429 || i == 325 || i == 9)
                isPremium = true;
        }
        if (category.equals("military")) {
            if (i == 191 || i == 343 || i == 79 || i == 235)
                isPremium = true;
        }
        if (category.equals("animal")) {
            if (i == 465 || i == 310 || i == 12 || i == 171)
                isPremium = true;
        }
        if (category.equals("heroes")) {
            if (i == 125 || i == 445 || i == 363 || i == 122)
                isPremium = true;
        }
        if (category.equals("monsters")) {
            if (i == 468 || i == 498 || i == 61 || i == 46)
                isPremium = true;
        }
        if (category.equals("robots")) {
            if (i == 337 || i == 475 || i == 139 || i == 419)
                isPremium = true;
        }
        if (category.equals("games")) {
            if (i == 290 || i == 419 || i == 356 || i == 444)
                isPremium = true;
        }
        if (category.equals("joke")) {
            if (i == 330 || i == 49 || i == 178 || i == 78)
                isPremium = true;
        }
        if (category.equals("characters")) {
            if (i == 396 || i == 24 || i == 236 || i == 270)
                isPremium = true;
        }
        if (category.equals("baby")) {
            if (i == 177 || i == 134 || i == 239 || i == 468)
                isPremium = true;
        }
        if (category.equals("camouflage")) {
            if (i == 194 || i == 191 || i == 161 || i == 186)
                isPremium = true;
        }
        return isPremium ? 1 : 0;
    }
}
