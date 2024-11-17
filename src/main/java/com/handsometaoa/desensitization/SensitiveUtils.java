package com.handsometaoa.desensitization;

import cn.hutool.core.util.DesensitizedUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: handsometaoa
 * @description 对象/Json字符串脱敏工具类
 * @date: 2024/10/18 19:24
 */

public class SensitiveUtils {

    public static final Logger logger = LoggerFactory.getLogger(SensitiveUtils.class);

    // 路径分隔符
    public static final String PATH_SPLIT_CHAR = "#";

    private static final int MOBILE_PHONE_TYPE = 1;

    /**
     * 将字符串手机号进行脱敏
     *
     * @param phone 字符串手机号: 13455556666
     * @return 134****6666
     */
    public static String desMobilePhone(String phone) {
        return desensitize(phone, MOBILE_PHONE_TYPE);
    }

    /**
     * 将Json字符串中指定路径内容脱敏
     *
     * @param jsonStr json字符串
     * @param path    单个路径
     * @return 脱敏后的Json字符串
     */
    public static String desMobilePhone(String jsonStr, String path) {
        Set<String> pathSet = new HashSet<>();
        if (!StringUtils.isEmpty(path)) {
            pathSet.add(path);
        }
        return desJson(jsonStr, pathSet, MOBILE_PHONE_TYPE);
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
        Set<String> pathSet = new HashSet<>();
        if (!StringUtils.isEmpty(path)) {
            pathSet.add(path);
        }
        return desJson(object, pathSet, MOBILE_PHONE_TYPE);
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
     * 脱敏对象中的字段
     *
     * @param object  对象
     * @param pathSet 要脱敏字段在Json字符串的位置集合 路径用 PATH_SPLIT_CHAR 分割
     * @return 脱敏后的Json字符串
     */
    public static String desJson(Object object, Set<String> pathSet, int type) {
        if (object == null) {
            return null;
        }
        return desMultiLevelJson(JSON.toJSONString(object), pathSet, type);
    }


    /**
     * 脱敏JsonStr中的字段
     *
     * @param jsonStr json字符串
     * @param pathSet 要脱敏字段在Json字符串的位置集合 路径用 PATH_SPLIT_CHAR 分割
     * @return 脱敏后的Json字符串
     */
    public static String desJson(String jsonStr, Set<String> pathSet, int type) {
        return desMultiLevelJson(jsonStr, pathSet, type);
    }


    //------------ 核心逻辑 start  ---------------

    /**
     * 处理多层级JSON 脱敏
     *
     * @param jsonStr {"phone":"13444444444","123":{"phone":"13444444444"}}
     * @param pathSet ["phone","123#phone"]
     * @param desType 脱敏类型
     * @return 脱敏后字符串 {"123":{"phone":"134****4444"},"phone":"134****4444"}
     */
    private static String desMultiLevelJson(String jsonStr, Set<String> pathSet, int desType) {
        try {
            if (!isValid(jsonStr) || CollectionUtils.isEmpty(pathSet)) {
                return jsonStr;
            }

            List<JSONObject> pendingJsonObjectList = new ArrayList<>();
            Object object = JSON.parse(jsonStr);
            if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                for (Object o : jsonArray) {
                    if (o instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) o;
                        pendingJsonObjectList.add(jsonObject);
                    } else {
                        // ? 不处理
                    }
                }
            } else if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                pendingJsonObjectList.add(jsonObject);
            } else {
                // ? 不处理
            }

            for (JSONObject jsonObject : pendingJsonObjectList) {
                for (String path : pathSet) {
                    if (StringUtils.isEmpty(path)) {
                        continue;
                    }
                    List<String> fieldNameList = Arrays.stream(path.split(PATH_SPLIT_CHAR)).collect(Collectors.toList());
                    recursiveReplacement(jsonObject, fieldNameList, desType);
                }
            }
            return JSON.toJSONString(object);
        } catch (Exception e) {
            logger.warn("脱敏失败! jsonStr => {},pathSet => {}", jsonStr, pathSet, e);
            return jsonStr;
        }
    }


    /**
     * 递归处理要处理的JSONObject对象
     *
     * @param jsonObject    当前层级JSONObject对象
     * @param fieldNameList 路径
     * @param desType       脱敏类型
     */
    private static void recursiveReplacement(JSONObject jsonObject, List<String> fieldNameList, int desType) {
        if (jsonObject == null || CollectionUtils.isEmpty(fieldNameList)) {
            return;
        }
        List<String> currentFieldNameList = new ArrayList<>(fieldNameList);
        String currentFieldName = currentFieldNameList.get(0);
        if (StringUtils.isEmpty(currentFieldName)) {
            return;
        }
        if (currentFieldNameList.size() == 1) {
            Object fieldValue = jsonObject.get(currentFieldName);
            if (fieldValue instanceof String) {
                jsonObject.put(currentFieldName, desensitize((String) fieldValue, desType));
            }
            // 只处理String类型
        } else {
            // 当前还未到最深层
            Object currentJsonObject = jsonObject.get(currentFieldName);
            if (currentJsonObject == null) {
                return;
            }
            currentFieldNameList.remove(0);

            List<JSONObject> pendingJsonObjectList = new ArrayList<>();
            if (currentJsonObject instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) currentJsonObject;
                for (Object o : jsonArray) {
                    if (o instanceof JSONObject) {
                        pendingJsonObjectList.add((JSONObject) o);
                    } else {
                        // ? 不处理
                    }
                }
            } else if (currentJsonObject instanceof JSONObject) {
                pendingJsonObjectList.add((JSONObject) currentJsonObject);
            } else {
                // 不支持此类型
                // ? 不处理
            }

            for (JSONObject pendingJsonObject : pendingJsonObjectList) {
                recursiveReplacement(pendingJsonObject, currentFieldNameList, desType);
            }
        }
    }


    /**
     * 字符串脱敏
     *
     * @param value   需要脱敏的值
     * @param desType 脱敏类型
     * @return 脱敏结果
     */
    private static String desensitize(String value, int desType) {
        if (desType == MOBILE_PHONE_TYPE) {
            if (StringUtils.isEmpty(value)) {
                return "";
            } else if (value.length() != 11) {
                // 防止给已经加密的号码再次加密
                return value;
            } else {
                return DesensitizedUtil.mobilePhone(value);
            }
        }
        // # 此处可进行扩展
        return value;
    }

    /**
     * 校验字符串是否是json格式
     *
     * @param jsonStr JSON字符串
     * @return true:符合JSON格式  false:不符合JSON格式
     */
    private static boolean isValid(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(jsonStr);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    //------------ 核心逻辑 end  ---------------


    public static void main(String[] args) {
        String str7 = "{\"users\":[{\"user1\":{\"phone\":\"12333333334\"}},{\"user1\":{\"phone\":\"12333333335\"}}]}";
        System.out.println(SensitiveUtils.desMobilePhone(str7, "users#user1#phone"));
    }

}
