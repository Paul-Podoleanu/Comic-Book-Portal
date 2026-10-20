package comicportaldesktop.state;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ComicReaderState {
    private Path comicPath;

    private final Map<Path, Integer> comicPageIndexes = new HashMap<>();

    public Path getComicPath() {
        return comicPath;
    }

    public void setComicPath(Path comicPath) {
        this.comicPath = comicPath == null ? null : normalize(comicPath);
    }

    public int getPageIndex(Path comicPath) {
        return comicPageIndexes.getOrDefault(
                normalize(comicPath),
                0
        );
    }

    public void setPageIndex(Path comicPath, int pageIndex) {
        comicPageIndexes.put(
                normalize(comicPath),
                pageIndex
        );
    }

    private Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }
}