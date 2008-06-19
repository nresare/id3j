package com.voxbiblia.jid3;

import junit.framework.TestCase;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Tests ID3Serializer
 */
public class ID3SerializerTest
    extends TestCase
{
    public void testSerialize()
    {
        ID3Tag t = new ID3Tag();
        t.setAlbum("Omnibus");
        ID3Serializer s = new ID3Serializer();
        cmp(readFile("test/data/minimal.bin"), s.serialize(t));
    }

    public void testSerialize1()
    {
        ID3Tag t = new ID3Tag();
        t.setArtist("Leffe");
        t.setAlbum("Yngve ”steen“ Nilsson");
        t.setTrack("8");
        ID3Serializer s = new ID3Serializer();
        cmp(readFile("test/data/tag1.bin"), s.serialize(t));
    }

    public void testSerialize2()
    {
        ID3Tag t = new ID3Tag();
        t.setArtist("greger");
        t.setTitle("stolpe");
        ID3Serializer s = new ID3Serializer();
        s.setPadCount(1500);
        cmp(readFile("test/data/tag2.bin.gz"), s.serialize(t));
    }

    public void testSerialize3()
    {
        ID3Tag t = new ID3Tag();
        t.setArtist("Bibel 2000");
        t.setAlbum("40 Matteusevangeliet");
        t.setTrack("1/28");
        t.setPicture(readFile("test/data/mt.png"));
        t.setTitle("Matt 01 Jesu släkttavla, Jesu födelse");
        t.setGenre("Nya Testamentet");
        t.setComment("www.voxbiblia.se\nInläsare: Mats Sundman");
        t.setLyrics(readTextFile("test/data/lyrics.txt"));
        ID3Serializer s = new ID3Serializer();
        cmp(readFile("test/data/tag3.bin"), s.serialize(t));
    }

    public void testSerializeUnicodeLyrics()
    {
        ID3Tag t = new ID3Tag();
        t.setArtist("Bibel 2000");
        t.setAlbum("40 Matteusevangeliet");
        t.setTrack("1/28");
        t.setTitle("Matt 01 Jesu släkttavla, Jesu födelse");
            t.setGenre("Nya Testamentet");
        t.setComment("www.voxbiblia.se\nInläsare: Mats Sundman");
        t.setLyrics("€299");
        ID3Serializer s = new ID3Serializer();
        cmp(readFile("test/data/tag4.bin"), s.serialize(t));



    }

    public void testNeedsUnicode()
    {
        assertTrue(ID3Serializer.needsUnicode("€100"));
        assertFalse(ID3Serializer.needsUnicode("falsterbo"));
    }

    public void testWriteSize()
    {
        byte[] b = new byte[267];
        ID3Serializer.writeTagSize(b);
        assertEquals(0, b[6]);
        assertEquals(0, b[7]);
        assertEquals(0x02, b[8]);
        assertEquals(0x01, b[9]);
    }

    private String readTextFile(String filename)
    {
        try {
            FileInputStream fis = new FileInputStream(filename);
            Reader fr = new InputStreamReader(fis, "UTF-8");
            CharArrayWriter w = new CharArrayWriter();
            char[] buf = new char[8192];
            int read = fr.read(buf);
            while (read > 0 ) {
                w.write(buf, 0, read);
                read = fr.read(buf);
            }
            fis.close();
            return w.toString();
        }   catch (IOException e) {
            throw new Error(e);
        }
    }

    private byte[] readFile(String filename)
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

    private void cmp(byte[] a, byte[] b)
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
