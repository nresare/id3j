package com.voxbiblia.jid3;

import junit.framework.TestCase;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Abstract base class for the tests, with a convinience method or two.
 *
 * 
 */
public abstract class TestBase
    extends TestCase
{
    protected byte[] readFile(String filename)
    {
        try {
            File f = new File(filename);
            if (filename.endsWith(".gz")) {
                FileInputStream fis = new FileInputStream(filename);
                GZIPInputStream zis = new GZIPInputStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int read = zis.read(buffer);
                while (read > 0) {
                    baos.write(buffer, 0, read);
                    read = zis.read(buffer);
                }
                fis.close();
                return baos.toByteArray();
            }
            FileInputStream fis = new FileInputStream(f);
            int len = (int)f.length();
            byte[] bs = new byte[(int)f.length()];
            if (fis.read(bs) != len) {
                throw new IOException("short read");
            }
            fis.close();
            return bs;
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    protected void cmp(byte[] a, byte[] b)
    {
        if (a == null || b == null) {

            fail("neither a nor b can be null");
        }
        if (a.length != b.length) {
            write(a, "reference.out");
            write(b, "candidate.out");
            fail("array length does not match: " + a.length + " vs " + b.length);
        }
        for (int i= 0; i < a.length; i++) {
            if (a[i] != b[i] ) {
                write(a, "reference.out");
                write(b, "candidate.out");
                fail("mismatch at byte 0x"+ Integer.toHexString(i));
            }
        }
    }

    protected void write(byte[] a, String filename)
    {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(a);
            fos.close();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
