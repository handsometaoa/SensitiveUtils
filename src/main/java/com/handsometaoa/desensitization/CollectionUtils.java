package com.handsometaoa.desensitization;

import java.util.Collection;

@Deprecated
public class CollectionUtils {
    public static boolean isEmpty(Collection<String> collection) {
        return collection == null || collection.isEmpty();
    }
}
