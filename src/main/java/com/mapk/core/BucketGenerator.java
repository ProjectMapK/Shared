package com.mapk.core;

import kotlin.Pair;
import kotlin.reflect.KParameter;

import java.util.ArrayList;
import java.util.List;

class BucketGenerator {
    private final int initializationStatus;
    private final List<Integer> initializeMask;
    private final int completionValue;

    private final KParameter[] keyArray;
    private final Object[] valueArray;

    BucketGenerator(int capacity, Pair<KParameter, Object> instancePair) {
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
