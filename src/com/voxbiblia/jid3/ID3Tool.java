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
}
