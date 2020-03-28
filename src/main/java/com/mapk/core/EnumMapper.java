package com.mapk.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Kotlinの型推論バグでクラスからvalueOfが使えないため、ここだけJavaで書いている（型引数もT extends Enumでは書けなかった）
 */
public class EnumMapper {
    /**
     * 文字列 -> Enumのマッピング
     * @param clazz Class of Enum
     * @param value StringValue
     * @param <T> enumClass
     * @return Enum.valueOf
     */
    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T getEnum(@NotNull Class<T> clazz, @Nullable String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return (T) Enum.valueOf((Class<? extends Enum>) clazz, value);
    }
}
