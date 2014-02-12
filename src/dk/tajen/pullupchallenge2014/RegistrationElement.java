/**
 * 
 */
package dk.tajen.pullupchallenge2014;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Tajen
 *
 */
public class RegistrationElement {

	public int Id;
	public int Count;
	public Date Registrated;
	
	public RegistrationElement() 
	{ 
		this.Registrated = new Date();
	}
	public RegistrationElement(int count, Date now)
	{
		this.Count = count;
		this.Registrated = now;
	}
	
	public String getDateTime() {        
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault());        
		Date date = this.Registrated;
		
		return dateFormat.format(date);
	}
	
	@Override
	public String toString()
	{
		return "At " + getDateTime() + " you took " + Count + " Pullups";
	}
}
