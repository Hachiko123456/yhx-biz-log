package com.yhx.log.util;

/**
 * @author yanghuaxu
 * @date 2022/6/17 11:38
 */
public class StringUtil {

    public static final String COMMA = ",";

    public static int strCount(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

}
