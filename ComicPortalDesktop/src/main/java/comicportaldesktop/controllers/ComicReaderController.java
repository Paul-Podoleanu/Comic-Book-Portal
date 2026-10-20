package comicportaldesktop.controllers;

import comicportaldesktop.reader.CbzReader;
import comicportaldesktop.state.ComicLibraryState;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;
import comicportaldesktop.state.ComicReaderState;
import java.nio.file.Path;


public class ComicReaderController {
    @FXML
    private Label selectedComicLabel;
    @FXML
    private ImageView firstPageImageView;

    private final CbzReader cbzReader = new CbzReader();
    private List<byte[]> pages = List.of();
    private int currentPageIndex = 0;
    private boolean keyboardNavigationInstalled;
    private ComicReaderState readerState;
    private Path currentComicPath;


    public void setReaderState(ComicReaderState readerState) {
        this.readerState = readerState;
    }

    @FXML
    private void selectComic(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a CBZ comic");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Comic Book Archive (*.cbz)",
                        "*.cbz"
                )
        );

        Node source = (Node) event.getSource();
        Window window = source.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(window);

        // The user may close the dialog without selecting anything.
        if (selectedFile != null) {
            selectedComicLabel.setText(selectedFile.getName());
            loadComic(selectedFile);
        }
    }

    private void showFirstPage(File selectedFile) {
        try {
            Optional<byte[]> firstPage =
                    cbzReader.getFirstPage(selectedFile.toPath());

            if (firstPage.isEmpty()) {
                firstPageImageView.setImage(null);
                return;
            }

            Image image = new Image(
                    new ByteArrayInputStream(firstPage.get())
            );

            if (image.isError()) {
                firstPageImageView.setImage(null);
                return;
            }

            firstPageImageView.setImage(image);
        } catch (IOException exception) {
            firstPageImageView.setImage(null);
        }
    }

    public void loadComic(File selectedFile) {
        selectedComicLabel.setText(selectedFile.getName());
        currentComicPath = selectedFile.toPath()
                .toAbsolutePath()
                .normalize();

        if (readerState != null) {
            readerState.setComicPath(currentComicPath);
        }

        try {
            pages = cbzReader.getPages(currentComicPath);

            if (pages.isEmpty()) {
                currentPageIndex = 0;
                firstPageImageView.setImage(null);
                return;
            }

            currentPageIndex = readerState == null
                    ? 0
                    : readerState.getPageIndex(currentComicPath);

            currentPageIndex = Math.max(
                    0,
                    Math.min(currentPageIndex, pages.size() - 1)
            );

            displayCurrentPage();
            installKeyboardNavigationWhenReady();
        } catch (IOException exception) {
            pages = List.of();
            currentPageIndex = 0;
            firstPageImageView.setImage(null);
        }
    }

    private void displayCurrentPage() {
        if (pages.isEmpty()) {
            firstPageImageView.setImage(null);
            return;
        }

        Image image = new Image(
                new ByteArrayInputStream(pages.get(currentPageIndex))
        );

        firstPageImageView.setImage(image);
    }

    private void saveCurrentPage() {
        if (readerState != null && currentComicPath != null) {
            readerState.setPageIndex(
                    currentComicPath,
                    currentPageIndex
            );
        }
    }

    private void installKeyboardNavigationWhenReady() {
        if (keyboardNavigationInstalled) {
            requestReaderFocus();
            return;
        }

        if (firstPageImageView.getScene() == null) {
            firstPageImageView.sceneProperty().addListener(
                    (observable, oldScene, newScene) -> {
                        if (newScene != null) {
                            installKeyboardNavigation();
                        }
                    });
            return;
        }

        installKeyboardNavigation();
    }

    private void installKeyboardNavigation() {
        if (keyboardNavigationInstalled) {
            requestReaderFocus();
            return;
        }

        firstPageImageView.getScene().addEventFilter(
                KeyEvent.KEY_PRESSED,
                this::handleKeyPressed
        );
        keyboardNavigationInstalled = true;
        requestReaderFocus();
    }

    private void requestReaderFocus() {
        if (firstPageImageView.getScene() != null) {
            firstPageImageView.getScene().getRoot().requestFocus();
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        if (pages.isEmpty()) {
            return;
        }

        if (event.getCode() == KeyCode.RIGHT
                && currentPageIndex < pages.size() - 1) {
            currentPageIndex++;
            saveCurrentPage();
            displayCurrentPage();
            event.consume();
        } else if (event.getCode() == KeyCode.LEFT
                && currentPageIndex > 0) {
            currentPageIndex--;
            saveCurrentPage();
            displayCurrentPage();
            event.consume();
        }
    }
}