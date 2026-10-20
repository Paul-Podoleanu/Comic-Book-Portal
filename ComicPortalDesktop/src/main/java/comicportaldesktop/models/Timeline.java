package comicportaldesktop.models;

import java.util.ArrayList;
import java.util.List;

public class Timeline {
    private String title;
    private ArrayList<ComicCard> comicsList;
    private String description;


    /*
     * ==================================================================
     * CONSTRUCTORS
     * ==================================================================
     * */

    public Timeline(ArrayList<ComicCard> comicsList, String description, String title) {
        this.comicsList = new ArrayList<>(comicsList);
        this.description = description;
        this.title = title;
    }

    public Timeline(String title) {
        this.title = title;
        this.comicsList = new ArrayList<>();
    }

    /*
     * ==================================================================
     * GETTERS & SETTERS
     * ==================================================================
     * */

    public ArrayList<ComicCard> getComicsList() {
        return new ArrayList<>(comicsList);
    }

    public void setComicsList(List<ComicCard> comicsList) {
        this.comicsList = new ArrayList<>(comicsList);
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

    /*
     * ==================================================================
     * METHODS
     * ==================================================================
     * */

    public void addComic(ComicCard comic) {
        if (!comicsList.contains(comic)) {
            comicsList.add(comic);
        }
    }

    public void removeComic(ComicCard comic) {
        comicsList.remove(comic);
    }
}
