package comicportaldesktop.models;

import java.nio.file.Path;

public class ComicCard {
    private final Path comicPath;
    private final String fileName;
    private String title;
    private String description;
    private boolean favourite;

    public ComicCard(Path comicPath, String title, boolean favourite) {
        this.comicPath = comicPath;
        this.fileName = comicPath.getFileName().toString();
        this.title = title;
        this.favourite = favourite;
    }

    public Path getComicPath() {
        return comicPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
