module com.example.granny_gains_new {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.granny_gains_new to javafx.fxml;
    exports com.example.granny_gains_new;
}