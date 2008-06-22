package com.voxbiblia.jid3;

/**
 * A tool class used to serialize audio metadata for inclusion into an
 * ID3 tag. 
 */
public class ID3Tool
{
    /**
     * Reads a serialized ID3 tag from <tt>existing</tt> and updates
     * all frames that is set in the tag <tt>updatedValues</tt>.
     *
     * @param existing the incoming ID3v23 byte stream
     * @param updatedValues the ID3Tag to read new values from
     * @return an updated byte stream
     */
    public static byte[] merge(byte[] existing, ID3Tag updatedValues)
    {
        ID3Serializer s = new ID3Serializer();
        return s.serialize(updatedValues, existing);
    }

    /**
     * Parses the tag header (the first 10 bytes of the ID3v2 tag) and
     * returns the length of the tag in bytes, excluding the header.
     *
     * @param tagHeader the ten first bytes of the tag
     * @return the total length of the ID3v2 tag excluding the header.
     */
    public static int getTagLength(byte[] tagHeader)
    {
        if (tagHeader[0] != 'I' || tagHeader[1] != 'D' || tagHeader[2] != '3') {
             throw new Error("Mismatch at the beginning of ID3 tag.");
         }
        int i = tagHeader[6] << 21;
        i += tagHeader[7] << 14;
        i += tagHeader[8] << 7;
        return i + tagHeader[9];
    }

}
