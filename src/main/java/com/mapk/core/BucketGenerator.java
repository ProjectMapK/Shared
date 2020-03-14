package com.mapk.core;

import kotlin.Pair;
import kotlin.reflect.KParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class BucketGenerator {
    private final int initializationStatus;
    @NotNull
    private final List<Integer> initializeMask;
    private final int completionValue;

    @NotNull
    private final KParameter[] keyArray;
    @NotNull
    private final Object[] valueArray;

    BucketGenerator(int capacity, @Nullable Pair<KParameter, Object> instancePair) {
        keyArray = new KParameter[capacity];
        valueArray = new Object[capacity];

        if (instancePair != null) {
            initializationStatus = 1;

            keyArray[0] = instancePair.getFirst();
            valueArray[0] = instancePair.getSecond();
        } else {
            initializationStatus = 0;
        }

        initializeMask = new ArrayList<>(capacity);
        int completionValue = 0;

        for (int i = 0, mask = 1; i < capacity; i++, mask <<= 1) {
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
                initializationStatus,
                initializeMask,
                completionValue
        );
    }
}
