import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class PathDemo {

    public static void main(String[] args) throws IOException {
        demoPathCreation();
        demoPathManipulation();
        demoFileIoComparison();
    }

    private static void demoPathCreation() {
        System.out.println("=== Path Creation ===");
        // Path.of() is the modern replacement for Paths.get() (Java 11+)
        Path p = Path.of(System.getProperty("user.home"), "source","repos", "NIO2-live-code-session", "test.txt");

        System.out.println("FileName:   " + p.getFileName());
        System.out.println("Parent:     " + p.getParent());
        System.out.println("NameCount:  " + p.getNameCount());
        System.out.println("Name(0):    " + p.getName(0));
        System.out.println("Name(1):    " + p.getName(1));
    }

    private static void demoPathManipulation() {
        System.out.println("\n=== Path Manipulation ===");

        Path base = Path.of(System.getProperty("user.home"), "source","repos");

        System.out.println("Base path:  " + base);
        System.out.println("Resolved:   " + base.resolve("NIO2-live-code-session/test.txt"));

        // Logic: base / .. / base last part / . / service
        Path messy = base.resolve("..")
                .resolve(base.getFileName())
                .resolve(".")
                .resolve("service");

        System.out.println("Messy path: " + messy);
        System.out.println("Normalized: " + messy.normalize());
    }

    private static void demoFileIoComparison() throws IOException {
        System.out.println("\n=== java.io.File vs NIO.2 ===");

        // 1. OLD WAY
        Path tmpOld = Files.createTempFile("io_demo_old_", ".txt");
        boolean deleted = tmpOld.toFile().delete(); // Returns false silently if it fails
        System.out.println("File.delete() success: " + deleted);

        // 2. NEW WAY
        Path tmpNew = Files.createTempFile("nio2_demo_new_", ".txt");
        Files.delete(tmpNew);
        try {
            Files.delete(tmpNew); // Attempting to delete again (NoSuchFileException will be thrown)
        } catch (NoSuchFileException e) {
            System.out.println("File doesnt exist: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Unexpected I/O error: " + e.getMessage());
        }

        // 3. DIRECTORY LISTING
        Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"));
        System.out.println("\n5 regular files in Temp Dir:");

        // try-with-resources is critical here to close the underlying OS directory stream
        try (var stream = Files.list(tmpDir)) {
            stream.filter(Files::isRegularFile)
                    .limit(5)
                    .forEach(path -> System.out.println(" -> " + path.getFileName()));
        }
    }
}