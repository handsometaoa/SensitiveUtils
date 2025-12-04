
package com.handsometaoa.desensitization.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handsometaoa.desensitization.dto.SensitivePath;
import com.handsometaoa.desensitization.enums.DesensitizedType;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author: handsometaoa
 * @description 对象/Json字符串脱敏工具类
 * @date: 2024/10/18 19:24
 */

public class SensitiveUtils {

    private static final Logger logger = Logger.getLogger(SensitiveUtils.class.getName());


    // 路径分隔符
    public static final String PATH_SPLIT_CHAR = "#";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static String des(String jsonStr, Set<SensitivePath> pathSet) {
        return desMultiLevelJson(jsonStr, pathSet);
    }

    /**
     * 处理多层级JSON 脱敏
     *
     * @param jsonStr {"phone":"13444444444","123":{"phone":"13444444444"}}
     * @param pathSet ["phone","123#phone"]
     * @return 脱敏后字符串 {"123":{"phone":"134****4444"},"phone":"134****4444"}
     */
    private static String desMultiLevelJson(String jsonStr, Set<SensitivePath> pathSet) {
        try {
            if (!JSON.isValid(jsonStr) || pathSet == null || pathSet.isEmpty()) {
                return jsonStr;
            }
            Object object = JSON.parse(jsonStr);
            List<JSONObject> pendingJsonObjectList = getPendingJsonObjectList(object);
            for (JSONObject jsonObject : pendingJsonObjectList) {
                for (SensitivePath sensitivePath : pathSet) {
                    List<String> fieldNameList = Arrays.stream(sensitivePath.getFieldPath().split(PATH_SPLIT_CHAR)).collect(Collectors.toList());
                    recursiveReplacement(jsonObject, fieldNameList, sensitivePath);
                }
            }
            return JSON.toJSONString(object);
        } catch (Exception e) {
            logger.warning("脱敏失败! jsonStr " + jsonStr + ", pathSet => " + pathSet + "," + e);
            return jsonStr;
        }
    }

    /**
     * 递归处理要处理的JSONObject对象
     *
     * @param jsonObject    当前层级JSONObject对象
     * @param fieldNameList 路径
     * @param sensitivePath 脱敏类型
     */
    private static void recursiveReplacement(JSONObject jsonObject, List<String> fieldNameList, SensitivePath sensitivePath) {
        if (jsonObject == null || fieldNameList == null || fieldNameList.isEmpty()) {
            return;
        }
        List<String> currentFieldNameList = new ArrayList<>(fieldNameList);
        String currentFieldName = currentFieldNameList.get(0);
        if (ToolUtils.isEmpty(currentFieldName)) {
            return;
        }
        if (currentFieldNameList.size() == 1) {
            Object fieldValue = jsonObject.get(currentFieldName);
            if (fieldValue instanceof String) {
                String value = getDesValue(sensitivePath, (String) fieldValue);
                jsonObject.put(currentFieldName, value);
            }
            // 只处理String类型
        } else {
            // 当前还未到最深层
            Object currentJsonObject = jsonObject.get(currentFieldName);
            if (currentJsonObject == null) {
                return;
            }
            currentFieldNameList.remove(0);
            List<JSONObject> pendingJsonObjectList = getPendingJsonObjectList(currentJsonObject);
            for (JSONObject pendingJsonObject : pendingJsonObjectList) {
                recursiveReplacement(pendingJsonObject, currentFieldNameList, sensitivePath);
            }
        }
    }

    private static String getDesValue(SensitivePath desType, String fieldValue) {
        String value = fieldValue;
        Function<String, String> function = null;
        if (desType.getFlag() == 1) {
            function = MaskUtils.getBuiltInTypeFunction(desType.getDesensitizedType());
        } else if (desType.getFlag() == 2) {
            function = desType.getCustomFunction();
        }
        value = MaskUtils.maskString(value, function);
        return value;
    }

    private static List<JSONObject> getPendingJsonObjectList(Object object) {
        List<JSONObject> pendingJsonObjectList = new ArrayList<>();
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            jsonArray.forEach(o -> {
                if (o instanceof JSONObject) {
                    pendingJsonObjectList.add((JSONObject) o);
                } else if (o instanceof JSONArray) {
                    pendingJsonObjectList.addAll(getPendingJsonObjectList(o));
                } else {
                    // ? 不处理
                }
            });
        } else if (object instanceof JSONObject) {
            pendingJsonObjectList.add((JSONObject) object);
        } else {
            // ? 不处理
        }
        return pendingJsonObjectList;
    }

    public static void main(String[] args) {
        String jsonStr = "{\"phone\":\"12444444444\",\"123\":{\"phone\":\"12444444444\"}}";
        Set<SensitivePath> pathSet = new HashSet<>();
        pathSet.addAll(SensitivePath.withBuiltInRuleList(Arrays.asList("phone", "123#phone"), DesensitizedType.MOBILE_PHONE));

        Long startTime = System.currentTimeMillis();
        String s = desMultiLevelJson(jsonStr, pathSet);
        System.out.println(s);
        System.out.println("耗时:" + (System.currentTimeMillis() - startTime));

        Long startTime2 = System.currentTimeMillis();
        String s2 = desMultiLevelJson(jsonStr, pathSet);
        System.out.println(s2);
        System.out.println("耗时:" + (System.currentTimeMillis() - startTime2));
    }

}