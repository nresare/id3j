package com.voxbiblia.jid3;

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
