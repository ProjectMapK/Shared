package com.mapk.core;

import com.mapk.annotations.KParameterRequireNonNull;
import kotlin.Pair;
import kotlin.reflect.KParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class BucketGenerator {
    private final Boolean[] initializationStatus;

    @NotNull
    private final List<Boolean> isRequireNonNull;
    @NotNull
    private final KParameter[] keyArray;
    @NotNull
    private final Object[] valueArray;

    BucketGenerator(List<KParameter> parameters, @Nullable Pair<KParameter, Object> instancePair) {
        final int capacity = parameters.size();

        isRequireNonNull = new ArrayList<>(capacity);
        initializationStatus = new Boolean[capacity];

        for (int i = 0; i < capacity; i++) {
            isRequireNonNull.add(
                    i,
                    parameters.get(i).getAnnotations().stream().anyMatch(it -> it instanceof KParameterRequireNonNull)
            );
            initializationStatus[i] = false;
        }

        keyArray = new KParameter[capacity];
        valueArray = new Object[capacity];

        if (instancePair != null) {
            keyArray[0] = instancePair.getFirst();
            valueArray[0] = instancePair.getSecond();
            initializationStatus[0] = true;
        } else {
            initializationStatus[0] = false;
        }
    }

    @NotNull
    ArgumentBucket generate() {
        return new ArgumentBucket(
                keyArray.clone(),
                valueArray.clone(),
                isRequireNonNull,
                new InitializationStatusManagerImpl(initializationStatus.clone())
        );
    }
}
