package data;

/**
 * TrackData stores information about a song track.
 *
 * @author	Robbie Hanson
 * @version	1.0
 */
public class ItunesTrackData
{
	/**
	 * Name of the song (song title)
	 */
	public String name;

	/**
	 * Artist of the song
	 */
	public String artist;
	
	/**
	 * File path of the song
	 */
	public String path;
	
	public String grouping;
	
	/**
	 * Total time of the song (in milliseconds)
	 */
	public int time;

	/**
	 * Initializes empty values for name and artist.
	 *
	 * I have come across times where the iTunes xml file does not
	 * contain certain keys.  I have noticed that sometimes when no
	 * name or artist is set, these keys are absent from the file.
	 * Thus, a constructor is needed to avoid null references.
	 * I have never seen a case where path or time is missing.
	 * Thus I avoid adding defaults for these in the interest of performance.
	 */
	public ItunesTrackData()
	{
		name = "";
		artist = "";
	}
}
