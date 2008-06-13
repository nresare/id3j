package com.voxbiblia.jid3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A tool class used to serialize audio metadata for inclusion into an
 * ID3 tag. 
 */
public class ID3Tool
{
    public static byte[] serialize(ID3Tag tag)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeString(baos, "ID3");
        writeBytes(baos, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00);
        return baos.toByteArray();
    }

    private static void writeString(OutputStream out, String s)
    {
        try {
            out.write(s.getBytes("US-ASCII"));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private static void writeBytes(OutputStream out, int... bytes)
    {
        try {
            for (int b : bytes) {
                assert(b < 0x100);
                out.write((byte)b);
            }
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
