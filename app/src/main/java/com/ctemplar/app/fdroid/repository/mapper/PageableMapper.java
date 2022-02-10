package com.ctemplar.app.fdroid.repository.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.net.response.PagableResponse;
import com.ctemplar.app.fdroid.repository.dto.PagableDTO;

public class PageableMapper {
    @Nullable
    public static <I, O> PagableDTO<O> map(@NonNull Class<?> clazz, @Nullable PagableResponse<I> response) {
        if (response == null) {
            return null;
        }
        return new PagableDTO<>(
                response.getTotalCount(),
                response.getPageCount(),
                response.isNext(),
                response.isPrevious(),
                CommonMapper.map(clazz, response.getResults())
        );
    }
}
