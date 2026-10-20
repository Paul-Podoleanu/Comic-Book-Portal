package comicportaldesktop;

import comicportaldesktop.controllers.ComicCollectionController;
import comicportaldesktop.controllers.ComicReaderController;
import comicportaldesktop.state.ComicLibraryState;
import comicportaldesktop.state.ComicReaderState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class HomeController {
    @FXML
    private BorderPane root;

    @FXML
    public javafx.scene.control.Button homeButton;

    private ComicLibraryState libraryState;
    private ComicReaderState readerState;

    public void setLibraryState(ComicLibraryState libraryState) {
        this.libraryState = libraryState;
    }

    public void setReaderState(ComicReaderState readerState) {
        this.readerState = readerState;
    }

    @FXML
    private void openHomeScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HomeController.class.getResource("home-view.fxml")
        );
        Node homeView = loader.load();
        ComicCollectionController controller = loader.getController();
        controller.setLibraryState(libraryState);
        controller.setReaderState(readerState);
        getApplicationRoot().setCenter(homeView);
        selectNavigationButton("homeButton");
    }

    @FXML
    private void openFavoritesScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HomeController.class.getResource("favourites-view.fxml")
        );
        Node favouritesView = loader.load();
        ComicCollectionController controller = loader.getController();
        controller.setLibraryState(libraryState);
        controller.setReaderState(readerState);
        controller.displayFavourites();
        getApplicationRoot().setCenter(favouritesView);
        selectNavigationButton("favoritesButton");
    }

    @FXML
    private void openComicReader() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HomeController.class.getResource("comic-reader-view.fxml")
        );
        Node readerView = loader.load();
        ComicReaderController controller = loader.getController();
        controller.setReaderState(readerState);
        getApplicationRoot().setCenter(readerView);
        selectNavigationButton("comicsButton");
    }

    @FXML
    private void openTimelinesPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HomeController.class.getResource("timelines-view.fxml")
        );
        Node readerView = loader.load();
        getApplicationRoot().setCenter(readerView);
        selectNavigationButton("timelinesButton");
    }


    private BorderPane getApplicationRoot() {
        return root;
    }

    private void selectNavigationButton(String buttonId) {
        root.lookupAll(".sidebar-button")
                .forEach(node -> node.getStyleClass().remove("selected"));
        Node navigationButton = root.lookup("#" + buttonId);
        if (navigationButton != null) {
            navigationButton.getStyleClass().add("selected");
        }
    }
}
