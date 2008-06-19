package com.voxbiblia.jid3;

import java.io.UnsupportedEncodingException;

/**
 * Serializes ID3 data into a tag byte stream.
 */
public class ID3Serializer
{
    private int padCount = 0;

    public byte[] serialize(ID3Tag tag)
    {
        Buffer b = new Buffer();
        b.writeString("ID3");
        b.writeBytes(0x03);
        // we write zero length for now, length field will be updated later
        b.writeZeroes(6);
        writeText(b, "TRCK", tag.getTrack());
        writePicture(b, tag.getPicture());
        writeText(b, "TALB", tag.getAlbum());
        writeTextLang(b, "COMM", "eng", tag.getComment());
        writeText(b, "TPE1", tag.getArtist());
        writeText(b, "TIT2", tag.getTitle());
        writeTextLang(b, "USLT", "eng", tag.getLyrics());

        if (padCount > 0) {
            b.writeZeroes(padCount);
        }

        byte[] bs = b.getBytes();
        writeTagSize(bs);
        return bs;
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
            // two bytes per char + bom (2) + flags (2) + encoding indicator (1)
            int len = (value.length() * 2) + 5;
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
