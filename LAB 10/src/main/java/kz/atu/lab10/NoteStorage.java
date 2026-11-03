package kz.atu.lab10;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class NoteStorage {
    private final ObjectMapper mapper = new ObjectMapper();

    public void save(Path path, List<Note> notes) throws IOException {
        Path target = path.toAbsolutePath();
        Path parent = target.getParent();
        Path tmp = Files.createTempFile(parent, "notes-", ".tmp");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), notes); // UTF-8
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    public List<Note> load(Path path) throws IOException {
        return mapper.readValue(path.toFile(), new TypeReference<List<Note>>() {});
    }
}
