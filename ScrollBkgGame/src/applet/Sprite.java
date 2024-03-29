/*********************************************************
 * Beginning Java Game Programming, 2nd Edition
 * by Jonathan S. Harbour
 * Sprite class
 **********************************************************/
package applet;
import java.awt.*;
import java.applet.*;

public class Sprite extends Object {
    private ImageEntity entity;
    protected Point2D pos;
    protected Point2D vel;
    protected double rotRate;
    protected int currentState;
    protected int sprType;
    protected boolean _collided;
    protected int _lifespan, _lifeage;
    protected long startTime;
    protected long timedEventSpan;
    private boolean stopped=false;
    
    private int lane=0;
    private int direction;
    private int currentCrossing=-1;
    private int sidewalk=0;
    private int row;
     
 	public int getRow() {
 	return row;
 }

 public void setRow(int row) {
 	this.row = row;
 }

 	public int getSidewalk() {
 	return sidewalk;
 }

 public void setSidewalk(int sidewalk) {
 	this.sidewalk = sidewalk;
 }

 	public int getCurrentCrossing() {
 	return currentCrossing;
 }

 public void setCurrentCrossing(int currentCrossing) {
 	this.currentCrossing = currentCrossing;
 }

 	public int getDirection() {
 	return direction;
 }

 public void setDirection(int direction) {
 	this.direction = direction;
 }

 	public int getLane() {
 	return lane;
 }

 public void setLane(int lane) {
 	this.lane = lane;
 }

 	
    
    public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	protected long collideTime=0;
    
    protected double maxX=0;

    public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	

	public void setTimedEventSpan(long timedEventSpan) {
		this.timedEventSpan = timedEventSpan;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public boolean isTimedEventExpired()
	{
	  boolean timeExpired=false;
	  long currentTime=System.currentTimeMillis();
	  if (currentTime>startTime+timedEventSpan) timeExpired=true;
	  return timeExpired;
	}
	//constructor
    Sprite(Applet a, Graphics2D g2d) {
        entity = new ImageEntity(a);
        entity.setGraphics(g2d);
        entity.setAlive(false);
        pos = new Point2D(0, 0);
        vel = new Point2D(0, 0);
        rotRate = 0.0;
        currentState = 0;
        _collided = false;
        _lifespan = 0;
        _lifeage = 0;
        maxX=pos.X();
    }

    //load bitmap file
    public void load(String filename) {
        entity.load(filename);
    }

    //perform affine transformations
    public void transform() {
        entity.setX(pos.X());
        entity.setY(pos.Y());
        entity.transform();
    }

    //draw the image
    public void draw() {
        entity.g2d.drawImage(entity.getImage(),entity.at,entity.applet);
    }

    //draw bounding rectangle around sprite
    public void drawBounds(Color c) {
    	entity.g2d.setStroke(new BasicStroke(1));
        entity.g2d.setColor(c);
        entity.g2d.draw(getBounds());
    }

    //update the position based on velocity
    public void updatePosition() {
        pos.setX(pos.X() + vel.X());
        pos.setY(pos.Y() + vel.Y());
        incrementMaxX(pos.X());
    }

    //methods related to automatic rotation factor
    public double rotationRate() { return rotRate; }
    public void setRotationRate(double rate) { rotRate = rate; }
    public void updateRotation() {
        setFaceAngle(faceAngle() + rotRate);
        if (faceAngle() < 0)
            setFaceAngle(360 - rotRate);
        else if (faceAngle() > 360)
            setFaceAngle(rotRate);
    }

    //generic sprite state variable (alive, dead, collided, etc)
    public int state() { return currentState; }
    public void setState(int state) { currentState = state; }

    //returns a bounding rectangle
    public Rectangle getBounds() { return entity.getBounds(); }

    //sprite position
    public Point2D position() { return pos; }
    public void setPosition(Point2D pos) { this.pos = pos; incrementMaxX(pos.X()); }

    //sprite movement velocity
    public Point2D velocity() { return vel; }
    public void setVelocity(Point2D vel) { this.vel = vel; }

    //returns the center of the sprite as a Point2D
    public Point2D center() {
        return(new Point2D(entity.getCenterX(),entity.getCenterY()));
    }

    //generic variable for selectively using sprites
    public boolean alive() { return entity.isAlive(); }
    public void setAlive(boolean alive) { entity.setAlive(alive); }

    //face angle indicates which direction sprite is facing
    public double faceAngle() { return entity.getFaceAngle(); }
    public void setFaceAngle(double angle) {
        entity.setFaceAngle(angle);
    }
    public void setFaceAngle(float angle) {
        entity.setFaceAngle((double) angle);
    }
    public void setFaceAngle(int angle) {
        entity.setFaceAngle((double) angle);
    }

    //move angle indicates direction sprite is moving
    public double moveAngle() { return entity.getMoveAngle(); }
    public void setMoveAngle(double angle) {
        entity.setMoveAngle(angle);
    }
    public void setMoveAngle(float angle) {
        entity.setMoveAngle((double) angle);
    }
    public void setMoveAngle(int angle) {
        entity.setMoveAngle((double) angle);
    }

    //returns the source image width/height
    public int imageWidth() { return entity.width(); }
    public int imageHeight() { return entity.height(); }

    //check for collision with a rectangular shape
    public boolean collidesWith(Rectangle rect) {
        return (rect.intersects(getBounds()));
    }
    //check for collision with another sprite
    public boolean collidesWith(Sprite sprite) {
        return (getBounds().intersects(sprite.getBounds()));
    }
    //check for collision with a point
    public boolean collidesWith(Point2D point) {
        return (getBounds().contains(point.X(), point.Y()));
    }

    public Applet applet() { return entity.applet; }
    public Graphics2D graphics() { return entity.g2d; }
    public Image image() { return entity.image; }
    public void setImage(Image image) { entity.setImage(image); }

    public int spriteType() { return sprType; }
    public void setSpriteType(int type) { sprType = type; }

    public boolean collided() { return _collided; }
    public void setCollided(boolean collide) 
    { 
      _collided = collide; 
      collideTime=System.currentTimeMillis(); 
      
    }

    public int lifespan() { return _lifespan; }
    public void setLifespan(int life) { _lifespan = life; }
    public int lifeage() { return _lifeage; }
    public void setLifeage(int age) { _lifeage = age; }
    public void updateLifetime() {
        //if lifespan is used, it must be > 0
        if (_lifespan > 0) {
            _lifeage++;
            if (_lifeage > _lifespan) {
                setAlive(false);
                _lifeage = 0;
            }
        }
    }
    
    void incrementMaxX(double x)
    {
      if (x>maxX) maxX=x;
    }
    
  

}
