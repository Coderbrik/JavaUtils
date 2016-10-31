package com.redstoner.javautils.blockplacemods;

import com.redstoner.javautils.blockplacemods.saving.Saveable;
import com.redstoner.javautils.blockplacemods.saving.SubSaveable;

public class Boxed<T> extends SubSaveable {

    public static <T> Boxed<T> box(Saveable superSaveable, T value) {
        return new Boxed<>(superSaveable, value);
    }

    public T value;

    private Boxed(Saveable superSaveable, T value) {
        super(superSaveable);
        this.value = value;
    }

}
