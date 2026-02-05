package services.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceResult<T> {

    private final boolean success;
    private final T data;
    private final String message;
    private final List<ValidationError> errors;

    private ServiceResult(boolean success, T data, String message, List<ValidationError> errors) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errors = (errors == null) ? new ArrayList<>() : new ArrayList<>(errors);
    }

    public static <T> ServiceResult<T> ok(T data, String message) {
        return new ServiceResult<>(true, data, message, Collections.emptyList());
    }

    public static <T> ServiceResult<T> fail(String message) {
        return new ServiceResult<>(false, null, message, Collections.emptyList());
    }

    public static <T> ServiceResult<T> fail(String message, List<ValidationError> errors) {
        return new ServiceResult<>(false, null, message, errors);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
    public List<ValidationError> getErrors() { return Collections.unmodifiableList(errors); }
}
