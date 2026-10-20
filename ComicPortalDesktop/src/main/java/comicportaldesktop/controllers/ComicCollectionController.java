package comicportaldesktop.controllers;

import comicportaldesktop.models.ComicCard;
import comicportaldesktop.reader.CbzReader;
import comicportaldesktop.state.ComicLibraryState;
import comicportaldesktop.state.ComicReaderState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ComicCollectionController {
    @FXML
    private TilePane comicGrid;

    @FXML
    private Label comicStatusLabel;

    private final CbzReader cbzReader = new CbzReader();
    private ComicLibraryState libraryState;
    private ComicReaderState readerState;
    private boolean folderReadError;

    public void setLibraryState(ComicLibraryState libraryState) {
        this.libraryState = libraryState;
        loadSavedComics();
        displayAllComics();
    }

    public void setReaderState(ComicReaderState readerState) {
        this.readerState = readerState;
    }

    @FXML
    private void displayComics() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Comic Folder");

        Window window = comicGrid.getScene().getWindow();
        File selectedFolder = directoryChooser.showDialog(window);
        if (selectedFolder == null) {
            return;
        }

        libraryState.setSelectedFolders(List.of(selectedFolder.toPath()));
        loadSavedComics();
        displayAllComics();
    }

    @FXML
    private void removeSavedFolders() {
        libraryState.setSelectedFolders(List.of());
        loadSavedComics();
        displayAllComics();
    }

    public void displayAllComics() {
        displayCards(libraryState == null
                ? List.of()
                : libraryState.getComicsList());
    }

    public void displayFavourites() {
        if (libraryState == null) {
            displayCards(List.of());
            return;
        }

        List<ComicCard> favouriteComics = libraryState.getComicsList().stream()
                .filter(ComicCard::isFavourite)
                .toList();
        displayCards(favouriteComics);
    }

    private void displayCards(List<ComicCard> comicCards) {
        if (comicGrid == null || comicStatusLabel == null) {
            return;
        }

        comicGrid.getChildren().clear();
        if (comicCards.isEmpty()) {
            comicStatusLabel.setText(
                    folderReadError
                            ? "Could not read one of the saved comic folders."
                            : libraryState != null
                            && !libraryState.getSelectedFolders().isEmpty()
                            ? "No comics found."
                            : "No comic folder selected."
            );
            return;
        }

        comicStatusLabel.setText(comicCards.size() + " comic(s) found.");
        comicCards.stream()
                .map(this::createComicCard)
                .forEach(comicGrid.getChildren()::add);
    }

    private VBox createComicCard(ComicCard comicCard) {
        Path comicPath = comicCard.getComicPath();
        ImageView coverImageView = new ImageView();
        coverImageView.setFitWidth(160);
        coverImageView.setFitHeight(220);
        coverImageView.setPreserveRatio(true);
        coverImageView.setSmooth(true);

        try {
            cbzReader.getFirstPage(comicPath).ifPresent(pageBytes ->
                    coverImageView.setImage(new Image(
                            new ByteArrayInputStream(pageBytes),
                            160,
                            220,
                            true,
                            true
                    )));
        } catch (IOException exception) {
            // Leave the image empty if the archive cannot be read.
        }

        Label comicNameLabel = new Label(comicCard.getTitle());
        comicNameLabel.setWrapText(true);
        comicNameLabel.setMaxWidth(160);
        comicNameLabel.setAlignment(Pos.CENTER);

        Button favouriteButton = new Button();
        favouriteButton.getStyleClass().add("favourite-button");
        favouriteButton.setAccessibleText("Toggle favourite");
        updateFavouriteButton(favouriteButton, comicCard.isFavourite());
        favouriteButton.setOnMouseClicked(event -> event.consume());
        favouriteButton.setOnAction(event -> {
            comicCard.setFavourite(!comicCard.isFavourite());
            libraryState.setFavourite(
                    comicCard.getComicPath(),
                    comicCard.isFavourite()
            );
            updateFavouriteButton(favouriteButton, comicCard.isFavourite());
            event.consume();
        });

        VBox card = new VBox(8, coverImageView, comicNameLabel, favouriteButton);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setStyle("-fx-padding: 10; -fx-border-color: lightgray;");
        card.setOnMouseEntered(event ->
                card.setStyle("-fx-padding: 10; -fx-border-color: dodgerblue;"));
        card.setOnMouseExited(event ->
                card.setStyle("-fx-padding: 10; -fx-border-color: lightgray;"));
        card.setOnMouseClicked(event -> openComic(comicPath));
        return card;
    }

    private void updateFavouriteButton(Button button, boolean favourite) {
        button.setText(favourite ? "♥" : "♡");
        button.getStyleClass().remove("favourite-button-selected");
        if (favourite) {
            button.getStyleClass().add("favourite-button-selected");
        }
    }

    private ComicCard toComicCard(Path comicPath) {
        return new ComicCard(
                comicPath,
                getComicTitle(comicPath),
                libraryState.isFavourite(comicPath)
        );
    }

    private String getComicTitle(Path comicPath) {
        String fileName = comicPath.getFileName().toString();
        String extension = ".cbz";

        return fileName.toLowerCase(Locale.ROOT).endsWith(extension)
                ? fileName.substring(0, fileName.length() - extension.length())
                : fileName;
    }

    private void openComic(Path comicPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ComicCollectionController.class.getResource(
                            "../comic-reader-view.fxml"
                    )
            );
            Node readerView = loader.load();
            ComicReaderController readerController = loader.getController();
            readerController.setReaderState(readerState);
            readerController.loadComic(comicPath.toFile());

            getApplicationRoot().setCenter(readerView);
        } catch (IOException exception) {
            comicStatusLabel.setText("Could not open the selected comic.");
        }
    }

    private BorderPane getApplicationRoot() {
        return (BorderPane) comicGrid.getScene().getRoot();
    }

    private void loadSavedComics() {
        if (libraryState == null) {
            return;
        }

        folderReadError = false;
        List<Path> comicFiles = new ArrayList<>();
        for (Path folder : libraryState.getSelectedFolders()) {
            if (!Files.isDirectory(folder)) {
                folderReadError = true;
                continue;
            }

            try (var files = Files.list(folder)) {
                files.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName()
                                .toString()
                                .toLowerCase(Locale.ROOT)
                                .endsWith(".cbz"))
                        .forEach(comicFiles::add);
            } catch (IOException exception) {
                folderReadError = true;
            }
        }

        comicFiles.sort(Comparator.comparing(path ->
                        path.getFileName().toString(),
                String.CASE_INSENSITIVE_ORDER));
        libraryState.setComicFiles(comicFiles);
        libraryState.setComicsList(comicFiles.stream()
                .map(this::toComicCard)
                .toList());
    }
}
