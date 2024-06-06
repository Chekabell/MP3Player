package player;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.*;
import javafx.util.Duration;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.layout.TilePane;




public class MP3Player implements Initializable {

    @FXML
    private Label trackName,artistName,albumName,playlistName;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;

    private Media media;
    private MediaPlayer mediaPlayer;

    private Playlist playlistNow;

    private Timeline timeline;
    private boolean running;

    void updateInfo(){
        if(playlistNow.size()>0) {
            media = new Media(playlistNow.getTrackNow().toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            trackName.setText(playlistNow.getTrackNow().getTitle());
            artistName.setText(playlistNow.getTrackNow().getArtist());
            albumName.setText(playlistNow.getTrackNow().getAlbum());
            playlistName.setText(playlistNow.getName());
        }
    }

    @Override
    public void initialize(URL var1, ResourceBundle var2) {
        playlistNow = null;
        songProgressBar.setStyle("-fx-accent: #00FF00;");
    }

    public void playMedia(){
        if(playlistNow == null || playlistNow.size() < 1 ) return;
        beginTimer();
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void pauseMedia(){
        if(playlistNow == null || !running || playlistNow.size() < 1) return;
        cancelTimer();
        mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
        mediaPlayer.pause();
    }
    public void prevMedia(){
        if(playlistNow == null || playlistNow.size() < 2) return;
        mediaPlayer.stop();

        if (running){
            cancelTimer();
        }
        playlistNow.prev();
        updateInfo();
        playMedia();
    }
    public void nextMedia(){
        if(playlistNow == null || playlistNow.size() < 2) return;
        mediaPlayer.stop();
        if (running){
            cancelTimer();
        }

        playlistNow.next();
        updateInfo();
        playMedia();
    }

    public void beginTimer(){
        timeline = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current/end);
                if(current/end==1){
                    cancelTimerNext();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        songProgressBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseClick) {
                songProgressBar.setProgress(mouseClick.getSceneX()/songProgressBar.getWidth());
                mediaPlayer.seek(Duration.seconds(media.getDuration().toSeconds() * mouseClick.getSceneX()/songProgressBar.getWidth()));
            }
        });
    }

    public void cancelTimer(){
        running = false;
        timeline.stop();
    }

    public void cancelTimerNext(){
        running = false;
        timeline.stop();
        nextMedia();
    }

    public void loadPlaylist(){
        if(playlistNow != null)
            pauseMedia();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        System.out.println(new File("Playlists").getAbsoluteFile());
        directoryChooser.setInitialDirectory(new File("Playlists").getAbsoluteFile());
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            playlistNow = Playlist.loadPlaylist(selectedDirectory);
            if(playlistNow.size() > 0) {
                songProgressBar.setProgress(0);
                playlistNow.start();
                updateInfo();
                volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                    }
                });
            } else{
                playlistName.setText("Playlist is empty");
            }
        }
    }
    public void savePlaylist() throws IOException {
        if(playlistNow == null || playlistNow.size() < 1 ) return;
        pauseMedia();
        playlistNow.savePlaylist();
    }
    public void addTrack() throws TikaException, IOException, SAXException {
        if(playlistNow == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files","*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null)
            playlistNow.addTrack(selectedFile);
        if(playlistNow.size()==1){
            playlistNow.start();
            updateInfo();
        }
    }
    public void deleteTrack() {
        pauseMedia();
        if(playlistNow == null) return;
        if (playlistNow.size() > 1){
            pauseMedia();
            playlistNow.deleteTrack();
            updateInfo();
            playMedia();
        } else {
            songProgressBar.setProgress(0);
            mediaPlayer.stop();
            trackName.setText("Track name");
            artistName.setText("Artist name");
            albumName.setText("Album name");
        }
    }
    public void createPlaylist() throws TikaException, IOException, SAXException {
        if(playlistNow != null){
            pauseMedia();
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Creating new playlist");
        dialog.setHeaderText("Enter name new playlist");

        Optional<String> result = dialog.showAndWait();
        String entered = "none.";
        if (result.isPresent()) {
            entered = result.get();
            playlistNow = new Playlist(entered);
            addTrack();
            playlistNow.start();
            updateInfo();
            playlistName.setText(entered);
        }
    }
    public void deletePlaylist() throws IOException {
        pauseMedia();
        media = null;
        mediaPlayer = null;
        Playlist pl = playlistNow;
        playlistNow = null;
        pl.deletePlaylist();
        trackName.setText("Track name");
        artistName.setText("Artist name");
        albumName.setText("Album name");
        playlistName.setText("MP3 Player");
    }
}
