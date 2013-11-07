package tangodj2.tango;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import tangodj2.Db;
import tangodj2.Playlist;
import tangodj2.SharedValues;
import tangodj2.TangoDJ2;
import tangodj2.PlaylistTree.TandaTreeItem;

public class TangoTable extends TableView<TangoTrack>
{
  //private TableView<TangoTrack> table = new TableView<TangoTrack>();
  private Playlist playlist;
  private SimpleStringProperty action = new SimpleStringProperty("nada");
  TableView<TangoTrack> tangoTable;
  private int tableIndex=-1;
  
  public final static ObservableList<TangoTrack> tangoTracksData = FXCollections.observableArrayList();
	 
  public TangoTable()
  {
	  
	  tangoTable=this;
	 
	 
	  Db.loadTangoTracks(tangoTracksData);
	  setupTable();
	  //System.out.println("TangoTable - tracks loaded: "+tangoTracksData.size());
	  /*
	  if (SharedValues.allTracksData.size()>0)
	  {
	    Track firstTrack = SharedValues.allTracksData.get(0);
	    if (type==TANGO)
	    SharedValues.selectedTangoPathHash.set(firstTrack.getPathHash());
	    if (type==CORTINA)
	    SharedValues.selectedCleanupPathHash.set(firstTrack.getPathHash());

	  }
	  */
  }
	 
  
  
  public static void reloadData()
  {
    Db.loadTangoTracks(tangoTracksData);
  }
	 
  /*
  public TableView<TangoTrack> getTable() 
  { 
    return this; 
  }
	*/
  private void setupTable() 
  {
    setupColumns();
    
    this.setItems(tangoTracksData);
  //  this.setPrefWidth(thisWidth);
  //  this.setMinWidth(thisWidth);
  //  this.setMaxWidth(thisWidth);
    this.setEditable(false);
	    
  //  this.setOnKeyReleased(keyEvent);
	    
   
  }
		
  private final class  MyCellFactory implements Callback<TableColumn,TableCell> 
  {
    private ContextMenu contextMenu;
    public MyCellFactory() 
    {
      contextMenu = setupContextMenu();
    }

    public TableCell call(TableColumn p) 
    {
      TableCell cell = new TableCell() {
              
      protected void updateItem(Object item, boolean empty) 
      {
        super.updateItem(item, empty);
        if(item != null) { setText(item.toString()); }
      }};
          
      cell.setContextMenu(contextMenu);
      /*
      // Right click
      if(menu != null) {
        cell.setContextMenu(menu);
      }
      // Double click
      if(click != null) {
         cell.setOnMouseClicked(click);
      }
      */
      return cell;
    }
  }
    
    private ContextMenu setupContextMenu()
    {
      MenuItem addToTanda = new MenuItem("Add To Tanda"); 
      MenuItem edit = new MenuItem("Edit");
      MenuItem play = new MenuItem("Play" );
      MenuItem delete = new MenuItem("Delete" );
      final ContextMenu tandaContextMenu = new ContextMenu();
      
      tandaContextMenu.setOnShowing(new EventHandler() 
      {
        public void handle(Event e) 
        {
          tableIndex=tangoTable.getSelectionModel().getSelectedIndex();
        }
      });
      
      tandaContextMenu.getItems().addAll(addToTanda, edit, play);
      addToTanda.setOnAction(new EventHandler() 
        { public void handle(Event t) { action.set("addToTanda"); }});
      edit.setOnAction(new EventHandler() 
        { public void handle(Event t) { action.set("edit"); } });
      play.setOnAction(new EventHandler() 
        { public void handle(Event t) { action.set("play"); }});
      delete.setOnAction(new EventHandler() 
        { public void handle(Event t) { action.set("delete"); }});
      
      return tandaContextMenu;
    } 
  
		  
	   private void setupColumns()
	   {
		   TableColumn titleCol = new TableColumn("Title");
		   titleCol.setMinWidth(100);
		   titleCol.setPrefWidth(150);
		   titleCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("title"));
		   titleCol.setCellFactory(new MyCellFactory());
		  
		  // ARTIST COLUMN 
		  TableColumn artistCol = new TableColumn("Artist");
		  artistCol.setMinWidth(50);
		  artistCol.setPrefWidth(100);
		  artistCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("artist"));
		  artistCol.setCellFactory(new MyCellFactory());
		  
		// SINGER COLUMN 
      TableColumn singerCol = new TableColumn("Singer");
      singerCol.setMinWidth(50);
      singerCol.setPrefWidth(80);
      singerCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("singer"));
      singerCol.setCellFactory(new MyCellFactory());
		 
		  // ALBUM COLUMN 
		  TableColumn albumCol = new TableColumn("Album");
		  albumCol.setMinWidth(50);
		  if (TangoDJ2.prefs.albumColWidth>50) albumCol.setPrefWidth(TangoDJ2.prefs.albumColWidth);
		  else albumCol.setPrefWidth(110);
		  albumCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("album"));
		  albumCol.setCellFactory(new MyCellFactory());
		  
		  albumCol.setVisible(TangoDJ2.prefs.albumCol);
		  albumCol.visibleProperty().addListener(new ChangeListener<Boolean>() 
		  {
        public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) 
        {
          TangoDJ2.prefs.albumCol=new_val;
          Db.updateTangoTableColumnVisible("albumCol", new_val);
       }});
		  
		  albumCol.widthProperty().addListener(new ChangeListener<Number>() 
		  {
        public void changed(ObservableValue<? extends Number> arg0,
                Number arg1, Number arg2) 
        {
          System.out.println("Col width changed: "+arg2);
          TangoDJ2.prefs.albumColWidth=arg2.doubleValue();
          Db.updateTangoTableColumnWidth("albumColWidth", arg2.doubleValue());
        }

    });
		   
		      
		  TableColumn genreCol = new TableColumn("Genre");
		  genreCol.setMinWidth(30);
		  genreCol.setPrefWidth(50);
		  genreCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("genre"));
		  genreCol.setCellFactory(new MyCellFactory());
		  
		  genreCol.setVisible(TangoDJ2.prefs.genreCol);
      genreCol.visibleProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) 
        {
          TangoDJ2.prefs.genreCol=new_val;
          Db.updateTangoTableColumnVisible("genreCol", new_val);
       }});
      
      TableColumn styleCol = new TableColumn("Style");
      styleCol.setMinWidth(30);
      styleCol.setPrefWidth(50);
      styleCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("style"));
      styleCol.setCellFactory(new MyCellFactory());
      
      styleCol.setVisible(TangoDJ2.prefs.styleCol);
      styleCol.visibleProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) 
        {
          TangoDJ2.prefs.styleCol=new_val;
          Db.updateTangoTableColumnVisible("styleCol", new_val);
       }});
		  
		      
		  TableColumn commentCol = new TableColumn("Comment");
		  commentCol.setMinWidth(50);
		  commentCol.setPrefWidth(100);
		  commentCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("comment"));
		  commentCol.setCellFactory(new MyCellFactory());
		  
		  commentCol.setVisible(TangoDJ2.prefs.commentCol);
      commentCol.visibleProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) 
        {
          TangoDJ2.prefs.commentCol=new_val;
          Db.updateTangoTableColumnVisible("commentCol", new_val);
       }});
		 		      
		  TableColumn durationCol = new TableColumn("Length");
		  durationCol.setMinWidth(50);
		  durationCol.setPrefWidth(50);
		  durationCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("duration"));
		  durationCol.setCellFactory(new MyCellFactory());
		  
		  durationCol.setVisible(TangoDJ2.prefs.lengthCol);
		  durationCol.visibleProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) 
        {
          TangoDJ2.prefs.lengthCol=new_val;
          Db.updateTangoTableColumnVisible("lengthCol", new_val);
       }});
		      
		  TableColumn yearCol = new TableColumn("Year");
		  yearCol.setMinWidth(50);
		  yearCol.setPrefWidth(50);
		  yearCol.setCellValueFactory(new PropertyValueFactory<TangoTrack, String>("track_year"));
		  yearCol.setCellFactory(new MyCellFactory());
		  
		  yearCol.setVisible(TangoDJ2.prefs.yearCol);
		  yearCol.visibleProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) 
        {
          TangoDJ2.prefs.yearCol=new_val;
          Db.updateTangoTableColumnVisible("yearCol", new_val);
       }});
		     
		  this.getColumns().addAll(titleCol, durationCol, yearCol, artistCol, singerCol, albumCol, genreCol, styleCol, commentCol);
		  this.setTableMenuButtonVisible(true);  
		  
	  }
		
	   /*
	  EventHandler<KeyEvent>keyEvent = new EventHandler<KeyEvent>() 
	  {
	    public void handle(KeyEvent ke) 
      {		    
	      if (ke.getCode()==KeyCode.ESCAPE) { System.out.println("Esc Pressed"); }
		 	  if (ke.getCode()==KeyCode.UP)     { System.out.println("UP Pressed");  }
			  if (ke.getCode()==KeyCode.DOWN)   { System.out.println("DOWN Pressed");}
			  if (ke.getCode()==KeyCode.RIGHT)  
			  {   	
			 	  if (playlist.getTandaCount()>0)
			  	{	 
			 	 	  if (SharedValues.selectedTanda==-1)	SharedValues.selectedTanda=0;
			 	 	  TandaTreeItem tandaTreeItem = playlist.getTanda(SharedValues.selectedTanda);
			 	 	  tandaTreeItem.addTrack(SharedValues.selectedTangoPathHash.get());
			 	 	}
			 	}
			}
	  };
*/
    public SimpleStringProperty getAction()
    {
      return action;
    }



    public int getTableIndex()
    {
      return tableIndex;
    }



	public TableView<TangoTrack> getTangoTable() {
		return tangoTable;
	}

      

}
