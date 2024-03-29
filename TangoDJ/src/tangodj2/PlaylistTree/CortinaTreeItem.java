package tangodj2.PlaylistTree;

import javafx.scene.image.ImageView;
import tangodj2.cortina.CortinaTrack;

public class CortinaTreeItem extends BaseTreeItem
{
  CortinaTrack cortinaTrack;
  private String album;
  private String artist;
  private String path;
  private String pathHash;
  private int start;
  private int stop;
  private int delay;
  private int fadein;
  private int fadeout;
  private int original_duration;
  private int premade=0;
  
  public final static int PLAYING = 1;
  public final static int SELECTED = 2;
  public final static int NONE = 3;
  private int status=NONE;
  private double duration=0;
  
  
  public CortinaTreeItem(CortinaTrack cortinaTrack) 
  {
	  super();
	  this.setTreeType("cortina");
	  this.cortinaTrack=cortinaTrack;
      this.album=cortinaTrack.getAlbum();	
      this.artist=cortinaTrack.getArtist();
      this.path=cortinaTrack.getPath();
      this.pathHash=cortinaTrack.getPathHash();
      this.start=cortinaTrack.getStartValue();
      this.stop=cortinaTrack.getStopValue();
      this.delay=cortinaTrack.getDelay();
      this.fadein=cortinaTrack.getFadein();
      this.fadeout=cortinaTrack.getFadeout();
      this.original_duration=cortinaTrack.getOriginal_duration();
      this.premade=cortinaTrack.getPremade();
      
      if (premade==1) duration=cortinaTrack.getOriginal_duration();
      else duration = (stop-start);
      
	  setGraphic(new ImageView(gray_light));
	  setValue(cortinaTrack.getTitle());
  }

  public String getAlbum()
  {
    return album;
  }

  public String getTandaAndTrackPosition()
  {
    TandaTreeItem tandaTreeItem = (TandaTreeItem)getParent();
    PlaylistTreeItem playlistTreeItem = (PlaylistTreeItem)tandaTreeItem.getParent();
    int tandaIndex = playlistTreeItem.getTandaPosition(tandaTreeItem);
    int trackIndex = tandaTreeItem.getChildren().indexOf(this);
    return tandaIndex+","+trackIndex;
  }
  
  public void setAlbum(String album)
  {
    this.album = album;
  }
  
  public TandaTreeItem getTanda()
  {
    return (TandaTreeItem)getParent();
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public int getStatus()
  {
    return status;
  }

  public void setStatus(int status)
  {
    this.status = status;
  }

  public CortinaTrack getCortinaTrack()
  {
    return cortinaTrack;
  }

public String getPathHash() {
	return pathHash;
}

public int getStart() {
	return start;
}

public int getStop() {
	return stop;
}

public int getDelay() {
	return delay;
}

public int getFadein() {
	return fadein;
}

public int getFadeout() {
	return fadeout;
}

public int getOriginal_duration() {
	return original_duration;
}

public String getArtist() {
	return artist;
}

public int getPremade()
{
  return premade;
}

public void setPremade(int premade)
{
  this.premade = premade;
}

public double getDuration()
{
  return duration;
}

  

}
