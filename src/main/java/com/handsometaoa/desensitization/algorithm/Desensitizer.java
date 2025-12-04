package com.handsometaoa.desensitization.algorithm;

import java.util.function.Function;

/**
 * @author handsometaoa
 * @date 2025/12/4 19:45
 */
public interface Desensitizer {

    Function<String, String> getDesFunction();

}