package comicportaldesktop.state;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public final class LibraryPreferences {
    private static final String SELECTED_FOLDER_COUNT = "selectedFolderCount";
    private static final String SELECTED_FOLDER_PREFIX = "selectedFolder.";
    private static final String FAVOURITE_COUNT = "favouriteCount";
    private static final String FAVOURITE_PREFIX = "favourite.";

    private final Preferences preferences = Preferences.userNodeForPackage(
            LibraryPreferences.class
    );

    public List<Path> loadSelectedFolders() {
        int folderCount = preferences.getInt(SELECTED_FOLDER_COUNT, 0);
        List<Path> folders = new ArrayList<>();

        for (int index = 0; index < folderCount; index++) {
            String folder = preferences.get(
                    SELECTED_FOLDER_PREFIX + index,
                    null
            );

            if (folder != null && !folder.isBlank()) {
                folders.add(Path.of(folder));
            }
        }

        return folders;
    }

    public void saveSelectedFolders(List<Path> folders) {
        int previousFolderCount = preferences.getInt(
                SELECTED_FOLDER_COUNT,
                0
        );

        for (int index = 0; index < previousFolderCount; index++) {
            preferences.remove(SELECTED_FOLDER_PREFIX + index);
        }

        preferences.putInt(SELECTED_FOLDER_COUNT, folders.size());
        for (int index = 0; index < folders.size(); index++) {
            preferences.put(
                    SELECTED_FOLDER_PREFIX + index,
                    folders.get(index).toAbsolutePath().normalize().toString()
            );
        }
    }

    public List<Path> loadFavourites() {
        int favouriteCount = preferences.getInt(FAVOURITE_COUNT, 0);
        List<Path> favourites = new ArrayList<>();

        for (int index = 0; index < favouriteCount; index++) {
            String favourite = preferences.get(
                    FAVOURITE_PREFIX + index,
                    null
            );

            if (favourite != null && !favourite.isBlank()) {
                favourites.add(Path.of(favourite));
            }
        }

        return favourites;
    }

    public void saveFavourites(List<Path> favourites) {
        int previousFavouriteCount = preferences.getInt(FAVOURITE_COUNT, 0);

        for (int index = 0; index < previousFavouriteCount; index++) {
            preferences.remove(FAVOURITE_PREFIX + index);
        }

        preferences.putInt(FAVOURITE_COUNT, favourites.size());
        for (int index = 0; index < favourites.size(); index++) {
            preferences.put(
                    FAVOURITE_PREFIX + index,
                    favourites.get(index).toAbsolutePath().normalize().toString()
            );
        }
    }
}
