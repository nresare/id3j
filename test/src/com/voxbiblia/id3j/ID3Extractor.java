/*
    id3j - a library that generates ID3v2 tags
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

    Project web page: http://fs.voxbiblia.com/id3j/
 */
package com.voxbiblia.id3j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A utility that extracts the ID3 tag from an MP3 file, and writes it to
 * a separate file.
 */
public class ID3Extractor
{
    public static void main(String[] args)
            throws IOException
    {
        FileInputStream fis = new FileInputStream(args[0]);
        byte[] buf = new byte[10];
        if (fis.read(buf) < 10) {
            throw new Error("short read from " + args[0]);
        }
        int len = getLength(buf);
        FileOutputStream fos = new FileOutputStream(args[1]);
        fos.write(buf);
        buf = new byte[len];
        if (fis.read(buf) < len) {
            throw new Error("short read from " + args[0]);
        }
        fos.write(buf);
        fos.close();
        fis.close();
    }

    private static int getLength(byte[] tagHeader)
    {
        int i = tagHeader[6] << 21;
        i += tagHeader[7] << 14;
        i += tagHeader[8] << 7;
        return i + tagHeader[9];
    }
}
