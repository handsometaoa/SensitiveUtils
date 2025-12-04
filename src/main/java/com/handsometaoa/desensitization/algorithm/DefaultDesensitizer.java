package com.handsometaoa.desensitization.algorithm;

import com.handsometaoa.desensitization.util.MaskUtils;
import com.handsometaoa.desensitization.util.ToolUtils;

import java.util.function.Function;

/**
 * @author handsometaoa
 * @date 2025/12/4 19:46
 */
public class DefaultDesensitizer implements Desensitizer {

    @Override
    public Function<String, String> getDesFunction() {
        return value -> {
            if (ToolUtils.isEmpty(value)) {
                return value;
            }
            int length = value.length();
            if (length <= 3) {
                return MaskUtils.generateRepeatedChar('*', length);
            } else if (length <= 6) {
                return value.charAt(0) + MaskUtils.generateRepeatedChar('*', length - 2) + value.charAt(length - 1);
            } else if (length <= 9) {
                return value.substring(0, 2) + MaskUtils.generateRepeatedChar('*', length - 4) + value.substring(length - 2);
            } else {
                return value.substring(0, 3) + MaskUtils.generateRepeatedChar('*', length - 6) + value.substring(length - 3);
            }
        };
    }

}