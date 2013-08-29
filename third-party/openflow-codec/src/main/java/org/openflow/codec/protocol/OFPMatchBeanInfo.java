package org.openflow.codec.protocol;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Extra info for how to treat OFPMatch as a JavaBean
 *
 * For some (inane!) reason, using chained setters in OFPMatch breaks a lot of
 * the JavaBean defaults.
 *
 * We don't really use OFPMatch as a java bean, but there are a lot of nice XML
 * utils that work for free if OFPMatch follows the java bean paradigm.
 *
 * @author Rob Sherwood (rob.sherwood@stanford.edu)
 *
 */

public class OFPMatchBeanInfo extends SimpleBeanInfo {

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        List<PropertyDescriptor> descs = new LinkedList<PropertyDescriptor>();
        Field[] fields = OFPMatch.class.getDeclaredFields();
        String name;
        for (int i = 0; i < fields.length; i++) {
            int mod = fields[i].getModifiers();
            if (Modifier.isFinal(mod) || // don't expose static or final fields
                    Modifier.isStatic(mod))
                continue;

            name = fields[i].getName();
            Class<?> type = fields[i].getType();

            try {
                descs.add(new PropertyDescriptor(name, name2getter(OFPMatch.class, name), name2setter(OFPMatch.class,
                        name, type)));
            } catch (IntrospectionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return descs.toArray(new PropertyDescriptor[0]);
    }

    private Method name2setter(Class<OFPMatch> c, String name, Class<?> type) {
        String mName = "set" + toLeadingCaps(name);
        Method m = null;
        try {
            m = c.getMethod(mName, new Class[] { type });
        } catch (SecurityException e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return m;
    }

    private Method name2getter(Class<OFPMatch> c, String name) {
        String mName = "get" + toLeadingCaps(name);
        Method m = null;
        try {
            m = c.getMethod(mName, new Class[] {});
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return m;
    }

    private String toLeadingCaps(String s) {
        char[] array = s.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        return String.valueOf(array, 0, array.length);
    }
}
