package com.handsometaoa.desensitization;

import com.alibaba.fastjson.JSON;
import com.handsometaoa.desensitization.dto.SensitivePath;
import com.handsometaoa.desensitization.enums.DesensitizedType;
import com.handsometaoa.desensitization.util.MaskUtils;
import com.handsometaoa.desensitization.util.ToolUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: handsometaoa
 * @description 对象/Json字符串脱敏工具类 (此方法已经废弃, 后续将移除）
 * @date: 2024/10/18 19:24
 */

@Deprecated
public class SensitiveUtils {

    private static final int MOBILE_PHONE_TYPE = 1;

    /**
     * 将字符串手机号进行脱敏
     *
     * @param phone 字符串手机号: 13455556666
     * @return 134****6666
     */
    public static String desMobilePhone(String phone) {
        if (ToolUtils.isEmpty(phone)) {
            return phone;
        }
        return MaskUtils.mobileMaskFunction.apply(phone);
    }

    /**
     * 将Json字符串中指定路径内容脱敏
     *
     * @param jsonStr json字符串
     * @param path    单个路径
     * @return 脱敏后的Json字符串
     */
    public static String desMobilePhone(String jsonStr, String path) {
        return desJson(jsonStr, new HashSet<>(Collections.singletonList(path)), MOBILE_PHONE_TYPE);
    }

    /**
     * 将Json字符串中指定路径内容脱敏
     *
     * @param jsonStr json字符串
     * @param pathSet 多个路径
     * @return 脱敏后的Json字符串
     */
    public static String desMobilePhone(String jsonStr, Set<String> pathSet) {
        return desJson(jsonStr, pathSet, MOBILE_PHONE_TYPE);
    }


    /**
     * 将对象中指定路径内容脱敏
     *
     * @param object 要脱敏的对象
     * @param path   单个路径
     * @return 脱敏后的Json字符串
     */
    public static String desMobilePhone(Object object, String path) {
        return desJson(object, new HashSet<>(Collections.singletonList(path)), MOBILE_PHONE_TYPE);
    }


    /**
     * 将对象中指定路径内容脱敏
     *
     * @param object  要脱敏的对象
     * @param pathSet 多个路径
     * @return 脱敏后的Json字符串
     */
    public static String desMobilePhone(Object object, Set<String> pathSet) {
        return desJson(object, pathSet, MOBILE_PHONE_TYPE);
    }


    /**
     * 脱敏对象中的字段 (已经废弃，仅支持手机号脱敏）
     *
     * @param object  对象
     * @param pathSet 要脱敏字段在Json字符串的位置集合 路径用 PATH_SPLIT_CHAR 分割
     * @return 脱敏后的Json字符串
     */
    public static String desJson(Object object, Set<String> pathSet, int type) {
        HashSet<SensitivePath> sensitivePaths = new HashSet<>();
        for (String path : pathSet) {
            sensitivePaths.add(SensitivePath.withBuiltInRule(path, DesensitizedType.MOBILE_PHONE));
        }
        return com.handsometaoa.desensitization.util.SensitiveUtils.des(JSON.toJSONString(object), sensitivePaths);
    }


    /**
     * 脱敏JsonStr中的字段 (已经废弃，仅支持手机号脱敏）
     *
     * @param jsonStr json字符串
     * @param pathSet 要脱敏字段在Json字符串的位置集合 路径用 PATH_SPLIT_CHAR 分割
     * @return 脱敏后的Json字符串
     */
    public static String desJson(String jsonStr, Set<String> pathSet, int type) {
        HashSet<SensitivePath> sensitivePaths = new HashSet<>();
        for (String path : pathSet) {
            sensitivePaths.add(SensitivePath.withBuiltInRule(path, DesensitizedType.MOBILE_PHONE));
        }
        return com.handsometaoa.desensitization.util.SensitiveUtils.des(jsonStr, sensitivePaths);
    }

}