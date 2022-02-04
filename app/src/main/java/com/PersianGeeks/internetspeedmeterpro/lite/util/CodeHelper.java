package com.PersianGeeks.internetspeedmeterpro.lite.util;

public class CodeHelper {
    public String[] parsCode(String string){

        try {
            String[] list = string.split("_");
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
