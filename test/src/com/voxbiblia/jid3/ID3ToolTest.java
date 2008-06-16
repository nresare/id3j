package com.voxbiblia.jid3;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Tests ID3Tool
 */
public class ID3ToolTest
    extends TestCase
{
    public void testSerialize()
    {
        ID3Tag t = new ID3Tag();
        t.setAlbum("Omnibus");
        cmp(ID3Tool.serialize(t), readFile("test/data/minimal.bin"));
    }

    public void testNeedsUnicode()
    {
        assertTrue(ID3Tool.needsUnicode("€100"));
        assertFalse(ID3Tool.needsUnicode("falsterbo"));
    }

    public void testWriteSize()
    {
        byte[] b = new byte[267];
        ID3Tool.writeTagSize(b);
        assertEquals(0, b[6]);
        assertEquals(0, b[7]);
        assertEquals(0x02, b[8]);
        assertEquals(0x01, b[9]);
    }

    private byte[] readFile(String filename)
    {
        try {
            File f = new File(filename);
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




    private void cmp(byte[] a, byte[] b)
    {
        if (a == null || b == null) {

            fail("neither a nor b can be null");
        }
        if (a.length != b.length) {
            write(a, "a.out");
            write(b, "b.out");
            fail("array length does not match: " + a.length + " vs " + b.length);
        }
        for (int i= 0; i < a.length; i++) {
            if (a[i] != b[i] ) {
                write(a, "a.out");
                write(b, "b.out");
                fail("mismatch at byte "+ i);
            }
        }
    }

    private void write(byte[] a, String filename)
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
