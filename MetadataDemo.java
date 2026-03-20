void main() {
    Path path = Path.of("examples/example.txt");
    try {

        System.out.println("=== BASIC CHECKS ===");
        System.out.println("Path: " + path.toAbsolutePath());
        System.out.println("Exists: " + Files.exists(path));
        System.out.println("Readable: " + Files.isReadable(path));
        System.out.println("Writable: " + Files.isWritable(path));
        System.out.println("Regular file: " + Files.isRegularFile(path));
        System.out.println("Directory: " + Files.isDirectory(path));
        System.out.println();

        System.out.println("=== BASIC FILE ATTRIBUTES ===");
        BasicFileAttributes attrs =
                Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

        System.out.println("Size: " + attrs.size() + " bytes");
        System.out.println("Created: " + attrs.creationTime());
        System.out.println("Last modified: " + attrs.lastModifiedTime());
        System.out.println("Is regular file: " + attrs.isRegularFile());
        System.out.println("Is directory: " + attrs.isDirectory());
        System.out.println();


        System.out.println("=== SINGLE ATTRIBUTE ===");
        long size = (Long) Files.getAttribute(path, "basic:size", LinkOption.NOFOLLOW_LINKS);
        System.out.println("Size via getAttribute: " + size + " bytes");
        System.out.println();


        System.out.println("=== MULTIPLE ATTRIBUTES AS MAP ===");
        Map<String, Object> map = Files.readAttributes(
                path,
                "basic:size,lastModifiedTime,creationTime,isRegularFile",
                LinkOption.NOFOLLOW_LINKS
        );

        map.forEach((key, value) -> System.out.println(key + " = " + value));
        System.out.println();


        System.out.println("=== UPDATE LAST MODIFIED TIME ===");
        FileTime oldTime = Files.getLastModifiedTime(path);
        System.out.println("Old lastModifiedTime: " + oldTime);

        FileTime newTime = FileTime.from(Instant.now());
        Files.setLastModifiedTime(path, newTime);

        FileTime updatedTime = Files.getLastModifiedTime(path);
        System.out.println("New lastModifiedTime: " + updatedTime);

    } catch (IOException e) {
        System.out.println("I/O error: " + e.getMessage());
    }
}
