module comicportaldesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;


    opens comicportaldesktop to javafx.fxml;
    opens comicportaldesktop.controllers to javafx.fxml;
    exports comicportaldesktop;
}