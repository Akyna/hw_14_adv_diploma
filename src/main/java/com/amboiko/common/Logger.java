package com.amboiko.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void info(final String message) {
        System.out.println(
                "[INFO: "
                        + (LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                        + "]: "
                        + message
        );
    }

    public static void error(final String message) {
        System.out.println(
                ANSI_RED + "[ERROR: "
                        + (LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                        + "]: "
                        + message
                        + ANSI_RESET
        );
    }
}
