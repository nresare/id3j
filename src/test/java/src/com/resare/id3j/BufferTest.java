package com.resare.id3j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Testing Buffer
 */
public class BufferTest
    extends TestBase
{
    public void testIOEPropagation()
    {
        Buffer b = new Buffer(new FailingBAOS());
        try {
            b.writeString("gustav");
            fail("Expecting Error, writing");
        } catch (Error e) {
            // pass
        }

        try {
            b.writeUInt32BE(42);
            fail("Expecting Error, writing");
        } catch (Error e) {
            // pass
        }
    }

    public void testSize() {
        Buffer b = new Buffer();
        b.writeZeroes(8);
        assertEquals(8, b.getSize());
    }

    static class FailingBAOS extends ByteArrayOutputStream {
        @Override
        public void write(byte[] bytes) throws IOException {
            throw new IOException("always fails");
        }
    }
}
