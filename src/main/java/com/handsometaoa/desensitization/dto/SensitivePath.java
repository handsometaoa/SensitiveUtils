package com.handsometaoa.desensitization.dto;

import com.handsometaoa.desensitization.enums.DesensitizedType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SensitivePath {

    // 字段路径
    private String fieldPath;
    // 脱敏类型
    private DesensitizedType desensitizedType;
    // 自定义脱敏逻辑
    private Function<String, String> customFunction;
    // 1 内置规则 2 自定义规则 SensitiveType 与 function 二选一
    private final int flag;

    private SensitivePath(String fieldPath, DesensitizedType desensitizedType) {
        if (fieldPath == null || desensitizedType == null) {
            throw new IllegalArgumentException("fieldPath or desensitizedType can not be null");
        }
        this.fieldPath = fieldPath;
        this.desensitizedType = desensitizedType;
        this.flag = 1;
    }

    private SensitivePath(String fieldPath, Function<String, String> customFunction) {
        if (fieldPath == null || customFunction == null) {
            throw new IllegalArgumentException("fieldPath or customFunction can not be null");
        }
        this.fieldPath = fieldPath;
        this.customFunction = customFunction;
        this.flag = 2;
    }

    // 内置脱敏逻辑
    public static SensitivePath withBuiltInRule(String fieldPath, DesensitizedType desensitizedType) {
        return new SensitivePath(fieldPath, desensitizedType);
    }

    // 自定义脱敏逻辑
    public static SensitivePath withCustomRule(String fieldPath, Function<String, String> customFunction) {
        return new SensitivePath(fieldPath, customFunction);
    }

    public static List<SensitivePath> withBuiltInRuleList(List<String> fieldPath, DesensitizedType desensitizedType) {
        return fieldPath.stream().map(field -> SensitivePath.withBuiltInRule(field, desensitizedType)).collect(Collectors.toList());
    }

    public static List<SensitivePath> withCustomRuleList(List<String> fieldPath, Function<String, String> customFunction) {
        return fieldPath.stream().map(field -> SensitivePath.withCustomRule(field, customFunction)).collect(Collectors.toList());
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public DesensitizedType getDesensitizedType() {
        return desensitizedType;
    }

    public Function<String, String> getCustomFunction() {
        return customFunction;
    }

    public int getFlag() {
        return flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensitivePath that = (SensitivePath) o;
        if (flag != that.flag) return false;
        if (fieldPath != null ? !fieldPath.equals(that.fieldPath) : that.fieldPath != null) return false;
        if (desensitizedType != null ? !desensitizedType.equals(that.desensitizedType) : that.desensitizedType != null)
            return false;
        return customFunction != null ? customFunction.equals(that.customFunction) : that.customFunction == null;
    }

    @Override
    public int hashCode() {
        int result = fieldPath != null ? fieldPath.hashCode() : 0;
        result = 31 * result + (desensitizedType != null ? desensitizedType.hashCode() : 0);
        result = 31 * result + (customFunction != null ? customFunction.hashCode() : 0);
        result = 31 * result + flag;
        return result;
    }

}