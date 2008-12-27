/*
    jid3 - a library that generates ID3v2 tags
    Copyright (C) 2008  Noa Resare (noa@voxbiblia.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Project web page: http://fs.voxbiblia.com/jid3/
 */
package com.voxbiblia.jid3;

import java.io.*;
import java.util.Map;
import java.util.zip.CRC32;

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

    public void testGetOffsetsShortPadding()
    {
        Map<String, Integer> offsets = ID3Serializer.getOffsets(readFile("test/data/tag6.bin"));
        assertEquals(3, offsets.size());
        assertEquals((Integer)0x0a, offsets.get("TRCK"));
        assertEquals((Integer)0x16, offsets.get("TALB"));
        assertEquals((Integer)0x4d, offsets.get("TPE1"));
    }

    public void testGetOffsetsNormalPadding()
    {
        Map<String, Integer> offsets = ID3Serializer.getOffsets(readFile("test/data/tag7.bin"));
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

    public void testReadUInt32BE()
    {
        byte[] bs = new byte[] {(byte)0x00, (byte)0x00, (byte)0xfe, (byte)0xd8};
        assertEquals(65240, ID3Serializer.readUInt32BE(bs, 0));
    }

    public void testPropagateUnknownTag()
    {
        byte[] oldTag = readFile("test/data/wild.bin.gz");
        ID3Tag t = new ID3Tag();
        t.setTitle("meep");
        byte[] newTag = new ID3Serializer().serialize(t, oldTag);
        assertTrue("Could not find TYER frame",
                ArrayTool.indexOf(ArrayTool.bs("TYER"), newTag) > -1);
        write(newTag, "tag.bin");
    }

    public void testCompensateCRC32()
    {
        CRC32 c = new CRC32();
        ID3Serializer s = new ID3Serializer();
        ID3Tag t = new ID3Tag();
        t.setArtist("greger");
        byte[] tag = s.serialize(t, null, true);
        c.update(tag);
        byte[] mp3 = readFile("test/data/short.mp3");
        c.update(mp3);

        CRC32 ref = new CRC32();
        ref.update(mp3);
        assertEquals(ref.getValue(), c.getValue());
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
