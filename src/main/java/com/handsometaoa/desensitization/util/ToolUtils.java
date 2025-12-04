
package com.handsometaoa.desensitization.util;

import java.util.Collection;

/**
 * @author handsometaoa
 * @date 2025/12/4 18:19
 */
public class ToolUtils {


    public static boolean isEmpty(Collection<String> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


}
