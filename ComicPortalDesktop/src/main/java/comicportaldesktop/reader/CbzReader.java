package comicportaldesktop.reader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.zip.ZipFile;
import java.util.stream.Collectors;
import java.io.InputStream;

public class CbzReader {
    public List<String> getPageNames(Path cbzPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(cbzPath.toFile())) {
            return zipFile.stream()
                    .filter(entry -> !entry.isDirectory())
                    .map(entry -> entry.getName())
                    .filter(this::isImage)
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList());
        }
    }

    private boolean isImage(String fileName) {
        String name = fileName.toLowerCase(Locale.ROOT);
        return name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".webp")
                || name.endsWith(".gif");
    }

    public Optional<byte[]> getFirstPage(Path cbzPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(cbzPath.toFile())) {
            String firstPageName = zipFile.stream()
                    .filter(entry -> !entry.isDirectory())
                    .map(entry -> entry.getName())
                    .filter(this::isImage)
                    .sorted(Comparator.naturalOrder())
                    .findFirst()
                    .orElse(null);

            if (firstPageName == null) {
                return Optional.empty();
            }

            try (InputStream inputStream =
                         zipFile.getInputStream(zipFile.getEntry(firstPageName))) {
                return Optional.of(inputStream.readAllBytes());
            }
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }

    public List<byte[]> getPages(Path cbzPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(cbzPath.toFile())) {
            return  zipFile.stream()
                    .filter(entry -> !entry.isDirectory())
                    .filter(entry -> isImage(entry.getName()))
                    .sorted(Comparator.comparing(entry -> entry.getName()))
                    .map(entry -> {
                        try (InputStream inputStream = zipFile.getInputStream(entry)) {
                            return inputStream.readAllBytes();
                        } catch (IOException exception) {
                            throw new PageReadException(exception);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    private static class PageReadException extends RuntimeException {
        private PageReadException(IOException cause) {
            super(cause);
        }

        public IOException getCause() {
            return (IOException) super.getCause();
        }
    }
}
