package player;

import org.apache.tika.metadata.*;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.exception.*;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.parser.mp3.Mp3Parser;


import java.io.*;
import java.net.URI;

public class Track {
	private final File link;
	private String album;
	private String artist;
	private String title;

	public Track(File mp3file) throws IOException, TikaException, SAXException {
		link = mp3file;
		InputStream input = new FileInputStream(mp3file);
		ContentHandler handler = new DefaultHandler();
		Metadata metadata = new Metadata();
		Parser parser = new Mp3Parser();
		ParseContext parseCtx = new ParseContext();
		parser.parse(input, handler, metadata, parseCtx);
		input.close();

		title = metadata.get("dc:title");
		artist = metadata.get("xmpDM:artist");
		album = metadata.get("xmpDM:album");
	}
	File getLink(){
		return link;
	}
    String getAlbum() {
		return album;
	}
	String getArtist() {
		return artist;
	}
	String getTitle() {
		return title;
	}
	public URI toURI(){
		return link.toURI();
	}
}
