package com.voxbiblia.jid3;

import java.io.UnsupportedEncodingException;

/**
 * Serializes ID3 data into a tag byte stream.
 */
public class ID3Serializer
{
    private boolean alwaysEndWithNull = false;

    public byte[] serialize(ID3Tag tag)
       {
           Buffer b = new Buffer();
           b.writeString("ID3");
           b.writeBytes(0x03);
           // we write zero length for now, length field will be updated later
           b.writeZeroes(6);
           writeText(b, "TPE1", tag.getArtist());
           writeText(b, "TALB", tag.getAlbum());
           writeText(b, "TRCK", tag.getTrack());

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
           if (size > (1 << 28)) {
               throw new Error("tag is larger than 2**28, too big: "+ size);
           }
           bs[6] = (byte)((size >> 21) & 0x7f);
           bs[7] = (byte)((size >> 14) & 0x7f);
           bs[8] = (byte)((size >> 7) & 0x7f);
           bs[9] = (byte)(size & 0x7f);
       }

       void writeText(Buffer b, String frameId, String value)
       {
           if (value == null || value.length() == 0) {
               return;
           }
           assert(frameId.length() == 4);
           b.writeString(frameId);
           if (needsUnicode(value)) {
               int len = (value.length() * 2) + 3;
               if (alwaysEndWithNull) {
                   len += 2;
               }
               b.writeUInt32BE(len);
               b.writeBytes(0x00,0x00,0x01,0xff,0xfe);
               byte[] bs;
               try {
                   bs = value.getBytes("UTF-16LE");
               } catch (UnsupportedEncodingException e) {
                   throw new Error("Your platform doesn't support UTF-16LE");
               }
               b.writeBytes(bs);
               if (alwaysEndWithNull) {
                   b.writeZeroes(2);
               }
           } else {
               int len = value.length() + 1;
               if (alwaysEndWithNull) {
                   len++;
               }
               b.writeUInt32BE(len);
               b.writeZeroes(3);
               b.writeString(value);
               if (alwaysEndWithNull) {
                   b.writeZeroes(1);
               }
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


    public void setAlwaysEndWithNull(boolean alwaysEndWithNull)
    {
        this.alwaysEndWithNull = alwaysEndWithNull;
    }
}
