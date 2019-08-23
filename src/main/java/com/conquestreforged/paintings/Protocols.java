package com.conquestreforged.paintings;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Protocols {

    public static final Supplier<String> VERSION = () -> "";
    public static final Predicate<String> CLIENT = s -> true;
    public static final Predicate<String> SERVER = s -> true;
}
