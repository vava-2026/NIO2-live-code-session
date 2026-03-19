void main() throws IOException {
    Path file = Path.of("ReadingWriting.txt");


    IO.println("1. Writing a file");

    Files.writeString(file, "Hello, world!\n", StandardCharsets.UTF_8);

    List<String> lines = List.of("Line 1", "Line 2", "Line 3");
    Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

    IO.println("Written: " + Files.size(file) + " bytes");


    IO.println("\n2a. Read entire file as String");

    String content = Files.readString(file, StandardCharsets.UTF_8);
    IO.println(content);

    IO.println("2b. Read all lines into a List");

    List<String> allLines = Files.readAllLines(file, StandardCharsets.UTF_8);
    allLines.forEach(System.out::println);


    IO.println("\n3. Stream lines");

    try (Stream<String> stream = Files.lines(file, StandardCharsets.UTF_8)) {
        stream
                .filter(l -> l.startsWith("Line"))
                .map(String::toUpperCase)
                .forEach(System.out::println);
    }


    IO.println("\n4. Buffered Writer / Reader");

    Path bufferedFile = Path.of("buffer.txt");

    try (BufferedWriter writer = Files.newBufferedWriter(bufferedFile,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING)) {

        writer.write("Buffered write — line A");
        writer.newLine();
        writer.write("Buffered write — line B");
        writer.newLine();
    }

    try (BufferedReader reader = Files.newBufferedReader(bufferedFile,
            StandardCharsets.UTF_8)) {

        String line;
        while ((line = reader.readLine()) != null) {
            IO.println(">> " + line);
        }
    }


    IO.println("\n5. StandardOpenOption examples");

    Path options = Path.of("options.txt");

    Files.writeString(options, "Hello world again\n",
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);

    Files.writeString(options, "Appended line\n",
            StandardOpenOption.APPEND);

    try {
        Files.writeString(options, "Error line",
                StandardOpenOption.CREATE_NEW);
    } catch (FileAlreadyExistsException e) {
        IO.println("CREATE_NEW caught: " + e.getMessage());
    }

    IO.println("options.txt content:");
    IO.println(Files.readString(options));


    Files.deleteIfExists(file);
    Files.deleteIfExists(bufferedFile);
    Files.deleteIfExists(options);
}
