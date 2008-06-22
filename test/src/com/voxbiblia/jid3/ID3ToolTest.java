package com.voxbiblia.jid3;

/**
 * Tests ID3Tool
 */
public class ID3ToolTest
    extends TestBase
{
    public void testMerge()
    {
        byte[] b = readFile("test/data/tag4.bin");
        ID3Tag t = new ID3Tag();
        t.setTitle("Greeger");
        byte[] another = ID3Tool.merge(b, t);
        cmp(readFile("test/data/tag4_modified_title.bin"), another);
    }
}
