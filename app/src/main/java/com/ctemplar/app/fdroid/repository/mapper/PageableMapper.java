package com.ctemplar.app.fdroid.repository.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctemplar.app.fdroid.net.response.PagableResponse;
import com.ctemplar.app.fdroid.repository.dto.PageableDTO;

public class PageableMapper {
    @Nullable
    public static <I, O> PageableDTO<O> map(@NonNull Class<?> clazz, @Nullable PagableResponse<I> response) {
        if (response == null) {
            return null;
        }
        return new PageableDTO<>(
                response.getTotalCount(),
                response.getPageCount(),
                response.isNext(),
                response.isPrevious(),
                CommonMapper.map(clazz, response.getResults())
        );
    }
}
