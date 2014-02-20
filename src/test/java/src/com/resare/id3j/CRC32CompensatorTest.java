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
package com.resare.id3j;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Tests CRC32Compensator.
 */
public class CRC32CompensatorTest
    extends TestCase
{



    public static long getCRC32(byte[]... data)
    {
        // initial value, all bits set
        int crc = -1;
        for (byte[] inner : data) {
            for (byte b : inner) {
                crc = (crc >>> 8) ^ CRC32Compensator.table[(crc ^ b) & 0xff];
            }
        }
        // flip bits
        return ~crc;
    }

    public void testCompensateFail() {
        byte[] data = "greger".getBytes();
        //noinspection PrimitiveArrayArgumentToVariableArgMethod
        long someChecksum = getCRC32(data);
        try {
            CRC32Compensator.compensate((int)someChecksum, -0x2e);
            fail("Should have thrown error, no crc32 can have lowest byte -0x2e");
        } catch (Error e) {
            // pass
        }


    }

    public void testCompensate()
            throws Exception
    {
        byte[] data = "greger".getBytes("US-ASCII");
        //noinspection PrimitiveArrayArgumentToVariableArgMethod
        long targetChecksum = getCRC32(data);

        //noinspection PrimitiveArrayArgumentToVariableArgMethod
        long intermediateChecksum = getCRC32(data);

        byte[] compensation = CRC32Compensator.compensate((int)intermediateChecksum,
                (int)targetChecksum);

        assertEquals(targetChecksum, getCRC32(data, compensation));
    }

    // just to verify my understanding of operator precedence
    public void testIntToBytes()
    {
        int i = 0xfffffffe;
        assertTrue(i < 0);
        assertEquals(0xff, i >>> 24);
        assertEquals(0xff, i >>> 16 & 0xff);
        assertEquals(0xff, i >>>  8 & 0xff);
        assertEquals(0xfe, i & 0xff);
    }

    public void testTableOffsets()
    {
        doTestTableOffsets(0);
        doTestTableOffsets(0xffffffff);
        doTestTableOffsets(0xdeadbeef);
    }

    private void doTestTableOffsets(int checksum)
    {
        int[] offsets = CRC32Compensator.tableOffsets(checksum);
        int i = 0;
        i = i >>> 8 ^ CRC32Compensator.table[offsets[0]];
        i = i >>> 8 ^ CRC32Compensator.table[offsets[1]];
        i = i >>> 8 ^ CRC32Compensator.table[offsets[2]];
        i = i >>> 8 ^ CRC32Compensator.table[offsets[3]];
        try {
            assertEquals(checksum, i);
        } catch (AssertionFailedError e) {
            System.out.println("expected: 0x" + Integer.toHexString(checksum));
            System.out.println("actual: 0x" + Integer.toHexString(i));
            throw e;
        }
    }


}
