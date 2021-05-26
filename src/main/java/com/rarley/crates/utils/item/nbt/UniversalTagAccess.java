/**************************************
 Copyright (c) 2021 | RarleyCrates.   *
 *
 Author github.com/pedroagrs          *
 *
 Rarley, Inc (github.com/RarleyInc)   *
 **************************************/

package com.rarley.crates.utils.item.nbt;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class UniversalTagAccess {

    @Getter
    private final Object compound;

    private final Lock
            getLocker = new ReentrantLock(), // sync action
            setLocker = new ReentrantLock(),
            hasLocker = new ReentrantLock();

    public boolean hasKey(@NonNull String key) {
        hasLocker.lock();

        try {
            return (boolean) invoke("hasKey", key, null);
        } finally {
            hasLocker.unlock();
        }
    }

    public void set(@NonNull String key, @NonNull Object value) {
        setLocker.lock();

        try {
            verify("set", key, value);
        } finally {
            setLocker.unlock();
        }
    }

    public <T> Object get(@NonNull String key, @NonNull T type) {
        getLocker.lock();

        try {
            return verify("get", key, type);
        } finally {
            getLocker.unlock();
        }
    }

    @SneakyThrows
    private <T> Object verify(String action, @NonNull String key, T value) {
        final ImmutableMap.Builder<Object, Class<?>[]> builder = new ImmutableMap.Builder<>();
        final String type = String.valueOf(value).replace("class java.lang.", "").toLowerCase();

        if (value instanceof String || type.equals("string")) // set check || get check
            return invoke(String.format("%sString", action), key,
                    builder.put(value, new Class[]{String.class, String.class}).build());

        else if (value instanceof Double || type.equals("double"))
            return invoke(String.format("%sDouble", action), key,
                    builder.put(value, new Class[]{String.class, double.class}).build());

        else if (value instanceof Integer || type.equals("int"))
            return invoke(String.format("%sInt", action), key,
                    builder.put(value, new Class[]{String.class, int.class}).build());

        else if (value instanceof Float || type.equals("float"))
            return invoke(String.format("%sFloat", action), key,
                    builder.put(value, new Class[]{String.class, float.class}).build());

        else if (value instanceof Long || type.equals("long"))
            return invoke(String.format("%sLong", action), key,
                    builder.put(value, new Class[]{String.class, long.class}).build());

        else if (value instanceof Short || type.equals("short"))
            return invoke(String.format("%sShort", action), key,
                    builder.put(value, new Class[]{String.class, short.class}).build());

        else if (value instanceof Byte || type.equals("byte"))
            return invoke(String.format("%sByte", action), key,
                    builder.put(value, new Class[]{String.class, byte.class}).build());

        else if (value instanceof Boolean || type.equals("boolean"))
            return invoke(String.format("%sBoolean", action), key,
                    builder.put(value, new Class[]{String.class, boolean.class}).build());

        else throw new IllegalArgumentException("Illegal type (UniversalTagAccess).");
    }

    @SneakyThrows
    private Object invoke(String methodName, String key, ImmutableMap<Object, Class<?>[]> entry) {
        AtomicReference<Object> objectBuilder = new AtomicReference<>();

        if (entry == null) {
            objectBuilder.set(getMethodSingleParam(methodName, key, new Class[]{String.class}));
        } else {
            entry.forEach((value, clazz) -> {
                try {
                    objectBuilder.set(getMethod(methodName, clazz).invoke(compound, key, value));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
                    objectBuilder.set(getMethodSingleParam(methodName, key, clazz));
                }
            });
        }

        return objectBuilder.get();

    }

    private Object getMethodSingleParam(@NonNull String method, String key, Class<?>[] clazz) {
        try {
            return compound.getClass().getMethod(method, new Class[]{clazz[0]}).invoke(compound, key);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            return null;
        }
    }

    private Method getMethod(String method, Class<?>[] params) throws NoSuchMethodException {
        return compound.getClass().getMethod(method, params);
    }
}
