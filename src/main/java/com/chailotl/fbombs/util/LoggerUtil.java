package com.chailotl.fbombs.util;

import com.chailotl.fbombs.FBombs;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LoggerUtil {
    public static final Logger LOGGER = FBombs.LOGGER;

    private LoggerUtil() {
    }

    /**
     * Method to display useful information in the console at runtime.<br>
     * The information will only be printed, if the instance is running in a developer environment.<br><br>
     * <i>Override of the {@link #devLogger(String, Type, Exception) devLogger} method.</i>
     *
     * @param input Informative text of the current state of the mod, to display in the game console
     */
    public static void devLogger(String input) {
        devLogger(input, Type.INFO, null);
    }

    /**
     * Uses LOGGER only when the instance has been started in a development environment.<br>
     * In addition, this method can print information or error values
     *
     * @param input     Informative text of the current state of the mod, to display in the game console
     * @param exception If not available, pass over a <b><i>null</i></b> value
     */
    public static void devLogger(String input, Type type, @Nullable Exception exception) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        String printText = "DEV - [ " + input + " ]";
        switch (type) {
            case INFO -> LOGGER.info(printText);
            case WARNING -> LOGGER.warn(printText);
            case ERROR -> {
                if (exception == null) LOGGER.error(printText);
                else LOGGER.error(printText, exception);
            }
        }
    }

    public enum Type {
        INFO, WARNING, ERROR
    }
}
