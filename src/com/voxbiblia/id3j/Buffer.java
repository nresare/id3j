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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Convinient methods to write different types of data to a byte buffer.
 */
class Buffer
{
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    /**
     * Writes a number of zero bytes to the buffer
     *
     * @param count the number of zero bytes to write
     */
    public void writeZeroes(int count)
    {
        for (int i = 0; i < count; i++) {
            baos.write(0);
        }
    }

    /**
     * Writes the given string to the byte buffer in the ISO-8859-1
     * character encoding.
     *
     * @param s the string to write
     */
    public void writeString(String s)
    {
        try {
            baos.write(s.getBytes("ISO-8859-1"));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public void writeBytes(int... bytes)
    {
        for (int b : bytes) {
            assert(b < 0x100);
            baos.write(b);
        }
    }

    public void writeBytes(byte... bytes)
    {
        for (byte b : bytes) {
            baos.write(b);
        }
        
    }

    public void writeBytes(byte[] bytes, int offset, int length)
    {
        baos.write(bytes, offset, length);
    }


    public void writeUInt32BE(int i)
    {
        byte[] tmpBuf = new byte[4];
        tmpBuf[0] = (byte)(i >> 24);
        tmpBuf[1] = (byte)((i >> 16) & 0xff);
        tmpBuf[2] = (byte)((i >> 8) & 0xff);
        tmpBuf[3] = (byte)(i & 0xff);
        try {
            baos.write(tmpBuf);
        } catch (IOException e) {
            throw new Error(e);
        }

    }

    public int getSize()
    {
        return baos.toByteArray().length;
    }

    public byte[] getBytes()
    {
        return baos.toByteArray();
    }
}
