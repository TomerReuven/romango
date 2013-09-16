package tangodj2.test;

import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;

public class TrackTreeItem extends TreeItem<Text>
{
  private String trackHash;
  private int type = 0;;

  public TrackTreeItem(Text text)
   {
	 super(text);
   }

public String getTrackHash() {
	return trackHash;
}

public void setTrackHash(String trackHash) {
	this.trackHash = trackHash;
}

public int getType() {
	return type;
}

public void setType(int type) {
	this.type = type;
}
}
