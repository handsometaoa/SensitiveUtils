
package com.handsometaoa.desensitization.util;

import com.handsometaoa.desensitization.algorithm.DefaultDesensitizer;
import com.handsometaoa.desensitization.algorithm.MobilePhoneDesensitizer;
import com.handsometaoa.desensitization.algorithm.PasswordDesensitizer;
import com.handsometaoa.desensitization.enums.DesensitizedType;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author handsometaoa
 * @date 2025/12/4 16:44
 */
public class MaskUtils {

    // 内置的脱敏函数
    public static Function<String, String> defaultMaskFunction = (new DefaultDesensitizer()).getDesFunction();
    public static Function<String, String> mobileMaskFunction = (new MobilePhoneDesensitizer()).getDesFunction();
    public static Function<String, String> passwordMaskFunction = (new PasswordDesensitizer()).getDesFunction();

    public static Function<String, String> getBuiltInTypeFunction(DesensitizedType type) {
        switch (type) {
            case DEFAULT:
                return defaultMaskFunction;
            case MOBILE_PHONE:
                return mobileMaskFunction;
            case PASSWORD:
                return passwordMaskFunction;
            default:
                return value -> value;
        }
    }

    public static String maskString(String value, Function<String, String> function) {
        return function.apply(value);
    }


    /**
     * 重复字符指定次数
     *
     * @param chat  字符
     * @param count 重复次数
     * @return 重复后的字符串
     */
    public static String generateRepeatedChar(char chat, int count) {
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        Arrays.fill(chars, chat);
        return new String(chars);
    }


    /**
     * 替换指定区间的字符串为*
     *
     * @param sourceStr    源字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @return 替换后的字符串
     */
    public static String replaceWithAsterisk(String sourceStr, Integer startInclude, Integer endExclude) {
        return replace(sourceStr, startInclude, endExclude, '*');
    }

    /**
     * 替换指定区间的字符串
     *
     * @param sourceStr    源字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @param replaceChar  替换的字符
     * @return 替换后的字符串
     */
    public static String replace(String sourceStr, Integer startInclude, Integer endExclude, char replaceChar) {
        if (ToolUtils.isEmpty(sourceStr)) {
            return sourceStr;
        }
        int len = sourceStr.length();
        if (startInclude > len) {
            return sourceStr;
        } else {
            endExclude = Math.min(len, endExclude);
            if (startInclude > endExclude) {
                return sourceStr;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    if (i >= startInclude && i < endExclude) {
                        sb.append(replaceChar);
                    } else {
                        sb.append(sourceStr.charAt(i));
                    }
                }
                return sb.toString();
            }
        }
    }

}
