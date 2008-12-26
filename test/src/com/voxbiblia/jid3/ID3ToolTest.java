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

    public void testMerge2()
    {
        ID3Tag t = new ID3Tag();
        t.setAlbum("bar");
        t.setTrack("18");
        ID3Tool.merge(readFile("test/data/tag5.bin.gz"), t);
    }


}
