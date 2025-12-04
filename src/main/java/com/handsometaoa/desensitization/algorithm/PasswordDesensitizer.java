
package com.handsometaoa.desensitization.algorithm;

import com.handsometaoa.desensitization.util.MaskUtils;
import com.handsometaoa.desensitization.util.ToolUtils;

import java.util.function.Function;

/**
 * @author handsometaoa
 * @date 2025/12/4 19:47
 */
public class PasswordDesensitizer implements Desensitizer {
    @Override
    public Function<String, String> getDesFunction() {
        return value -> {
            if (ToolUtils.isEmpty(value)) {
                return value;
            }
            return MaskUtils.generateRepeatedChar('*', value.length());
        };
    }
}
