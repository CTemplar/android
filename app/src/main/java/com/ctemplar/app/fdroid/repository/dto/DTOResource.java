package com.ctemplar.app.fdroid.repository.dto;

import static com.ctemplar.app.fdroid.utils.DateUtils.GENERAL_GSON;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import com.ctemplar.app.fdroid.net.response.HttpErrorResponse;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class DTOResource<T> {
    private T dto;
    private String error;
    private boolean success;

    public DTOResource() {
    }

    public T getDto() {
        return dto;
    }

    public void setDto(T dto) {
        this.dto = dto;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setError(Throwable e) {
        error = "";
        if (e instanceof HttpException) {
            Response<?> errorResponse = ((HttpException) e).response();
            if (errorResponse == null || errorResponse.errorBody() == null) {
                Timber.e("errorResponse is null");
                return;
            }
            try {
                String errorBody = errorResponse.errorBody().string();
                if (errorBody.isEmpty()) {
                    Timber.e("errorBody is empty");
                    return;
                }
                HttpErrorResponse httpErrorResponse = GENERAL_GSON.fromJson(errorBody,
                        HttpErrorResponse.class);
                if (httpErrorResponse.getError() == null) {
                    Timber.e("httpErrorResponse is null");
                    return;
                }
                error = httpErrorResponse.getError().getError();
            } catch (IOException | JsonSyntaxException ex) {
                Timber.e(ex, "Can't parse server error");
                error = "The server returned an error, but it could not be read";
            }
        }
    }

    public static <T> DTOResource<T> success(T dto) {
        DTOResource<T> response = new DTOResource<>();
        response.setDto(dto);
        response.setSuccess(true);
        return response;
    }

    public static <T> DTOResource<T> error(Throwable e) {
        DTOResource<T> response = new DTOResource<>();
        response.setError(e);
        response.setSuccess(false);
        return response;
    }
}
