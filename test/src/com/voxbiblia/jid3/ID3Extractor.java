package com.voxbiblia.jid3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A utility that extracts the ID3 tag from an MP3 file, and writes it to
 * a separate file.
 */
public class ID3Extractor
{
    public static void main(String[] args)
            throws IOException
    {
        FileInputStream fis = new FileInputStream(args[0]);
        byte[] buf = new byte[10];
        if (fis.read(buf) < 10) {
            throw new Error("short read from " + args[0]);
        }
        int len = getLength(buf);
        FileOutputStream fos = new FileOutputStream(args[1]);
        fos.write(buf);
        buf = new byte[len];
        if (fis.read(buf) < len) {
            throw new Error("short read from " + args[0]);
        }
        fos.write(buf);
        fos.close();
        fis.close();
    }

    private static int getLength(byte[] tagHeader)
    {
        int i = tagHeader[6] << 21;
        i += tagHeader[7] << 14;
        i += tagHeader[8] << 7;
        return i + tagHeader[9];
    }
}
