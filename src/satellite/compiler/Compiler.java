package satellite.compiler;

import satellite.compiler.syntax.Program;

import java.nio.file.Files;
import java.nio.file.Path;

public class Compiler {
    public static void main(String[] args) {
        try {
            String source = Files.readString(Path.of("test.stl"));
            System.out.println(Program.parser().parseTotal(source));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
