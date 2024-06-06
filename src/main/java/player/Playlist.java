package player;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Playlist {
	private final LinkedList<Track> tracks;
	private final String name;
	private static Track trackNow;
	public Playlist(String n) {
		tracks = new LinkedList<>();
		name = n;
		trackNow = null;
	}
	int size(){
		return tracks.size();
	}
	String getName() {return name;}
	Track getTrackNow() {return trackNow;}
	void addTrack(File file) throws IOException, TikaException, SAXException {
		Track t = new Track(file);
		tracks.add(t);
	}
	void deleteTrack() throws IndexOutOfBoundsException {
		Track track = trackNow;
		next();
		tracks.remove(track);
	}
	void start() throws RuntimeException{
		if(tracks.isEmpty())
			throw new RuntimeException ("Playlist is empty!");
		else
			trackNow = tracks.getFirst();
	}
	void next(){
		int i = tracks.indexOf(trackNow);

		if (trackNow == tracks.getLast())
			trackNow = tracks.getFirst();
		else
			trackNow = tracks.get(i + 1);
	}
	void prev(){
		int i = tracks.indexOf(trackNow);

		if (trackNow == tracks.getFirst())
			trackNow = tracks.getLast();
		else
			trackNow = tracks.get(i - 1);
	}
	public static Playlist loadPlaylist(File directory){
		Playlist playlist = new Playlist(directory.getName());
		File[] files = directory.listFiles();
		if(files != null){
			for(File file : files) {
				try {
					playlist.addTrack(file);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TikaException e) {
					throw new RuntimeException(e);
				} catch (SAXException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return playlist;
	}
	void savePlaylist() throws IOException {
		Path directory = Paths.get("Playlists/" + name);
		if (Files.exists(directory)) {
			System.gc();
			List<File> files = Arrays.asList(Objects.requireNonNull(directory.toFile().listFiles()));
			if (!files.isEmpty()) {
				boolean flag;
				for (File file : files) {
					if (file.getName().equals(trackNow.getLink().getName())) continue;
					flag = true;
					for (Track track : tracks) {
						if (track.getLink().getName().equals(file.getName())) {
							flag = false;
							break;
						}
					}
					if (flag) FileDeleteStrategy.FORCE.delete(file);
				}
			}
		} else {
			Files.createDirectory(directory);
		}
        for (Track track : tracks) {
			if(Files.notExists(Path.of(directory + "\\" +track.getLink().getName())))
				Files.copy(track.getLink().toPath(), Path.of(directory + "\\" +track.getLink().getName()));
		}
	}

	void deletePlaylist() throws IOException {
		trackNow = null;
		System.gc();
		File directory = new File("Playlists/" + name);
		File[] files = directory.listFiles();
        assert files != null;
        for(File file: files){
			FileDeleteStrategy.FORCE.delete(file);
		}
		directory.delete();
	}

}
