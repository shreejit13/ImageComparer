package main.java;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.*;  

/** 
* <h1>Image Compression!</h1>
* 
* The Image Compression program implements an application that simply compares images and
* generate a score based on percentage of imaged matching.
* 
* <p> The program takes input from CSV file and writes output to a different CSV file with 
* Image Compression score and time (in sec.) to run each comparison.
* 
* 
* @author  Shreejit Nair
* @version 1.0 
* @since   2020-02-22 
*/

public class ImageComparision {

	
	/** 
     * Setting default value for input file
     * if input file path is not passed as parameter to the main method then
     * these values will be set for input file path
     */
//	static String inputFile_Path="./input.csv";
	/** 
     * Setting default value for output file
     * if output file path is not passed as parameter to the main method then
     * these values will be set for output file path
     */
//	static String outputFile_Path="./output.csv";

	static String inputFile_Path="/Users/s0n00nk/Downloads/imagecomparer/in.csv";
	static String outputFile_Path="/Users/s0n00nk/Downloads/imagecomparer/output.csv";
	
	
    /** This method will take care of generating score by comparing the given 2 images
     * based on pixel to pixel comparison 
     * @param filea_path absolute path of image1
     * @param fileb_path absolute path of image2
     * @return double - this method will return a score between 0 to 10 for successful image comparisons, 
     * will return 11 if cannot read image file from given path and will return 12 if dimensions of images are not same.	
     */	
	static double processImage(String filea_path, String fileb_path){
    	
    	System.out.println("Info: Comparing image "+ filea_path +" with "+ fileb_path);

        BufferedImage imgA = null;
        BufferedImage imgB = null;

        try{
        	File fileA = new File(filea_path);
        	File fileB = new File(fileb_path);
            imgA = ImageIO.read(fileA);
            imgB = ImageIO.read(fileB);
        }
        catch (IOException e){
            System.out.println(e);
            return 11;
        }
        
        int width1 = imgA.getWidth();
        int width2 = imgB.getWidth();
        int height1 = imgA.getHeight();
        int height2 = imgB.getHeight();

        /** 
         * Checking if image dimensions are same
         */
        if ((width1 != width2) || (height1 != height2)) {
            System.out.println("Error: Images dimensions mismatched");
        	return 12;}
        else{
        	double unmatched = 0;
        	
            for (int y = 0; y < height1; y++){
            	
                for (int x = 0; x < width1; x++){
                	
                    int rgbA = imgA.getRGB(x, y);
                    int rgbB = imgB.getRGB(x, y);
                 
                    /** 
                     * pixel to pixel comparison
                     * saving unmatched number of pixels in variable unmatched  
                     */
                    if (rgbA != rgbB) {
                    	unmatched = unmatched + 1;
                    }
                    
                }
            }
            double total_pixels = width1 * height1;
            double score = (unmatched / total_pixels);
            System.out.println("Info: Finished Comparing image "+ filea_path +" with "+ fileb_path);
            return Math.abs(score*10);
        }
    }
    
    
    /** This method will read data from the given input CSV file in one go and
     * stores the data in an ArrayList.
     * @return ArrayList - this method will return an ArrayList with path of imageA and imageB which needs to be compared 
     */	
	static ArrayList<String[]> readCSV() {
    	
    	System.out.println("Info: Geting list of images from input CSV file : "+inputFile_Path);
       	ArrayList<String[]> files = new ArrayList<String[]>();
       	String[] record = new String[2];
    	Scanner scan = null;
		try {
			scan = new Scanner(new File(inputFile_Path));
	       	while(scan.hasNext())
	       	{
	       	    record = scan.nextLine().split(",");
	       	    files.add(record);
	       	}
		} catch (FileNotFoundException e) {
			System.out.println("Error: Failed to read input CSV file");
			e.printStackTrace();
		} finally {
			scan.close();
		}
        /** 
         * Removing header from input
         */
       	files.remove(0);
       	System.out.println("Info: List of images received");
	return files; 		
    }
    
    
    /** This method will write date to the given output CSV file 
     * @param ofile this ArrayList object will contain image1, image2, similar and elapsed 
     * data for each row of input CSV file
     */	
	static void writeCSV(ArrayList<String> ofile) {
    	
    	System.out.println("Info: Writing results to output CSV file : "+outputFile_Path);
    	
        /** 
         * Adding header from output
         */
        String header = "image1,image2,similar,elapsed";
        File csvFile = new File(outputFile_Path);
        PrintWriter csvWriter = null;
        //try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile));){
        try {
        	csvWriter = new PrintWriter(new FileWriter(csvFile));
        	csvWriter.println(header);
        	for(String item : ofile){
        		csvWriter.println(item);
        	}
        } catch (IOException e) {	
            e.printStackTrace();
        } finally {
        	csvWriter.close();
        }
    		
    }
    
    
    /** This method is to set variables based on the parameters passed to main method
     * @param args[] it takes input and output CSV files absolute path
     */	
    static void readParams(String args[]) {
 	
        for (int i = 0; i < args.length; i++) { 
        	 
        	if (args[i].equals("-i")) {
        		inputFile_Path=args[i+1]; 		
        	}
        	else if (args[i].equals("-o")) {
        		outputFile_Path=args[i+1];   		
        	}  	
        } 
    }
    
    
    /** This is the main method which make call to all methods in serial fashion.
     * @param args[] it takes input and output CSV files absolute path
     */
    public static void main(String args[])
    {	
    	
    	readParams(args);
    	
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
    	
    	String file[] = new String[4];
    	ArrayList<String[]> ifile = new ArrayList<String[]>();
    	ArrayList<String> ofile = new ArrayList<String>();

        /** 
        * loading input CSV file
        */
    	ifile = readCSV();
    	
    	System.out.println("Info: Starting image comparision");
    	
    	/** 
         * comparing the image set values line by line as given in input CSV file
         */
        for(String[] temp : ifile) {
        	int index=0;
        	List<String> myList = new ArrayList<String>();
            for(String temp1 : temp) {
                file[index]=temp1;
                index=index+1;         
            }
           
            myList.add(file[0]);
            myList.add(file[1]);
            
            long start = System.currentTimeMillis();
            /** 
             * call processImage method to get comparison score for images in scr
             */
            double scr = processImage(file[0],file[1]); 
            long end = System.currentTimeMillis();
            
            if (scr <= 10) {
            	myList.add(df.format(scr));
            } else if (scr == 11){
            	myList.add("image not found");
            } else if (scr == 12){
            	myList.add("dimension mismatch");
            }
            
            /** 
             * getting time taken to get image comparison score in seconds
             */
            float timesec = (end - start) / 1000F;
            myList.add(Float.toString(timesec));
                       
            /** 
             * converting the list to comma separated string
             */
            String output = myList.stream().collect(Collectors.joining(","));
            
            ofile.add(output);            
            
        }
        System.out.println("Info: Finished image comparision");
        
        /** 
        * writing to CSV file
        */
        writeCSV(ofile);

    }
	
}
