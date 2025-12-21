package pl.ros.keep.infrastracture.exceptions;

import java.util.Objects;

public class VersionMismatchException extends RuntimeException {
    private static final String MESSAGE = "Version mismatch [ Id = %s, class = %s ]";
    public VersionMismatchException(Object id, Class<?> clazz) {
        super(String.format(MESSAGE, Objects.toString(id), clazz.getSimpleName()));
    }
}