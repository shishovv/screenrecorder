package screenrecorder.util;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static final Logger LOG = Logger.getLogger("ScreenRecorder");

    static {
        LOG.setLevel(Level.INFO);
    }

    private Log() {}

    public static void i(@NotNull final String msg) {
        LOG.info(msg);
    }
}
