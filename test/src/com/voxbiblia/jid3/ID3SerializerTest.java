package com.voxbiblia.jid3;

import java.io.*;
import java.util.Map;

/**
 * Tests ID3Serializer
 */
public class ID3SerializerTest
    extends TestBase
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

    public void testGetOffsets()
    {
        Map<String, Integer> offsets = ID3Serializer.getOffsets(readFile("test/data/tag4.bin"));
        assertEquals(6, offsets.size());
        assertEquals((Integer)0x0a, offsets.get("TRCK"));
        assertEquals((Integer)0x19, offsets.get("TALB"));
        assertEquals((Integer)0x38, offsets.get("COMM"));
        assertEquals((Integer)0x6e, offsets.get("TPE1"));
        assertEquals((Integer)0xb3, offsets.get("USLT"));
    }

    public void testGetOffsets2()
    {
        Map<String, Integer> offsets = ID3Serializer.getOffsets(readFile("test/data/tag1.bin"));
        assertEquals(3, offsets.size());
        assertEquals((Integer)0x0a, offsets.get("TRCK"));
        assertEquals((Integer)0x16, offsets.get("TALB"));
        assertEquals((Integer)0x4d, offsets.get("TPE1"));
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

}
