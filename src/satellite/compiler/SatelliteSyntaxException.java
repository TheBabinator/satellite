package satellite.compiler;

public class SatelliteSyntaxException extends Exception {
    public SatelliteSyntaxException(String format, Object... arguments) {
        super(String.format(format, arguments));
    }
}
