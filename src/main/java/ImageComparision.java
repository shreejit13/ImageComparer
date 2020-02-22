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

	static String inputFile_Path="./input.csv";
	static String outputFile_Path="./output.csv";

//	static String inputFile_Path="/Users/s0n00nk/Downloads/imagecomparer/in.csv";
//	static String outputFile_Path="/Users/s0n00nk/Downloads/imagecomparer/output.csv";
	
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

        if ((width1 != width2) || (height1 != height2)) 
        {
            System.out.println("Error: Images dimensions mismatched");
        	return 12;}
        else
        {
        	double unmatched = 0;
            for (int y = 0; y < height1; y++)
            {
                for (int x = 0; x < width1; x++)
                {
                    int rgbA = imgA.getRGB(x, y);
                    int rgbB = imgB.getRGB(x, y);
                 
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
    
    
    // method to read the input CSV file into an ArrayList
    static ArrayList<String[]> readCSV() {
    	
    	System.out.println("Info: Geting list of images from input CSV file : "+inputFile_Path);
    	Scanner scan = null;
		try {
			scan = new Scanner(new File(inputFile_Path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Failed to read input CSV file");
			e.printStackTrace();
		}
       	ArrayList<String[]> files = new ArrayList<String[]>();
       	String[] record = new String[2];
       	while(scan.hasNext())
       	{
       	    record = scan.nextLine().split(",");
       	    files.add(record);
       	}
       	// ignoring header from Input file
       	files.remove(0);
       	System.out.println("Info: List of images received");
	return files; 		
    }
    
    
    // method to write the result into output CSV file
    static void writeCSV(ArrayList<String> ofile) {
    	
    	System.out.println("Info: Writing results to output CSV file : "+outputFile_Path);
    	
        String header = "image1,image2,similar,elapsed";
        File csvFile = new File(outputFile_Path);
        try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile));){
        	csvWriter.println(header);
        	for(String item : ofile){
        		csvWriter.println(item);
        	}
        } catch (IOException e) {	
            e.printStackTrace();
        }	
    		
    }
    
    
    // method to set initial values passed as arguments
    static void setPath(String args[]) {
 	
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
     * @return Nothing
     */
    public static void main(String args[]) throws IOException 
    {	
    	
    	setPath(args);
    	
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
            String citiesCommaSeparated = myList.stream().collect(Collectors.joining(","));
            
            ofile.add(citiesCommaSeparated);            
            
        }
        System.out.println("Info: Finished image comparision");
        writeCSV(ofile);

    }
	
}
