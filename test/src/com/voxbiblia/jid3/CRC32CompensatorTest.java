package com.voxbiblia.jid3;

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
