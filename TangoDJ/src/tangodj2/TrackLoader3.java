package tangodj2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;

import tangodj2.cleanup.CleanupTable;
import tangodj2.tango.TangoTable;




public class TrackLoader3 
{
//  String inPath="C:\\music\\tango\\Di Sarli, Carlos";
  static int fileCounter=0;
  static int counter2=0;
  ArrayList<File> fileList;
 // ArrayList<ProblemFile> problemFileList;
  private static List<TrackDb> trackInfo = new ArrayList<TrackDb>();
  
 // private static List<TrackDb> finalTrackInfo = new ArrayList<TrackDb>();
  private static Hasher hasher = new Hasher();
  int errors=0;
  BufferedWriter out;
  private int trackCount=0;
  private int trackSize=0;
 
  //ArrayList<String> al;
  //static int players=0;
  //int trackInfoSizeInt=0;
 // Label trackInfoSize = new Label("0");
  //int finalTrackInfoSizeInt=0;
  //Label finalTrackInfoSize = new Label("0");
  public static final String DRIVER2 ="org.apache.derby.jdbc.EmbeddedDriver";
  public static final String JDBC_URL2 ="jdbc:derby:tango_db;create=false";
  static Connection connection;
  private boolean isTango=true;
  
  TangoTable tangoTable;
  CleanupTable cleanupTable;
  
  public TrackLoader3() { }
  
  public void process(String inPath, boolean singleFile, boolean isTango) throws Exception
  {
     this.isTango=isTango;
      fileCounter=0;
     File outFile = new File("out.txt");
     if (outFile.exists()) outFile.delete();
     FileWriter fstream = new FileWriter("out.txt");
     out = new BufferedWriter(fstream);
     fileList = new ArrayList<File>();
    // problemFileList = new ArrayList<ProblemFile>();
     
     if (singleFile)
     {
       File f = new File(inPath);
       processSingleFile(f.toPath());
     }
     else
     {
       FileVisitor<Path> fileProcessor = new ProcessFile();
       Files.walkFileTree(Paths.get(inPath), fileProcessor);
     }   
     
     getFarngMP3Tags();
     out.close();
   //  System.out.println("Problems list: "+problemFileList.size());
   //  System.out.println("OK list: "+fileList.size());
     getDurations();
     
  }

  void processDirectory()
  {
    FileVisitor<Path> fileProcessor = new ProcessFile();
  }
  
  public  FileVisitResult processSingleFile(Path path) throws Exception
  {
    String pathStr = path.toString().trim().toLowerCase();
    String pathStr2="";
    
    
    if (pathStr.endsWith(".mp3")) 
    {
      if  (!pathStr.contains("._"))
      {
        File file = path.toFile();
        pathStr2=path.toString();
        TangoDJ2.feedback.setText("Preparing Files: "+fileCounter+" - "+pathStr2);
        fileList.add(file);
      }
    }
    fileCounter++;
    return FileVisitResult.CONTINUE;
  }
  
 private  final class ProcessFile extends SimpleFileVisitor<Path> 
 {
   public FileVisitResult visitFile(Path path, BasicFileAttributes aAttrs) throws IOException 
   {
     try {
     return processSingleFile(path);
     } catch (Exception e) { e.printStackTrace(); }
     return null;
   }
 }
       
   public FileVisitResult preVisitDirectory(Path aDir, BasicFileAttributes aAttrs) throws IOException 
   {
   System.out.println("Processing directory:" + aDir);
   return FileVisitResult.CONTINUE;
   }
  
  
  
  private void getDurations()
  {
    Iterator<TrackDb> it = trackInfo.iterator();
    Media media;
    final int count=0;
    trackCount=0;
    
    int trackSize=trackInfo.size();
    System.out.println("TrackInfo Size: "+trackSize);
    
    Timeline timeline = new Timeline();
    timeline.setCycleCount(trackSize);
    KeyFrame keyFrame= new KeyFrame(Duration.seconds(.3), new EventHandler() 
    {
       public void handle(Event event) 
       {
         final TrackDb trackDb = trackInfo.get(trackCount);
         getTags(trackDb, trackCount);
         trackCount++;
       }});
            
      timeline.getKeyFrames().add(keyFrame);
      timeline.playFromStart();
      timeline.setOnFinished(new EventHandler() {

        @Override
        public void handle(Event arg0)
        {
          sqlReadyTrackInfo();
          insertRecords();
          if (isTango) { tangoTable.reloadData(); }
          else cleanupTable.reloadData();
          
        }});
  }
  
  
  private void getTags(final TrackDb trackDb, final int count)
  {
    File file;
    final Media media;
    file = new File(trackDb.path);
    try 
    {
      media = new Media(file.toURI().toString());
      media.setOnError(new Runnable() 
      {
        public void run() {
        System.out.println("MEDIA ERROR: "+media.getError()); }});
      } catch (Exception e) {  System.out.println("Problem with media, "+trackDb.path);  return; }
    
    if (media==null) { System.out.println("Media is null, "+trackDb.path);  return; }
    
    final MediaPlayer mp = new MediaPlayer(media);
    //players++;
    mp.setOnError(new Runnable() 
    {
      public void run() {
      System.out.println("MEDIA PLAYER ERROR: "+mp.getError());
    }});
    
    if (mp==null) { System.out.println("MediaPlayer is null, "+trackDb.path); return; }
    mp.setOnReady(new Runnable() 
    {
      public void run() 
      {
        trackDb.duration=(int)mp.getTotalDuration().toMillis();
        System.out.println("duration: "+trackDb.duration);
        if ("NO TITLE".equals(trackDb.title))
        {
          trackDb.artist=(String)media.getMetadata().get("artist");
          trackDb.album=(String)media.getMetadata().get("album");
          trackDb.title=(String)media.getMetadata().get("title");
          trackDb.comment=(String)media.getMetadata().get("comment-0");
          trackDb.genre=(String)media.getMetadata().get("genre");
          trackDb.track_year = (String)media.getMetadata().get("year");
        }
        
        TangoDJ2.feedback.setText("Getting MP3 Tags: "+count+") "+trackDb.duration+", "+trackDb.title);
        //finalTrackInfo.add(trackDb);
        //finalTrackInfoSizeInt++;
        // finalTrackInfoSize.setText(""+finalTrackInfoSizeInt);
        mp.dispose();
        //players--;
       
      }
    });
    
  }
  
  private void getFarngMP3Tags() throws Exception
  {
    errors=0;
    counter2=0;
    out.write("GET FARNG MP3 TAGS =====================");
    out.newLine();
    Iterator<File> it = fileList.iterator();
    trackInfo.clear();
    //trackInfoSizeInt=0;
    File file;
    TrackDb trackDb;
    while(it.hasNext())
    {
      file = it.next();
      trackDb=getSingleMP3tag(file.toPath());
      
      if (trackDb!=null) 
      {  
        trackInfo.add(trackDb);
       // trackInfoSizeInt++;
        TangoDJ2.feedback.setText("Getting preliminary MP3 tags: "+counter2+") "+trackDb.artist+", "+trackDb.title);
      }
    }
   
  }
  
  private TrackDb getSingleMP3tag(Path path) throws Exception
  {
    String pathStr = path.toString().trim().toLowerCase();
    String pathStr2="";
    String title="";
    String artist="";
    String album="";
    String comment="";
    String genre="";
    String track_year="";
  
    String pathHash="";
    MP3File mp3 = null;
    AbstractID3v2 tag;
    AbstractID3v1 tag2;

    File file = path.toFile();
    pathStr2=path.toString();
    String message;
      
    try { mp3= new MP3File(file); } catch (Exception e) 
    { 
      errors++; 
      message="Could not create MP3File class: "+pathStr;
      out.write("Could not create MP3File class: "+pathStr);
      out.newLine();
      counter2++; 
   //   problemFileList.add(new ProblemFile(file, message));
      return null;  
    }
    
    try { tag = mp3.getID3v2Tag();  } catch (Exception e2) 
    {  errors++; 
      out.write("Could not get ID3v2 tag: "+pathStr);
      out.newLine();
      counter2++; 
      message="Could not get ID3v2 tag: "+pathStr;
    //  problemFileList.add(new ProblemFile(file, message));
      return null;  
    }
     
    if (tag==null)
    {
      errors++; 
      out.write("tag is null: "+pathStr);
      out.newLine();
      counter2++; 
      message="tag is null: "+pathStr;
    //  problemFileList.add(new ProblemFile(file, message));
      return null;  
    }
      
    title=tag.getSongTitle();
    comment= tag.getSongComment();
    genre=tag.getSongGenre();
    artist=tag.getLeadArtist();
   
    album=tag.getAlbumTitle();
    track_year=tag.getYearReleased();
      //System.out.println(track_year);
    
    if (artist==null) artist="NO ARTIST";
    else if (artist.trim().length()==0)  artist="NO ARTIST";
    if (title==null) title="NO TITLE";
    else if (title.trim().length()==0)  title="NO TITLE";
    
   
    pathStr2=path.toString();
              
    pathHash = hasher.getMd5Hash(pathStr2.getBytes());
    counter2++;
    tag=null;
    return new TrackDb(cleanString(title), 
        cleanString(artist),
        cleanString(album),
        cleanString(comment), 
        cleanString(genre), 
        pathHash, 
        cleanString(pathStr2), 
        cleanString(track_year));
  }
  
  public static String cleanString(String inStr)
  {
    // String returnStr = inStr.replace("'","''");
    String returnStr = inStr.replace("��","");
    // returnStr = returnStr.replace("\\","\\\\");
     
     char tChar=0;
     returnStr = removeChar(returnStr, tChar);
     
     return returnStr;
  }
  
  public static String removeChar(String s, char c) 
  {
  String r = "";
  for (int i = 0; i < s.length(); i ++) 
  {
    if (s.charAt(i) != c) r += s.charAt(i);
  }
  return r;
  }
  
  void insertRecords() 
  {
   int cleanup=0;  // TODO set this on load
   if (!isTango) cleanup=1;
 try{
   //Db.connect();
    connection = DriverManager.getConnection(JDBC_URL2);
    TrackDb trackDb;
    String styleGuess="Tango";
    String lowerTitle;
    String lowerGenre;
    Iterator<TrackDb> it = trackInfo.iterator();
    while(it.hasNext())
    {
      styleGuess="Tango";
      trackDb=it.next();
      lowerTitle=trackDb.title.toLowerCase();
      lowerGenre=trackDb.genre.toLowerCase();
      if (lowerTitle.contains("milonga")) styleGuess="Milonga";
      if (lowerTitle.contains("vals")) styleGuess="Vals";
      if (lowerGenre.contains("milonga")) styleGuess="Milonga";
      if (lowerGenre.contains("vals")) styleGuess="Vals";
      
      String sql="insert into tracks(cleanup, path, pathHash, title, artist, album, duration, genre, comment, style, track_year) "
       +"values ("+cleanup+", '"+trackDb.path
               +"', '"+trackDb.pathHash
               +"', '"+trackDb.title
               +"', '"+trackDb.artist
               +"', '"+trackDb.album
               +"', "+trackDb.duration
               +", '"+trackDb.genre
               +"', '"+trackDb.comment
               +"', '"+styleGuess
               +"', '"+trackDb.track_year
               +"')";
   // System.out.println("TrackLoader3, sql: "+sql);
   TangoDJ2.feedback.setText("Inserting Records: "+trackDb.title);
    try {
      // TODO java.sql.SQLIntegrityConstraintViolationException needs to be handled here
    if (isSet(trackDb.title)) connection.createStatement().execute(sql);
    } catch (Exception ex) { System.out.println("SQL ERROR: "+sql); 
    ex.printStackTrace(); } 
   }
   connection.close(); 
  // Db.disconnect();
 } catch (Exception e) { e.printStackTrace(); }
}
  
  private boolean isSet(String inStr)
  {
     if (inStr==null) return false;
     if (inStr.length()==0) return false;
     return true;
  }
 void sqlReadyTrackInfo()
 {
   TrackDb trackDb;
   Iterator<TrackDb> it = trackInfo.iterator();
   while(it.hasNext())
   {
   trackDb=it.next();
   trackDb.title    = sqlReadyString(trackDb.title);
   trackDb.artist   = sqlReadyString(trackDb.artist);
   trackDb.album    = sqlReadyString(trackDb.album);
   trackDb.comment  = sqlReadyString(trackDb.comment);
   trackDb.path     = sqlReadyString(trackDb.path);
   trackDb.genre     = sqlReadyString(trackDb.genre);
   //trackDb.path = new File(trackDb.path).toURI().toString();
   trackDb.track_year     = sqlReadyString(trackDb.track_year);
  }
    
 }
 
 public static String sqlReadyString(String inStr)
 {
    String returnStr = inStr.replace("'","''");
    returnStr = returnStr.replace("��","");
   // returnStr = returnStr.replace("\\","\\\\");
    
    char tChar=0;
    returnStr = removeChar(returnStr, tChar);
    
    return returnStr;
 }
 
 public void setTangoTable(TangoTable tangoTable)
 {
   this.tangoTable = tangoTable;
 }
 
 public void setCleanupTable(CleanupTable cleanupTable)
 {
   this.cleanupTable = cleanupTable;
 }
}