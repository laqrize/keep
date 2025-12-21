package pl.ros.keep.commons.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NpeUtils {

    public static <T extends Enum> String getName(T value) {
        return Optional.of(value).map(Enum::name).orElse(null);
    }

    public static <T extends Enum> T getValue(String value, Class<T> enumClass) {
        return Optional.of(value).map(v -> (T) Enum.valueOf(enumClass, v)).orElse(null);
    }

    public static <R> R getValue(Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            log.trace("There was an error during the conversion", e);
            return null;
        }
    }

    public static <T, ID> T createObjectWithId(Class<T> clazz, ID id) {
        if (id == null) {
            return null;
        }
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(obj, id);
            return obj;
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Nie udało się stworzyć obiektu klasy " + clazz.getName() + " z ID: " + id, e);
        }
    }

}