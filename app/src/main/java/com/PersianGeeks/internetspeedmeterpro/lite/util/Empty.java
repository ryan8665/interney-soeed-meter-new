package com.PersianGeeks.internetspeedmeterpro.lite.util;

import java.util.Collection;
import java.util.Map;


public class Empty {
    public static boolean isEmpty(Object o){
        return o == null;
    }
    public static boolean isNotEmpty(Object o){
        if(o instanceof String){
            return isNotEmpty((String)o);
        }
        if(o instanceof Object[]){
            return isNotEmpty((Object[])o);
        }
        if(o instanceof Collection){
            return isNotEmpty((Collection)o);
        }
        if(o instanceof Map){
            return isNotEmpty((Map)o);
        }
        return o != null;
    }
    public static boolean isEmptyCustom(Object o, Object... emptyValues){
        if (o == null){
            return true;
        }
        if(isNotEmpty(emptyValues)){
            for (Object emptyValue : emptyValues) {
                if(o.equals(emptyValue)){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isNotEmptyCustom(Object o, Object... emptyValues){
        return !isEmptyCustom(o, emptyValues);
    }

    public static boolean isEmpty(Object[] array){
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array){
        return array != null && array.length > 0;
    }

    public static boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection){
        return collection != null && !collection.isEmpty();
    }

    public static boolean isEmpty(Map map){
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map){
        return map != null && !map.isEmpty();
    }

    public static boolean isEmpty(String str){
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str){
        return str != null && !str.isEmpty();
    }

    public static boolean isEmptyCustom(String str, String... emptyValues){
        if (isEmpty(str)){
            return true;
        }
        if(isNotEmpty(emptyValues)){
            for (Object emptyValue : emptyValues) {
                if(str.equals(emptyValue)){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isNotEmptyCustom(String  str, String... emptyValues){
        return !isEmptyCustom(str, emptyValues);
    }

    public static boolean isEmpty(StringBuilder str){
        return isEmpty(str.toString());
    }

    public static boolean isNotEmpty(StringBuilder str){
        return isNotEmpty(str.toString());
    }

    public static boolean isEmptyCustom(StringBuilder str, String... emptyValues){
        return isEmptyCustom(str.toString(), emptyValues);
    }
    public static boolean isNotEmptyCustom(StringBuilder  str, String... emptyValues){
        return !isEmptyCustom(str, emptyValues);
    }

    public static boolean isNotEmpty(byte[] bytes) {
        return bytes != null && bytes.length > 0;
    }

}