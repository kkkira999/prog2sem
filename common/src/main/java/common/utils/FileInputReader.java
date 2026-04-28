package common.utils;

import java.util.Scanner;

public class FileInputReader implements InputReader {
    private Scanner fileScanner;

    public FileInputReader(Scanner fileScanner) {
        this.fileScanner = fileScanner;
    }

    @Override
    public String readLine() {
        return fileScanner.nextLine();
    }
    public boolean hasNextLine() {
        return fileScanner.hasNextLine();
    }
}