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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Access properties of a bean style java object 
 */
class BeanTool
{
    static Map<Class, BeanProxy> proxies = new HashMap<Class, BeanProxy>();

    static class BeanProxy
    {
        private Map<String, Method> getters;
        private String className;

        public BeanProxy(Class c) {
            getters = new HashMap<String, Method>();
            this.className = c.getName();
            for (Method m : c.getMethods()) {
                if (m.getName().startsWith("get")
                        && m.getParameterTypes().length == 0
                        && !m.getName().equals("getClass")) {
                    String s = Character.toLowerCase(m.getName().charAt(3))
                            + m.getName().substring(4);
                    getters.put(s, m);
                }
            }
        }

        public Object getProperty(Object instance, String name)
        {
            Method m = getters.get(name);
            if (m == null) {
                throw new RuntimeException("Class " + className + " does not " +
                        "have a property named " + name);
            }
            try {
                return m.invoke(instance);
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        public Set<String> getProperties()
        {
            return getters.keySet(); 
        }
    }

    private static BeanProxy getBeanProxy(Object bean)
    {
        BeanProxy bp = proxies.get(bean.getClass());
        if (bp == null) {
            bp = new BeanProxy(bean.getClass());
            proxies.put(bean.getClass(), bp);
        }
        return bp;
    }

    public static Object getProperty(Object bean, String name)
    {
        return getBeanProxy(bean).getProperty(bean, name);
    }

    public static Set<String> getProperties(Object bean)
    {
        return getBeanProxy(bean).getProperties();
    }
}
