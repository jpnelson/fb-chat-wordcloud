package wordCloud;

import java.awt.Color;
import java.awt.Font;

public class WordCloudPreferences {

	//Word options
	public static Integer MIN_WORD_THRESHOLD = 40; //Every word must appear at least MIN_WORD_THRESHOLD times
	public static Integer RANDOM_WORD_ADD_FACTOR = 5; //If words occur under the threshold, with probability RANDOM_WORD_ADD_FACTOR * (appearences / largestWord) add anyway
	//Display options
	public static String WORD_FONT_NAME = "Arial";
	public static Color COLOR_A = Color.red;
	public static Color COLOR_B = Color.blue;
	public static Color BACKGROUND_COLOR = Color.white;
	public static Integer MAX_WORD_SIZE = 80;
	public static Integer MIN_WORD_SIZE = 9;
	public static Integer WIDTH = 4000;
	public static Integer HEIGHT = 1280;
	public static Integer IMAGE_BORDER = 200; //add this much to the borders of the displayed image to ensure nothing is cut off
	
	//Overlap reduction
	public static Integer OVERLAP_ITERATIONS = 20;
	public static Double OVERLAP_HORIZON = 50.0;
	public static Double OVERLAP_MOVEMENT_AMOUNT_Y = 1.0;
	public static Double OVERLAP_MOVEMENT_AMOUNT_X = 1.0; //The factor that you scale the vectors by when moving them
	public static Double OVERLAP_RANDOM_NOISE = 20.0; //Add a vector of around OVERLAP_RANDOM_NOISE to reduce chance of coincidental rectangles
	public static Integer OVERLAP_PADDING = 10;
}
