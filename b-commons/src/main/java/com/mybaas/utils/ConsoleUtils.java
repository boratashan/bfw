package com.mybaas.utils;

/**
 * Console utility
 *
 * Console utils provide some of usefull console output methods such as terminal colors, clearing screen etc
 */
public class ConsoleUtils {

    public static void resetColour() {
        System.out.print(AnsiColours.RESET.getAnsiCode());
    }

    public static void setColor(AnsiColours color) {
        System.out.print(color.getAnsiCode());
    }

    public static void writeLine(String line) {
        System.out.println(line);
    }

    public static void write(String message) {
        System.out.print(message);
    }

    @SuppressWarnings("unused")
    public static void clearScreen() {
        System.out.println("\u001B[2j");
    }

    public static void writeException(Exception e) {
        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
        ConsoleUtils.writeLine(String.format("Error = [%s] %s", e.toString(), e.getMessage()));
        ConsoleUtils.resetColour();
    }


    public static void printLogo(String logo, AnsiColours colour) {
        setColor(colour);
        writeLine(logo);
        resetColour();

    }

    @SuppressWarnings("unused")
    public enum AnsiColours {
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        private final String ansiCode;

        AnsiColours(String ansiCode) {
            this.ansiCode = ansiCode;
        }

        private String getAnsiCode() {
            return this.ansiCode;
        }
    }

}
