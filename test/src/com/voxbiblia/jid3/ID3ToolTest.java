package com.voxbiblia.jid3;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
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
            fail("array length does not match");
        }
        for (int i= 0; i < a.length; i++) {
            if (a[i] != b[i] ) {
                
            }
        }
    }
}
