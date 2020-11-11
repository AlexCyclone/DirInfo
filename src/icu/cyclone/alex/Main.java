package icu.cyclone.alex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        String pathString = ConsoleUtils.requestString("Input directory path: ", (s) -> Files.isDirectory(Paths.get(s)));
        Path baseDirectory = Paths.get(pathString);

        DirInfoVisitor dirInfoVisitor = new DirInfoVisitor();
        Files.walkFileTree(baseDirectory, dirInfoVisitor);
        dirInfoVisitor.printSummary(System.out);
        dirInfoVisitor.printDetails(System.out);
    }
}
