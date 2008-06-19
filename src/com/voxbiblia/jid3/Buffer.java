package com.voxbiblia.jid3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Convinient methods to write different types of data to a byte buffer.
 */
class Buffer
{
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public void writeZeroes(int count)
    {
        for (int i = 0; i < count; i++) {
            baos.write((byte)0);
        }
    }

    public void writeString(String s, boolean bugCompatibleNewlines)
    {
        try {
            byte[] b = s.getBytes("ISO-8859-1");
            if (bugCompatibleNewlines) {
                for (int i = 0; i < b.length; i++) {
                    if (b[i] == 0x0a) {
                        b[i] = 0x0d;
                    }
                }
            }
            baos.write(b);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public void writeString(String s)
    {
        writeString(s, false);
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
