package comicportaldesktop;

import comicportaldesktop.state.ComicLibraryState;
import comicportaldesktop.state.ComicReaderState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ComicPortalApplication extends Application {
    private final ComicLibraryState libraryState =
            new ComicLibraryState();


    private final ComicReaderState readerState =
            new ComicReaderState();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ComicPortalApplication.class.getResource("main-view.fxml")
        );

        BorderPane root = loader.load();

        HomeController controller = loader.getController();
        controller.setLibraryState(libraryState);
        controller.setReaderState(readerState);

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }
}
