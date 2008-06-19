package com.voxbiblia.jid3;

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
