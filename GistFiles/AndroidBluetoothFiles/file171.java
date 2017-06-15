package linz.jku;

import android.net.Uri;

/**
 * Data with the information of a MP3 song
 *
 */
public class Mp3Data {
	private String name;
	private String track;
	private String album;
	private Uri uri;
		
	/**
	 * Constructor need the name, track number, album and uri of the song
	 * @param name
	 * @param track
	 * @param album
	 * @param uri
	 */
	public Mp3Data(String name, String track, String album, Uri uri){
	
		this.name = name;
		this.track = track;
		this.album = album;
		this.setUri(uri);
	}
	
	
	/**
	 * Return the name of the song
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the song
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the track of the song
	 * @return string with the track
	 */
	public String getTrack() {
		return track;
	}

	/**
	 * Set the track of a song
	 * @param track
	 */
	public void setTrack(String track) {
		this.track = track;
	}

	/**
	 * Return the album of a song
	 * @return string with the name of album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * Set the album of the song
	 * @param album
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * Return the uri associated of a song
	 * @return uri
	 */
	public Uri getUri() {
		return uri;
	}

	/**
	 * Set the uri of a song
	 * @param uri
	 */
	public void setUri(Uri uri) {
		this.uri = uri;
	}

	
}
