package comicportaldesktop.state;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import comicportaldesktop.models.ComicCard;
import comicportaldesktop.models.Timeline;

public class ComicLibraryState {
    private List<Path> selectedFolders = new ArrayList<>();
    private List<Path> comicFiles = List.of();
    private final Timeline comicsTimeline = new Timeline("All Comics");
    private final LibraryPreferences preferences = new LibraryPreferences();
    private List<Path> favourites;

    public ComicLibraryState() {
        selectedFolders = preferences.loadSelectedFolders();
        favourites = preferences.loadFavourites();
    }

    public List<Path> getComicFiles() {
        return comicFiles;
    }

    public void setComicFiles(List<Path> comicFiles) {
        this.comicFiles = List.copyOf(comicFiles);
    }

    public List<ComicCard> getComicsList() {
        return comicsTimeline.getComicsList();
    }

    public void setComicsList(List<ComicCard> comicsList) {
        comicsTimeline.setComicsList(comicsList);
    }

    public List<Path> getSelectedFolders() {
        return selectedFolders;
    }

    public void setSelectedFolders(List<Path> selectedFolder) {
        this.selectedFolders = new ArrayList<>(selectedFolder);
        preferences.saveSelectedFolders(this.selectedFolders);
    }

    public void addFolder(Path folder) {
        if (!selectedFolders.contains(folder)) {
            selectedFolders.add(folder);
            preferences.saveSelectedFolders(selectedFolders);
        }
    }

    public boolean isFavourite(Path comicPath) {
        return favourites.contains(normalize(comicPath));
    }

    public void setFavourite(Path comicPath, boolean favourite) {
        Path normalizedPath = normalize(comicPath);
        favourites.remove(normalizedPath);

        if (favourite) {
            favourites.add(normalizedPath);
        }

        preferences.saveFavourites(favourites);

        comicsTimeline.getComicsList().stream()
                .filter(comic -> normalize(comic.getComicPath()).equals(normalizedPath))
                .forEach(comic -> comic.setFavourite(favourite));
    }

    public List<Path> getFavourites() {
        return List.copyOf(favourites);
    }

    private Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }
}