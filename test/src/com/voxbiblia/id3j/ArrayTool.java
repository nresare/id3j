package com.voxbiblia.id3j;

import java.io.UnsupportedEncodingException;

/**
 * Convinience methods for arrays.
 */
public class ArrayTool
{
    /**
     * Returns the position of needle in haystack, or -1 if needle is not
     * found in haystack
     *
     * @param needle the array to look for
     * @param haystack the array to look in
     * @return the index of needle in haystack
     */
    public static int indexOf(byte[] needle, byte[] haystack)
    {
        int needleOffset = 0;
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i] == needle[needleOffset]) {
                needleOffset++;
                if (needleOffset >= needle.length ) {
                    return i - needle.length + 1;
                }
            } else {
                if (needleOffset > 0) {
                    i = i - needleOffset;
                }
                needleOffset = 0;

            }
        }
        if (needleOffset == needle.length) {
            return haystack.length - needle.length + 1;
        }
        return -1;

    }

    public static byte[] bs(String ascii)
    {
        try {
            return ascii.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
