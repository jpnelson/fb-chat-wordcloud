package htmlParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
	public final String sender;
	public Date time;
	public final String messageBody;
	public Message(String sender, String messageBody){
		this.sender = sender;
		this.messageBody = messageBody;
	}
	@Override
	public String toString(){
		return sender+":"+messageBody;
	}
	
}
