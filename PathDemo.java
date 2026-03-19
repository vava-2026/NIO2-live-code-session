void main() throws IOException {
    demoPathCreation();
    demoPathManipulation();
    demoFileIoComparison();
}

private static void demoPathCreation() {
    IO.println("=== Path Creation ===");
    // Path.of() is the modern replacement for Paths.get() (Java 11+)
    Path p = Path.of(System.getProperty("user.home"), "source", "repos", "NIO2-live-code-session", "test.txt");

    IO.println("FileName:   " + p.getFileName());
    IO.println("Parent:     " + p.getParent());
    IO.println("NameCount:  " + p.getNameCount());
    IO.println("Name(0):    " + p.getName(0));
    IO.println("Name(1):    " + p.getName(1));
}

private static void demoPathManipulation() {
    IO.println("\n=== Path Manipulation ===");

    Path base = Path.of(System.getProperty("user.home"), "source", "repos");

    IO.println("Base path:  " + base);
    IO.println("Resolved:   " + base.resolve("NIO2-live-code-session/test.txt"));

    // Logic: base / .. / base last part / . / service
    Path messy = base.resolve("..")
            .resolve(base.getFileName())
            .resolve(".")
            .resolve("service");

    IO.println("Messy path: " + messy);
    IO.println("Normalized: " + messy.normalize());
}

private static void demoFileIoComparison() throws IOException {
    IO.println("\n=== java.io.File vs NIO.2 ===");

    // 1. OLD WAY
    Path tmpOld = Files.createTempFile("io_demo_old_", ".txt");
    boolean deleted = tmpOld.toFile().delete(); // Returns false silently if it fails
    IO.println("File.delete() success: " + deleted);

    // 2. NEW WAY
    Path tmpNew = Files.createTempFile("nio2_demo_new_", ".txt");
    Files.delete(tmpNew);
    try {
        Files.delete(tmpNew); // Attempting to delete again (NoSuchFileException will be thrown)
    } catch (NoSuchFileException e) {
        IO.println("File doesnt exist: " + e.getMessage());
    } catch (IOException e) {
        IO.println("Unexpected I/O error: " + e.getMessage());
    }

    // 3. DIRECTORY LISTING
    Path tmpDirectory = Path.of(System.getProperty("java.io.tmpdir"));
    IO.println("\n5 regular files in Temp Directory:");

    // try-with-resources is critical here to close the underlying OS directory stream
    try (var stream = Files.list(tmpDirectory)) {
        stream.filter(Files::isRegularFile)
                .limit(5)
                .forEach(path -> IO.println("\t" + path.getFileName()));
    }
}
