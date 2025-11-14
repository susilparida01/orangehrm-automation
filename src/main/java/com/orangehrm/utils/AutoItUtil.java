package com.orangehrm.utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class AutoItUtil {
    private AutoItUtil() {}

    /**
     * Runs UploadFile.exe with the absolute path to the file to be uploaded.
     * Works only on Windows.
     */
    public static void uploadWithAutoIt(String absoluteFilePath) {
        if (!isWindows()) {
            throw new UnsupportedOperationException("AutoIt works only on Windows.");
        }
        try {
            String exe = new File("src/test/resources/autoit/UploadFile.exe").getAbsolutePath();
            Process p = new ProcessBuilder(exe, absoluteFilePath).start();
            // Wait a bit for the dialog to process; adjust if needed
            p.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("AutoIt upload failed", e);
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
