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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Checks for the MPEG synchronization bit pattern (11 consecutive bits set
 * to 1) in all files in the directory given as argv[0]
 *
 */
class SyncChecker
{
    public static void main(String[] args)
            throws IOException
    {
        for (File f : new File(args[0]).listFiles()) {
            if (f.getName().endsWith(".jpg")) {
                if (handleFile(f)) {
                    //return;
                }
            }
        }
    }

    private static boolean handleFile(File f)
            throws IOException
    {
        byte[] bs = readFile(f);
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == (byte)0xff) {
                if (bs.length > i) {
                    if ((bs[i + 1] & (byte)0xe0) == (byte)0xe0) {
                        System.out.printf("found sync in '%s' at 0x%02x\n",
                                f.getName(), i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static byte[] readFile(File f)
            throws IOException
    {
        byte[] bs = new byte[(int)f.length()];
        FileInputStream fis = new FileInputStream(f);
        if (fis.read(bs) != bs.length) {
            throw new Error("short read");
        }
        fis.close();
        return bs;
    }
}
