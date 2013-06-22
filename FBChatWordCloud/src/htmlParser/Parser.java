package htmlParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Parser {
	public final String filename;
	public final String otherPerson;
	
	public Parser(String filename, String otherPerson){
		this.filename = filename;
		this.otherPerson = otherPerson;
	}
	
	public MessageLog parse(){

		MessageLog messageLog = new MessageLog();
		try {
			String messageFileString = new Scanner(new File(filename)).useDelimiter("\\A").next();
			
			//Parse the HTML
			Document messageDocument = Jsoup.parse(messageFileString);
			Elements messageThreads = messageDocument.getElementsByClass("thread");
			
			//Look for the right conversation
			Element conversationThread=null;
			for(Element thread : messageThreads){
				Elements peopleInvolved = thread.getElementsByClass("header").get(0).getElementsByClass("profile");
				for(Element profile : peopleInvolved){
					if(profile.text().equals(otherPerson) && peopleInvolved.size()==2){
						conversationThread = thread;
						break;
					}
				}
			}
			//We have the right conversation
			if(conversationThread==null){
				System.err.println("Couldn't find profile: "+otherPerson);
			}
			Elements messages = conversationThread.getElementsByClass("message");
			
			//Parse individual messages
			for(Element message : messages){
				String sender = message.getElementsByClass("from").get(0).getAllElements().get(0).text();
				String messageBody = message.getElementsByClass("msgbody").get(0).text();
				Message messageToAdd = new Message(sender,messageBody);
				messageLog.add(messageToAdd);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return messageLog;
	}
}
