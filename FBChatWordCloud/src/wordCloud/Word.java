package wordCloud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class Word {
	final Integer countA;
	final Integer countB;
	final Integer largestWordCount;
	final String text;
	private int adjustX = 0;
	private int adjustY = 0;
	
	private int randomNoiseX;
	private int randomNoiseY;
	
	/**
	 * Constructs a word
	 * @param text		the actual word
	 * @param countA	the number of times it occurs in input A
	 * @param countB	the number of times it occurs in input B
	 */
	public Word(String text, Integer countA, Integer countB, Integer largestWordCount){
		this.countA = countA;
		this.countB = countB;
		this.text = text;
		this.largestWordCount = largestWordCount;
		this.randomNoiseX = (int) (Math.random() * WordCloudPreferences.OVERLAP_RANDOM_NOISE);
		this.randomNoiseY = (int) (Math.random() * WordCloudPreferences.OVERLAP_RANDOM_NOISE);

	}
	
	/**
	 * @return percentage of the time the word occurs in input A
	 */
	public double getWordRatio(){
		return ((double) countA) / ((double)(countA) + (double)(countB));
	}
	
	private Color getWordColour(){
		double ratioA = getWordRatio();
		double ratioB = 1-ratioA;
		return new Color(
				(int)(WordCloudPreferences.COLOR_A.getRed() * ratioA + WordCloudPreferences.COLOR_B.getRed() * ratioB),
				(int)(WordCloudPreferences.COLOR_A.getGreen() * ratioA + WordCloudPreferences.COLOR_B.getGreen() * ratioB),
				(int)(WordCloudPreferences.COLOR_A.getBlue() * ratioA + WordCloudPreferences.COLOR_B.getBlue() * ratioB));
	}
	
	/**
	 * Returns the size this word should be rendered as, given its word count
	 * @param largestWordCount	the number of times the largest word appears
	 * @return the size the word should be
	 */
	public int getSize(){
		double coefficient = (((double)(countA + countB)) / ((double)(largestWordCount)));
		int rawScaledSize = (int) ((coefficient) * (double)(WordCloudPreferences.MAX_WORD_SIZE));
		return Math.max(WordCloudPreferences.MIN_WORD_SIZE, rawScaledSize);
	}
	
	/**
	 * Creates the font, setting the size according the the counts
	 * @return
	 */
	private Font getFont(){
		Font thisWordFont = new Font(WordCloudPreferences.WORD_FONT_NAME,Font.PLAIN, getSize());
		return thisWordFont;
	}
	
	/**
	 * Draws the word on the screen, with the calculated color, font, and position
	 * @param g	The screen to draw on
	 */
	public void drawWord(Graphics g){
		g.setColor(getWordColour());
		g.setFont(getFont());
		int[] position = getActualPosition();
		g.drawString(text, position[0], position[1]);
	}
	
	public Rectangle2D.Float currentBoundingBox(Graphics g){
		
		Font renderingFont = getFont();
		FontMetrics metrics = g.getFontMetrics(renderingFont);
		Rectangle2D.Float stringBounds = (Rectangle2D.Float) metrics.getStringBounds(text, 0, text.length(), g);
		int[] currentActualPosition = getActualPosition();
		
		float boundingWidth = stringBounds.width;
		float boundingHeight = metrics.getMaxAscent();
		float boundingX = currentActualPosition[0];
		float boundingY = currentActualPosition[1] - boundingHeight;//We do y-height, as the drawing occurs from bottom left, but we want the bounding box from the top left

		
		int padAmount = WordCloudPreferences.OVERLAP_PADDING;
		Rectangle2D.Float boundingBox = new Rectangle2D.Float(
				boundingX-padAmount,
				boundingY-padAmount,
				boundingWidth+padAmount,
				boundingHeight+padAmount);

		return boundingBox;
	}
	
	/**
	 * Moves the word's position by a certain amount, used to make sure the word cloud doesn't overlap
	 * @param x	the x amount to move the word by
	 * @param y	the y amount to move the word by
	 */
	public void moveByAmount(int x, int y){
		adjustX += x;
		adjustY += y;
	}
	
	private int[] getActualPosition(){
		int[] preferredPosition = getPreferredPosition();
		return new int[]{
				preferredPosition[0] + adjustX + randomNoiseX,
				preferredPosition[1] + adjustY + randomNoiseY};
	}
	
	/*public int getRenderedWordHeight(Graphics2D g){
		Font renderingFont = WordCloudPreferences.WORD_FONT;
		renderingFont.
		FontMetrics metrics =  g.getFontMetrics(renderingFont);
		return metrics.getHeight
	}*/
	
	private int[] getPreferredPosition(){
		int preferredX = (int) (WordCloudPreferences.WIDTH * getWordRatio());
		int preferredY = (int) (0.5 * WordCloudPreferences.HEIGHT); //We'd like to distribute along the x axis at the center of the screen
		return new int[]{preferredX,preferredY};
	}
}
