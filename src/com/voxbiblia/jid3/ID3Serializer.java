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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * Serializes music file metadata data into a ID3 version 2.3.0 tag byte stream
 * that can be prepended to an MP3 file.
 */
public class ID3Serializer
{
    private int padCount = 0;

    private static Map<String, String> properties = new HashMap<String, String>();

    static {
        properties.put("TRCK", "track");
        properties.put("APIC", "picture");
        properties.put("TALB", "album");
        properties.put("COMM", "comment");
        properties.put("TPE1", "artist");
        properties.put("TIT2", "title");
        properties.put("USLT", "lyrics");
    }
    
    /**
     * Serializes the data in the given <tt>tag</tt> into the returned byte
     * array.
     *
     * @param tag the tag
     * @return a byte array with the tags serialized
     */
    public byte[] serialize(ID3Tag tag)
    {
        return serialize(tag, null);
    }

    /**
     * Copies the frames from the tag previous, replacing any frames which are
     * set in tag.
     * @param tag the data to write to the tag
     * @param previous a previously serialized tag to extract default values from
     * @return a serialized ID3-tag
     */
    public byte[] serialize(ID3Tag tag, byte[] previous)
    {
        return serialize(tag, previous, false);
    }

    /**
     * Copies the frames from the tag previous, replacing any frames which are
     * set in tag. If compensateCrc is set to true the resulting tag contains
     * data that makes it's CRC32 checksum match that of a zero-size byte array.
     *
     * @param tag the data to write to the tag
     * @param previous a previously serialized tag to extract default values from
     * @param compensateCrc if set to true the serialized tag is CRC32-neutral.
     * @return a serialized ID3-tag
     */
    public byte[] serialize(ID3Tag tag, byte[] previous, boolean compensateCrc)
    {
        Buffer b = new Buffer();
        b.writeString("ID3");
        b.writeBytes(0x03);
        // we write zero length for now, length field will be updated later
        b.writeZeroes(6);

        Map<String, Boolean> used = new HashMap<String, Boolean>();
        Map<String, Integer> offsets = getOffsets(previous);

        for (String frameId : offsets.keySet()) {
            if (Boolean.TRUE.equals(used.get(frameId))) {
                continue;
            }
            boolean u = handleFrame(b, frameId, properties.get(frameId),
                    tag, offsets, previous);
            if (u) {
                used.put(frameId, true);
            }
        }

        for (String frameId : properties.keySet()) {
            if (Boolean.TRUE.equals(used.get(frameId))) {
                continue;
            }
            handleFrame(b, frameId, properties.get(frameId),
                    tag, null, null);
        }

        if (padCount > 0) {
            if (compensateCrc) {
                throw new IllegalStateException("CRC compensation and padding" +
                        " can not both be added, don't call setPadCount()");
            }
            b.writeZeroes(padCount);
        }

        if (compensateCrc) {
            writeCrcCompensationFrame(b);
        }

        byte[] bs = b.getBytes();
        writeTagSize(bs);
        if (compensateCrc) {
            fillInCompensationFrame(bs);
        }
        return bs;
    }

    private static final String COMP_FRAME_ID = "http://resare.com/crc32/tag";

    /**
     * Writes a blank CRC compensation tag of the right size.
     * @param buffer the buffer to write the data to
     */
    private void writeCrcCompensationFrame(Buffer buffer)
    {
        buffer.writeString("PRIV");
        buffer.writeUInt32BE(COMP_FRAME_ID.length() + 5);
        buffer.writeString(COMP_FRAME_ID);
        buffer.writeZeroes(5);
    }

    /**
     * Replaces the four last bytes of the given byte array with the CRC32
     * compensation value that makes the wole byte array crc neutral.
     *
     * @param bytes the bytes to checksum and update the tail of.
     */
    private void fillInCompensationFrame(byte[] bytes)
    {
        CRC32 c = new CRC32();
        c.update(bytes, 0, bytes.length - 4);
        byte[] update = CRC32Compensator.compensate((int)c.getValue(), 0);
        System.arraycopy(update, 0, bytes, bytes.length - 4, 4);
    }

    // returns true if a value from tag was used, else false
    private boolean handleFrame(Buffer b, String frameId, String property,
                             ID3Tag t, Map<String, Integer> offsets,
                             byte[] previous)
    {
        Object tagProperty = null;
        if (property != null) {
            tagProperty = BeanTool.getProperty(t, property);
        }
        if (tagProperty == null) {
            if (offsets != null && offsets.containsKey(frameId)) {
                copyFrame(b, previous, offsets.get(frameId));
            }
            return false;
        }

        if (frameId.equals("APIC")) {
            writePicture(b, (byte[])tagProperty);
        } else if (frameId.equals("COMM") || frameId.equals("USLT")) {
            writeTextLang(b, frameId, "eng", (String)tagProperty);
        } else {
            writeText(b, frameId, (String)tagProperty);
        }
        return true;
    }

    private void copyFrame(Buffer b, byte[] previous, int offset)
    {
        int len = readUInt32BE(previous, offset + 4);
        if (previous.length < offset + len + 10) {
            throw new Error(String.format("buffer length (0x%x bytes) is " +
                    "shorter than 0x%x",previous.length, (offset + len + 10)));
        }
        b.writeBytes(previous, offset, len + 10);
    }


    /**
     * Returns a Map of frame ids and offsets in the given tag.
     *
     * @param tag the tag to extract names and offset from
     * @return a map of ids and offsets 
     */
    static Map<String, Integer> getOffsets(byte[] tag)
    {
        Map<String, Integer> m = new HashMap<String, Integer>();
        if (tag == null) {
            return m;
        }
        try {
            for (int i = 10; i < tag.length;) {
                String id;
                if (i > tag.length - 4) {
                    return m;
                }
                if (tag[i] == 0 && tag[i+1] == 0 && tag[i+2] == 0
                        && tag[i+3] == 0) {
                    return m;
                }
                id = new String(tag, i, 4, "US-ASCII");
                m.put(id, i);
                i += 4;
                i += readUInt32BE(tag, i) + 6;
            }
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        return m;
    }


    /**
     *
     * Reads an unsigned big endian 32 bit integer and returns it. Since
     * the java int is signed, it can't handles values higher than 2**31.
     * Providing such values will result in an IllegalArgumentException.
     *
     * @param in the InputStream to read four bytes from.
     * @param offset the offset into the byte buffer to read the int at
     * @return the integer
     * @throws IllegalArgumentException if the given int is larger than 2**31.
     */
    static int readUInt32BE(byte[] in, int offset)
            throws IllegalArgumentException
    {
        if (in.length < offset + 4) {
            throw new IllegalArgumentException(
                    String.format("can't read from in with size %d offset %d",
                            in.length, offset));
        }
        if (in[offset] < 0) {
            throw new IllegalArgumentException(String.format("too large: %d",
                    in[offset]));
        }
        int i = in[offset] << 24;
        i += pos(in[1 + offset]) << 16;
        i += pos(in[2 + offset]) << 8;
        i += pos(in[3 + offset]);
        return i;
    }

    /**
     * Convert the signed byte to an unsigned number between 0x0 and 0xff.
     * @param b the byte to convert
     * @return the integer containing the positive value
     */
    private static int pos(byte b)
    {
        return b < 0 ? b + 0x100 : b;
    }

    private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
    private static final int IMAGE_TYPE = 0x03;

    private void writePicture(Buffer b, byte[] picture)
    {
        if (picture == null) {
            return;
        }

        String ct = DEFAULT_CONTENT_TYPE;
        if (picture[1] == 'P' &&  picture[2] == 'N' && picture[3] == 'G') {
            ct = "image/png";
        }

        b.writeString("APIC");
        int size = picture.length + ct.length() + 4;
        b.writeUInt32BE(size);
        b.writeZeroes(3);
        b.writeString(ct);
        b.writeBytes(0x00, IMAGE_TYPE, 0x00);
        b.writeBytes(picture);
    }

    /**
     * Writes the size of the tag minus header (10 bytes) into 4 bytes
     * at offset 6 using using only the 7 lower bits in each byte.
     *
     * @param bs the buffer.
     */
    static void writeTagSize(byte[] bs)
    {
        int size = bs.length - 10;
        if (size > (1 << 28)) {
            throw new Error("tag is larger than 2**28, too big: "+ size);
        }
        bs[6] = (byte)((size >> 21) & 0x7f);
        bs[7] = (byte)((size >> 14) & 0x7f);
        bs[8] = (byte)((size >> 7) & 0x7f);
        bs[9] = (byte)(size & 0x7f);
    }

    /**
     * Writes a regular "Text information frame" (ID3v2.3.0 4.2.1) with the
     * specified frame id to the buffer
     *
     * @param b the buffer to append the frame data to
     * @param frameId the four letter ID of the frame
     * @param value the text value
     */
    void writeText(Buffer b, String frameId, String value)
    {
        writeTextLang(b, frameId, null, value);
    }

    /**
     * Writes a text frame with language code (the format specified for frames
     * USLT and COMM). If lang is null, a regular text frame without the
     * language tag and "short content description" (See ID3v2.3.0 4.11)
     *
     * @param b the buffer to append the frame data to
     * @param frameId the four letter frame ID
     * @param lang the language tag
     * @param value the text value
     */
    void writeTextLang(Buffer b, String frameId, String lang, String value)
    {
        if (value == null || value.length() == 0) {
            return;
        }
        assert (frameId.length() == 4);
        b.writeString(frameId);
        if (needsUnicode(value)) {
            // two bytes per char + bom (2) + encoding indicator (1)
            int len = (value.length() * 2) + 3;
            if (lang != null) {
                assert(lang.length() == 3);
                // lang (3) + bom (2) + null (2)
                len += 7;
            }
            b.writeUInt32BE(len);
            // frame flags and string encoding
            b.writeBytes(0x00, 0x00, 0x01);
            if (lang != null) {
                b.writeString(lang);
                b.writeBytes(0xff, 0xfe, 0x00, 0x00);
            }
            // Byte Order Mark for Little Endian UTF-16
            b.writeBytes(0xff, 0xfe);
            byte[] bs;
            try {
                bs = value.getBytes("UTF-16LE");
            } catch (UnsupportedEncodingException e) {
                throw new Error("Your platform doesn't support UTF-16LE");
            }
            b.writeBytes(bs);
        } else {
            int len = value.length() + 1;
            if (lang != null) {
                assert(lang.length() == 3);
                len += 4;
            }
            b.writeUInt32BE(len);
            // frame flags and string encoding
            b.writeZeroes(3);
            if (lang != null) {
                b.writeString(lang);
                b.writeZeroes(1);
            }
            b.writeString(value);
        }
    }

    static boolean needsUnicode(String s)
    {
        for (char c : s.toCharArray()) {
            if (c > 0xff) {
                return true;
            }
        }
        return false;
    }

    /**
     * Configures this serializer to add <tt>padCount</tt> null bytes at the
     * end of the tag.
     *
     * @param padCount the number of padding bytes to add to written tags
     */
    public void setPadCount(int padCount)
    {
        this.padCount = padCount;
    }
}
