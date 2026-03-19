// WatchService is an event-driven alternative to polling the filesystem
void main() throws Exception {
    // Create a WatchService from the default filesystem
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
        // You don’t watch files directly, only directories
        // Directory we want to watch
        Path directory = Path.of("examples/watch-service-examples");

        // Registers directory with a watch service and specify events we want to listen
        directory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        // keep waiting for filesystem events until the key becomes invalid or program stops
        while (true) {
            // take() blocks the thread until at least one event arrives
            // When something happens, you receive a WatchKey that represents “this directory has events ready”
            WatchKey key = watchService.take();

            // A WatchKey can contain multiple events (batch).
            // pollEvents() returns all currently queued events for that key.
            for (WatchEvent<?> event : key.pollEvents()) {
                // get event type
                WatchEvent.Kind<?> kind = event.kind();

                // OVERFLOW is special event to indicate that events may have been lost or discarded.
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // context() is the “item inside the watched directory” that changed
                Path fileName = (Path) event.context(); // relative to dir

                System.out.println(kind + ": " + fileName);
            }

            // Re-enables this WatchKey to receive further events. If we don’t, the watcher stops.
            boolean valid = key.reset();

            if (!valid) {
                System.out.println("Key is no longer valid (directory deleted/unmounted?)");
                break;
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}
