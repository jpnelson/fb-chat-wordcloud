package wordCloud;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import htmlParser.MessageLog;
import htmlParser.Parser;

public class WordCloud {
	JPanel pane = new JPanel();
	final String inputA;
	final String inputB;
	
	
	HashMap<String, Integer> inputAWordCount;
	HashMap<String, Integer> inputBWordCount;
	ArrayList<Word> wordsToDraw;
	
	public WordCloud(String inputA, String inputB){
		this.inputA = inputA;
		this.inputB = inputB;
		
		
		//Extra processing to come up with the word cloud information
		inputAWordCount = new HashMap<String, Integer>();
		inputBWordCount = new HashMap<String, Integer>();
		processWordCount(cleanString(removeCommonWords(inputA)), inputAWordCount);
		processWordCount(cleanString(removeCommonWords(inputB)), inputBWordCount);
		
		//Create an arraylist of words to draw in the paint routine
		wordsToDraw = new ArrayList<Word>();
		int mostCommonWordCount = maxValue(inputAWordCount,inputBWordCount);
		for(String wordText : inputAWordCount.keySet()){
			int countA = inputAWordCount.get(wordText);
			int countB = inputBWordCount.containsKey(wordText) ? inputBWordCount.get(wordText) : 0;
			Word w = new Word(wordText,countA,countB,mostCommonWordCount);
			//Certainly add if we're above the threshold. Add with diminishing probability if we're below the threshold
			if((countA+countB) > WordCloudPreferences.MIN_WORD_THRESHOLD){
				wordsToDraw.add(w);
			}else{
				if(Math.random() < 5*(double)((countA+countB))/((double)mostCommonWordCount)){
					wordsToDraw.add(w);
				}
			}
			//System.out.println(w.getSize());
		}
	}
	
	private void reduceOverlap(ArrayList<Word> wordsToDraw, Graphics g){
		System.out.println("Calculating word positions (may take some time depending on OVERLAP_ITERATIONS)");
		for(int i = 0; i < WordCloudPreferences.OVERLAP_ITERATIONS; i++){
			//We linearly decrease the amount we're moving the coefficient by
			double iterationRatio = (((double)(WordCloudPreferences.OVERLAP_ITERATIONS - i)) / 
					((double)(WordCloudPreferences.OVERLAP_ITERATIONS)));
			double movementCoefficientX = WordCloudPreferences.OVERLAP_MOVEMENT_AMOUNT_X * iterationRatio;
			double movementCoefficientY = WordCloudPreferences.OVERLAP_MOVEMENT_AMOUNT_Y * iterationRatio;

			int currentWordIndex = 0;
			printProgressBar(0);
			for(Word thisWord : wordsToDraw){
				//System.out.println(thisWord.text);
				for(Word otherWord : wordsToDraw){
					//Don't compare with ourself
					if(thisWord.equals(otherWord)){
						continue;
					}else{
						Rectangle2D thisBox = thisWord.currentBoundingBox(g);
						Rectangle2D otherBox = otherWord.currentBoundingBox(g);
						//If the boxes are too far away, don't bother already
						double distX = (thisBox.getX() - otherBox.getX());
						double distY = (thisBox.getY() - otherBox.getY());
						if(distX < WordCloudPreferences.OVERLAP_HORIZON && distY < WordCloudPreferences.OVERLAP_HORIZON){
							//If there is some overlap
							if(thisBox.intersects(otherBox)){
								//Calculate how much we should be moving to solve the overlap
								int moveUp = (int) (thisBox.getY() + thisBox.getHeight() - otherBox.getY());
								int moveLeft = (int) (thisBox.getX() + thisBox.getWidth() - otherBox.getX());
								int moveDown = (int) (otherBox.getY() + otherBox.getHeight() - thisBox.getY());
								int moveRight = (int) (otherBox.getX() + otherBox.getWidth() - thisBox.getX());
								int smallestMovement = Math.min(Math.min(moveUp, moveDown),Math.min(moveLeft,moveRight));
								double deltaX = 0;
								double deltaY = 0;
								if(moveUp == smallestMovement){
									deltaY = -moveUp;
								}else if(moveDown==smallestMovement){
									deltaY = moveDown;
								}else if(moveLeft==smallestMovement){
									deltaX = -moveLeft;
								}else{
									deltaX = moveLeft;
								}
								/*double deltaX = thisBox.getCenterX() - otherBox.getCenterX();
								double deltaY = thisBox.getCenterY() - otherBox.getCenterY();*/
								
								thisWord.moveByAmount(
										(int)(movementCoefficientX * deltaX),
										(int)(movementCoefficientY * deltaY));
							}
						}
					}
				}
				currentWordIndex++;
				double thisWordCompletePercent = (double)(currentWordIndex) / (double)(wordsToDraw.size());
				if(currentWordIndex % 100==0) printProgressBar(((double)(i) + (thisWordCompletePercent))/((double)(WordCloudPreferences.OVERLAP_ITERATIONS)));
			}
			printProgressBar((double)(i)/((double)(WordCloudPreferences.OVERLAP_ITERATIONS)));

		}
	}
	
	private static void printProgressBar(double percent){
		int width = 40;
		System.out.print("|");
		for(int i = 0; i < (int)(percent*width); i++){
			System.out.print("=");
		}
		for(int i = (int)(percent*width); i < width; i++){
			System.out.print(" ");
		}
		System.out.print("|  "+Math.round(100*percent)+"%\r");
	}
	
	/**
	 * Given two hash tables, returns the largest value from either
	 * @param tableA	the first table
	 * @param tableB	the second table
	 * @return	the largest value from either table
	 */
	private static int maxValue(HashMap<String, Integer> tableA, HashMap<String, Integer> tableB){
		int largest = 1;
		for(Integer count : tableA.values()){
			if(count > largest){
				largest = count;
			}
		}
		for(Integer count : tableB.values()){
			if(count > largest){
				largest = count;
			}
		}
		return largest;
	}
	
	/**
	 * Takes a string and removes punctuation, common words, etc.
	 * @param s	the string to clean
	 */
	private static String cleanString(String s){
		//Remove punctuation
		String[] punctuation = {"?","!",".",",","\"","*",")","(","'"};
		String clean = s;
		for(String p : punctuation){
			clean =  clean.replace(p, "");
		}
		
		//Transform to lower case
		clean = clean.toLowerCase();
		
		//Remove multi spaces
		clean = clean.replaceAll(" +", " ");
		
		return clean;
	}
	/**
	 * Take a space separated list of words, and remove common words. Also makes the string lower case
	 * @param s	a space separated string
	 * @return		the string without common words
	 */
	private static String removeCommonWords(String s){
		String[] commonWordsList = {"to","you","i","am","is","of","and","the","so","that","it","a"};
		String reduced = s.toLowerCase();
		for(String w : commonWordsList){
			reduced = reduced.replace(" "+w+" ", " ");
		}
		return reduced;
	}
	
	private void processWordCount(String input, HashMap<String, Integer> table){
		for(String word : input.split(" ")){
			if(table.containsKey(word)){
				Integer oldCount = table.get(word);
				table.put(word, oldCount+1);
			}else{
				table.put(word, 1);
			}
		}
	}
	
	public void makeWordCloud(String filename){
		BufferedImage bImg = new BufferedImage(WordCloudPreferences.WIDTH+WordCloudPreferences.IMAGE_BORDER,WordCloudPreferences.HEIGHT+WordCloudPreferences.IMAGE_BORDER,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(WordCloudPreferences.BACKGROUND_COLOR);
		g.fillRect(0, 0, WordCloudPreferences.WIDTH+WordCloudPreferences.IMAGE_BORDER, WordCloudPreferences.HEIGHT+WordCloudPreferences.IMAGE_BORDER);
		
		//Start drawing
		reduceOverlap(wordsToDraw, g);
		for(Word w : wordsToDraw){
			//g2.setColor(Color.white);
			//g2.draw(w.currentBoundingBox(g2));
			w.drawWord(g);
		}
		
		//Save the image
		try{
			ImageIO.write(bImg, "png", new File(filename));
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String args[]){
		String usageString = "Usage: filename name1 name2 outputfile\nOr: filename name1 name2\nMake sure you quote names spaces with \" to preserve spaces";
		String messagesFile=null;
		String otherPerson=null;
		String thisPerson=null;
		String outputFile="cloud.png";
		if(args.length<3){
			System.out.println(usageString);
			System.exit(0);
		}else{
			messagesFile = args[0];
			thisPerson = args[1];
			otherPerson = args[2];
			if(args.length==4) outputFile = args[3];
		}
		System.out.println("Generating word cloud for "+thisPerson+" and "+otherPerson+" in "+messagesFile);
		System.out.println("Parsing HTML file");
		Parser p = new Parser(messagesFile,otherPerson);
		MessageLog messages = p.parse();
		String thisPersonMessages = messages.getMessagesFrom(thisPerson).getMessageText();
		String otherPersonMessages = messages.getMessagesFrom(otherPerson).getMessageText();
		System.out.println("Done parsing");
		System.out.println("Generating word cloud");
		WordCloud w = new WordCloud(thisPersonMessages, otherPersonMessages);
		w.makeWordCloud(outputFile);
		System.out.println("Complete, cloud image written to "+outputFile);

		
		
	}
}
