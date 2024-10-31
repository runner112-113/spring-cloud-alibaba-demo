package com.runner.misc.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Copy from Cassandra 2.x ,But {@link sun.misc.Unsafe} no longer contains the methods monitorEnter + monitorExit after java 9+
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/30 9:24
 */
public class Locks
{
/*    static final Unsafe unsafe;

    static
    {
        try
        {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }
    }

    // enters the object's monitor IF UNSAFE IS PRESENT. If it isn't, this is a no-op.
    public static void monitorEnterUnsafe(Object object)
    {
        if (unsafe != null)
            unsafe.monitorEnter(object);
    }

    public static void monitorExitUnsafe(Object object)
    {
        if (unsafe != null)
            unsafe.monitorExit(object);
    }*/
}
