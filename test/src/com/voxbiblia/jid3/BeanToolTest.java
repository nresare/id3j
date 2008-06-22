package com.voxbiblia.jid3;

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

    public class Bean
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
    }

    public void testGetProperties()
    {
        Set<String> props = BeanTool.getProperties(new Bean());
        assertEquals(3, props.size());
        assertTrue(props.contains("foo"));
        assertTrue(props.contains("a"));
        assertTrue(props.contains("b"));
    }

}
