package com.ctemplar.app.fdroid.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtils {
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[]... arrays) {
        if (arrays.length == 0) {
            return (T[]) Array.newInstance(arrays.getClass(), 0);
        }
        int lengthSum = 0;
        for (T[] array : arrays) {
            lengthSum += array.length;
        }
        Object[] result = Arrays.copyOf(arrays[0], lengthSum);
        int currentArrayLength = arrays[0].length;

        for (int i = 1; i < arrays.length; ++i) {
            Object[] currentArray;
            System.arraycopy(currentArray = arrays[i], 0, result, currentArrayLength, currentArray.length);
            currentArrayLength += currentArray.length;
        }
        return (T[]) result;
    }
}
