package htmlParser;

import java.util.ArrayList;

public class MessageLog extends ArrayList<Message>{

	public MessageLog getMessagesFrom(String sender){
		MessageLog messagesFromSender = new MessageLog();
		for(Message m : this){
			if(m.sender.equals(sender)){
				messagesFromSender.add(m);
			}
		}
		return messagesFromSender;
	}
	
	/**
	 * Returns all messages, without the sender and without formatting
	 */
	public String getMessageText(){
		String unformattedMessageContents = "";
		for(Message m : this){
			unformattedMessageContents += " "+m.messageBody;
		}
		return unformattedMessageContents;
	}
}
