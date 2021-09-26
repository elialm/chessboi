module org.efac {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens org.efac.ui to javafx.fxml;
    
    requires guava;
    requires com.github.oshi;

    exports org.efac;
    exports org.efac.ui;
}