package com.redstoner.javautils.blockplacemods.util;

@FunctionalInterface
public interface ThrowingSupplier<T> {
	T get() throws Throwable;
}
