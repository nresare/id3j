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

import java.util.Set;

/**
 * Tests BeanTool
 */
public class BeanToolTest
    extends TestBase
{
    public void testGetProperty()
    {
        ID3Tag t = new ID3Tag();
        t.setTitle("foobar");
        assertEquals("foobar", BeanTool.getProperty(t, "title"));
    }

    public void testGetNonexistant()
    {
        try {
            BeanTool.getProperty(new Object(), "something");
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException e) {
            // pass
        }

        try {
            BeanTool.getProperty(new Bean(), "c");
            fail("Should have thrown Error");
        } catch (Error e) {
            //
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class Bean
    {
        private String a, b, foo;

        public String getA()
        {
            return a;
        }

        public String getB()
        {
            return b;
        }

        public String getFoo()
        {
            return foo;
        }

        public void setA(String a)
        {
            this.a = a;
        }

        public void setB(String b)
        {
            this.b = b;
        }

        public void setFoo(String foo)
        {
            this.foo = foo;
        }

        public static String getC() { throw new RuntimeException(); }
    }

    public void testGetProperties()
    {
        Set<String> props = BeanTool.getProperties(new Bean());
        assertEquals(4, props.size());
        assertTrue(props.contains("foo"));
        assertTrue(props.contains("a"));
        assertTrue(props.contains("b"));
    }

    public void testCreateSingleton()
    {
        new BeanTool();
        new BeanTool.BeanProxy(String.class);
    }
}
