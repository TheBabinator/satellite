package satellite.runtime;

import java.io.PrintStream;
import java.io.PrintWriter;

public class SatelliteRuntimeException extends RuntimeException {
    private final String traceback;

    public SatelliteRuntimeException(SatelliteException cause, String traceback) {
        super(String.format("%s\nDebug %s", cause.getMessage(), traceback), cause);
        this.traceback = traceback;
    }

    public String getTraceback() {
        return traceback;
    }
}
