package com.mapk.core;

import com.mapk.annotations.KParameterRequireNonNull;
import kotlin.Pair;
import kotlin.reflect.KParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class BucketGenerator {
    private final int initializationStatus;
    @NotNull
    private final List<Long> initializeMask;
    private final long completionValue;

    @NotNull
    private final List<Boolean> isRequireNonNull;
    @NotNull
    private final KParameter[] keyArray;
    @NotNull
    private final Object[] valueArray;

    BucketGenerator(List<KParameter> parameters, @Nullable Pair<KParameter, Object> instancePair) {
        final int capacity = parameters.size();

        keyArray = new KParameter[capacity];
        valueArray = new Object[capacity];

        if (instancePair != null) {
            initializationStatus = 1;

            keyArray[0] = instancePair.getFirst();
            valueArray[0] = instancePair.getSecond();
        } else {
            initializationStatus = 0;
        }

        isRequireNonNull = new ArrayList<>(capacity);
        initializeMask = new ArrayList<>(capacity);
        int completionValue = 0;

        long mask = 1;
        for (int i = 0; i < capacity; i++, mask <<= 1) {
            isRequireNonNull.add(
                    i,
                    parameters.get(i).getAnnotations().stream().anyMatch(it -> it instanceof KParameterRequireNonNull)
            );
            initializeMask.add(i, mask);
            completionValue |= mask;
        }

        this.completionValue = completionValue;
    }

    @NotNull
    ArgumentBucket generate() {
        return new ArgumentBucket(
                keyArray.clone(),
                valueArray.clone(),
                isRequireNonNull,
                new BitFlagInitializationStatusManager(initializationStatus, initializeMask, completionValue)
        );
    }
}
