package tangodj2;

import java.io.File;
import java.nio.file.Path;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;

import tangodj2.tango.TangoTable;
import tangodj2.tango.TangoTrack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MP3EditorDialog extends Stage
{
  TrackMeta trackMeta;
  TextField title   = new TextField("");
  TextField artist  = new TextField("");
  TextField album   = new TextField("");
  TextField year    = new TextField("");
  TextField genre   = new TextField("");
  TextField singer  = new TextField("");
  TextField comment = new TextField("");
  TextField rating  = new TextField("");
  TangoTrack ttrack;
  TangoTable ttable;
 // int idx=0;
  final ComboBox styleComboBox = new ComboBox();
  String style = "Tango";
  
  public MP3EditorDialog(TangoTrack tangoTrack,  TangoTable tangoTable)
  {
    this.ttrack=tangoTrack;
    this.ttable=tangoTable;
   // this.idx=index;
    
	  final int col[] = {0,1,2,3,4,5,6,7,8,9,10};
    final int row[] = {0,1,2,3,4,5,6,7,8,9,10};
    this.initModality(Modality.APPLICATION_MODAL); 
    
    trackMeta=Db.getTrackInfo(tangoTrack.getPathHash());
    title.setText(trackMeta.title);
    artist.setText(trackMeta.artist);
    album.setText(trackMeta.album);
    year.setText(trackMeta.track_year);
    genre.setText(trackMeta.genre);
    comment.setText(trackMeta.comment);
    // TODO implement style, singer, adjectives, rating (tango/vals etc)
    
    final ComboBox styleComboBox = new ComboBox();
    styleComboBox.getItems().addAll(
        "Tango",
        "Vals",
        "Milonga",
        "Chacarera",
        "Candombe",
        "Alternative"
    );   
    
    styleComboBox.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
             public void changed(ObservableValue<? extends String> ov, 
                 String old_val, String new_val) { style=new_val; } });

    styleComboBox.setValue("Tango");
    
    System.out.println("MP3Editor: "+trackMeta.title);
    Button okButton = new Button("OK");
	  GridPane gridPane = new GridPane();
    gridPane.setPadding(new Insets(10, 10, 10, 10));
    gridPane.setVgap(5);
    gridPane.setHgap(5);
    int labelRow=0;
    gridPane.add(new Label("Title: "),   col[0], row[labelRow++]);
    gridPane.add(new Label("Artist: "),  col[0], row[labelRow++]);
    gridPane.add(new Label("Album: "),   col[0], row[labelRow++]);
    gridPane.add(new Label("Year: "),    col[0], row[labelRow++]);
    gridPane.add(new Label("Genre: "),   col[0], row[labelRow++]);
    gridPane.add(new Label("Style: "),   col[0], row[labelRow++]);
    gridPane.add(new Label("Singer: "),  col[0], row[labelRow++]);
    gridPane.add(new Label("Comment: "), col[0], row[labelRow++]);
    gridPane.add(new Label("Rating: "),  col[0], row[labelRow++]);
    
    int fieldRow=0;
    gridPane.add(title,    col[1], row[fieldRow++]);
    gridPane.add(artist,   col[1], row[fieldRow++]);
    gridPane.add(album,    col[1], row[fieldRow++]);
    gridPane.add(year,     col[1], row[fieldRow++]);
    gridPane.add(genre,    col[1], row[fieldRow++]);
    gridPane.add(styleComboBox,    col[1], row[fieldRow++]);
    gridPane.add(singer,   col[1], row[fieldRow++]);
    gridPane.add(comment,  col[1], row[fieldRow++]);
    gridPane.add(rating,   col[1], row[fieldRow++]);
    gridPane.add(okButton,   col[1], row[fieldRow++]);
    
   // GridPane.setHalignment(handicapLabel, HPos.RIGHT);
    ColumnConstraints col0 = new ColumnConstraints();
    col0.setHalignment(HPos.RIGHT);
    gridPane.getColumnConstraints().add(col0);
    //gridPane.add(okButton, col[0], row[2]);
    
    okButton.setOnAction(new EventHandler<ActionEvent>()
    {
      public void handle(ActionEvent arg0) 
      {
       
        updateTrackMeta();
        updateTangoTableView();
        Db.updateTrack(trackMeta);
        updateMP3tag();
        close();	
      }});
    
    Scene myDialogScene = new Scene(gridPane, 300, 300);
    setScene(myDialogScene);
    show();
  } 
  
  private void updateTangoTableView()
  {
    ttrack.setTitle(trackMeta.title);
    ttrack.setArtist(trackMeta.artist);
    ttrack.setTrack_year(trackMeta.track_year);
    ttrack.setGenre(trackMeta.genre);
    ttrack.setComment(trackMeta.comment);
    ttrack.setStyle(trackMeta.style);
    ttrack.setSinger(trackMeta.singer);
    
    // this forces the table to update the row
    
    int numberOfColumns=ttable.getColumns().size();
    for(int i=0; i<numberOfColumns; i++)
    {
      if (ttable.getColumns().get(i).isVisible())
      {
        ttable.getColumns().get(i).setVisible(false);
        ttable.getColumns().get(i).setVisible(true);
      }
    }
  }
  
  private void updateTrackMeta()
  {
    trackMeta.title=title.getText();
    trackMeta.artist=artist.getText();
    trackMeta.album=album.getText();
    trackMeta.track_year=year.getText();
    trackMeta.genre=genre.getText();
    trackMeta.singer=singer.getText();
    trackMeta.comment=comment.getText();
    trackMeta.rating=rating.getText();
    trackMeta.style=style;
  }
  
  private void updateMP3tag() 
  {
    MP3File mp3 = null;
    AbstractID3v2 tag;
   
    File file = new File(trackMeta.path);
   
    String message;
      
    try { mp3= new MP3File(file); } catch (Exception e) 
    { 
      System.out.println(" Could not create MP3File class: "+trackMeta.path); 
      return;  
    }
    
    try { tag = mp3.getID3v2Tag();  } catch (Exception e2) 
    {  
      System.out.println(" Could not get ID3v2 tag: "+trackMeta.path); 
      return;  
    }
     
    if (tag==null)
    {
      System.out.println(" tag is null: "+trackMeta.path+"\n"); 
      return;  
    }
    
    tag.setSongTitle(trackMeta.title);
    tag.setLeadArtist(trackMeta.artist);
    tag.setAlbumTitle(trackMeta.album);
    tag.setYearReleased(trackMeta.track_year);
    tag.setSongGenre(trackMeta.genre);
    tag.setSongComment(trackMeta.comment);
   
    mp3.setID3v2Tag(tag);
    try
    {
      mp3.save();
    } catch (Exception e) { e.printStackTrace(); } 
  }
  
}