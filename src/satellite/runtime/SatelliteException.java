package satellite.runtime;

public class SatelliteException extends Exception {
    public SatelliteException(String format, Object... arguments) {
        super(String.format(format, arguments));
    }
}
