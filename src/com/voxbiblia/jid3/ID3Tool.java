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

import java.io.File;

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
     * Converts the given tag instance to an ID3v2.3 byte stream
     * and adds it to the target, replacing any existing tag.
     *
     * @param tag the tag to write
     * @param target the file to write it to
     */
    public static void write(File target, ID3Tag tag)
    {
        throw new Error("not implemented");
    }

    /**
     * Updates the ID3 tag in target with new values from tag. If a property
     * in tag is not set, the corresponding frame is copied from the previous
     * tag.
     *
     * @param target the file to update the ID3 tag in
     * @param tag an ID3Tag instance that contains updated properties
     */
    public static void update(File target, ID3Tag tag)
    {
        throw new Error("not implemented");
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
