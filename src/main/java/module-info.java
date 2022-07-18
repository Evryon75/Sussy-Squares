module ene_colombetti_amto.sussysquares {
    requires javafx.controls;
    requires javafx.fxml;


    opens ene_colombetti_amto.sussysquares to javafx.fxml;
    exports ene_colombetti_amto.sussysquares;
}