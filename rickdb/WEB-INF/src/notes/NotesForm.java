package notes;

import java.sql.Date;

import org.apache.struts.action.ActionForm;

public class NotesForm extends ActionForm
{
  private static final long serialVersionUID = 4232555499356099479L;
  private long id;
  private Date note_date;
  private String summary;
  private String note;
  private long track;
  private long cust_id; 
  private int priority;
  
  private String mode;
  private String website;
  private long order_id;
  




public long getOrder_id() {
	return order_id;
}

public void setOrder_id(long orderId) {
	order_id = orderId;
}

public String getWebsite()
  {
    return website;
  }

  public void setWebsite(String website)
  {
    this.website = website;
  }

public String getMode()
  {
    return mode;
  }

  public void setMode(String mode)
  {
    this.mode = mode;
  }

public long getId(){
   return this.id;
}

public void setId( long param ){
   this.id= param;
}
public Date getNote_date(){
   return this.note_date;
}

public void setNote_date( Date param ){
   this.note_date= param;
}
public String getSummary(){
   return this.summary;
}

public void setSummary( String param ){
   this.summary= param;
}
public String getNote(){
   return this.note;
}

public void setNote( String param ){
   this.note= param;
}
public long getTrack(){
   return this.track;
}

public void setTrack( long param ){
   this.track= param;
}
public long getCust_id(){
   return this.cust_id;
}

public void setCust_id( long param ){
   this.cust_id= param;
}

public String toString(){
       return  " [id] " + id + " [note_date] " + note_date + " [summary] " + summary + " [note] " + note + " [track] " + track + " [cust_id] " + cust_id;
}


public int getPriority()
{
  return priority;
}

public void setPriority(int priority)
{
  this.priority = priority;
}

}
