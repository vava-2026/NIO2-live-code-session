import static java.lang.IO.println;

void main() {

    // Setup
    String wd = Path.of("").toAbsolutePath().toString();
    Path examples = Path.of(wd, "examples");
    Path root = Path.of(examples.toAbsolutePath().toString(), "directory-operations");
    try {
        Files.createDirectories(root);
    } catch (IOException e) {
        println("Error setting up root directory: " + e.getMessage());
        return;
    }


//    createDirectoryExample(root, "new-single-directory", "path/of/directories/to/create", "non-existent");
//        listDirectoryExample(root);
//        moveCopyDirectoryExample(root, "demo-dir", "demo-dir-copy", "demo-dir-move-name");
//        directoryStreamExample(root);
//        traverseDirectoryExample(root);
//    walkTreeExampleRecursiveCopy(root, "directory-operations-copy", ".");
    deleteDirectoryExample(root, "missing-dir");
}

public static void createDirectoryExample(Path root, String toCreateDir,
                                          String toCreateDirs, String existent) {
    // Creating directory/directories
    try {
        Path single = Files.createDirectory(root.resolve(toCreateDir));  // mkdir
        Path deep = Files.createDirectories(root.resolve(toCreateDirs)); // mkdir -p
        Files.createDirectory(root.resolve(existent));             // fails if exists

        println(Files.exists(single));                   // true
        println(Files.isDirectory(deep));                // true
        println(Files.isHidden(root));                   // platform-specific
    } catch (IOException e) {
        println("Error creating directory: " + e.getMessage());
    }
}

public static void listDirectoryExample(Path dir) {
    // List directory contents
    try (Stream<Path> s = Files.list(dir)) {
        s.filter(Files::isDirectory).forEach(p -> System.out.println(p.getFileName())); // PRINTS FILENAMES OF THE DIRECTORY.
    } catch (IOException e) {
        println("Error listing directory: " + e.getMessage());
    }
}

public static void moveCopyDirectoryExample(Path root, String source, String copyName, String moveName) {
    try {
        Path src = Files.createDirectories(root.resolve(source)); // create source dir
        Path copy = root.resolve(copyName);
        Path move = root.resolve(moveName);

        Files.copy(src, copy);                                   // copy the dir (shallow)
        Files.move(src, move, StandardCopyOption.REPLACE_EXISTING); // rename / move
    } catch (IOException e) {
        System.out.println("Error copying/moving directory: " + e.getMessage());
    }
}

public static void directoryStreamExample(Path dir) {
    try {
        // Directory stream with global filter
        Files.writeString(dir.resolve("notes.txt"), "hi");
        Files.writeString(dir.resolve("data.csv"), "1,2");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.{txt,csv}")) {
            for (Path p : ds) System.out.println("glob: " + p.getFileName());
        }

        // Directory stream with predicate filter
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir,
                p -> Files.isDirectory(p) && !p.getFileName().toString().equals("existent"))) {
            for (Path p : ds) System.out.println("filter: " + p.getFileName());
        }
    } catch (IOException e) {
        println("Error with directory stream: " + e.getMessage());
    }
}

public static void traverseDirectoryExample(Path dir) {
    try {
        // Traverse directories, uses lazy loading. Should be closed after usage
        try (Stream<Path> s = Files.walk(dir)) {             // unbounded depth
            long count = s.filter(Files::isRegularFile).count();
            System.out.println("files in tree: " + count);
        }
        try (Stream<Path> s = Files.walk(dir, 4)) {          // depth-limited
            s.skip(1).forEach(System.out::println);           // skip root itself
        }
    } catch (IOException e) {
        println("Error traversing directory: " + e.getMessage());
    }
}

public static void deleteDirectoryExample(Path root, String missingDirectory) {
    try {
        // Deleting folder contents recursively
        try (var stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder()) // sorts so deepest files/folders come first
                    .forEach(path -> {
                        try {
                            Files.delete(path); // throws IOException if missing
                        } catch (IOException e) {
                            System.err.println("Failed to delete " + path + " - " + e.getMessage());
                        }
                    });
        }

        Files.deleteIfExists(root.resolve(missingDirectory)); // will ignore exception, but return the boolean result
    } catch (IOException e) {
        println("Error deleting directory: " + e.getMessage());
    }
}

public static void walkTreeExampleRecursiveCopy(Path root, String copyRootName, String folderToTraverse) {
    try {
        Path copyRoot = root.resolve(copyRootName);
        Files.walkFileTree(root.resolve(folderToTraverse), new SimpleFileVisitor<>() {
            public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes a) throws IOException {
                Files.createDirectories(copyRoot.resolve(root.resolve(folderToTraverse).relativize(d)));
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                Files.copy(f, copyRoot.resolve(root.resolve(folderToTraverse).relativize(f)),
                        StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    } catch (IOException e) {
        println("Error walking file tree: " + e.getMessage());
    }
}
