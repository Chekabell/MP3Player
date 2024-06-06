module player {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires org.apache.tika.core;
    requires org.apache.commons.io;
    requires org.apache.tika.parser.audiovideo;


    opens player to javafx.fxml;
    exports player;
}