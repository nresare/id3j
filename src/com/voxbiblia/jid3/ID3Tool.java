package com.voxbiblia.jid3;


/**
 * A tool class used to serialize audio metadata for inclusion into an
 * ID3 tag. 
 */
public class ID3Tool
{
    public static byte[] serialize(ID3Tag tag)
    {
        Buffer b = new Buffer();
        b.writeString("ID3");
        b.writeBytes(0x03);
        // we write zero length for now, length field will be updated later
        b.writeZeroes(6);
        writeText(b, "TALB", tag.getAlbum());
        byte[] bs = b.getBytes();
        writeTagSize(bs);
        return bs;
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
        if (size > (2 << 27)) {
            throw new Error("tag is larger than 2**28, too big: "+ size);
        }
        bs[6] = (byte)((size >> 21) & 0x7f);
        bs[7] = (byte)((size >> 14) & 0x7f);
        bs[8] = (byte)((size >> 7) & 0x7f);
        bs[9] = (byte)(size & 0x7f); 
    }

    private static void writeText(Buffer b, String frameId, String value)
    {
        assert(frameId.length() == 4);
        b.writeString(frameId);
        if (needsUnicode(value)) {
            throw new Error("can't handle characters outside of ISO-8859-1 " +
                    "for now");
        }
        b.writeUInt32BE(value.length() + 1);
        b.writeZeroes(3);
        b.writeString(value);
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

}
