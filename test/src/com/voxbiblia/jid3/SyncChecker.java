package com.voxbiblia.jid3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Checks for the MPEG synchronization bit pattern (11 consecutive bits set
 * to 1) in all files in the directory given as argv[0]
 *
 */
class SyncChecker
{
    public static void main(String[] args)
            throws IOException
    {
        for (File f : new File(args[0]).listFiles()) {
            if (f.getName().endsWith(".jpg")) {
                if (handleFile(f)) {
                    //return;
                }
            }
        }
    }

    private static boolean handleFile(File f)
            throws IOException
    {
        byte[] bs = readFile(f);
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == (byte)0xff) {
                if (bs.length > i) {
                    if ((bs[i + 1] & (byte)0xe0) == (byte)0xe0) {
                        System.out.printf("found sync in '%s' at 0x%02x\n",
                                f.getName(), i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static byte[] readFile(File f)
            throws IOException
    {
        byte[] bs = new byte[(int)f.length()];
        FileInputStream fis = new FileInputStream(f);
        if (fis.read(bs) != bs.length) {
            throw new Error("short read");
        }
        fis.close();
        return bs;
    }
}
