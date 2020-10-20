package com.qdriven.doc.value;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class UnivCollectioSingelton {
	private static UnivCollectioSingelton single_instance = null; 
	
	public HashMap<String,UnivPub> univColl;
	private final String fileUrl ="src/main/resources/value/univ-value";
	private UnivCollectioSingelton() {
		
		univColl =  new HashMap<String,UnivPub>();
		processData();
	}
	
	private void processData() {
		
		try {
			File text = new File(fileUrl);
			Scanner scnr = new Scanner(text);
			while(scnr.hasNextLine()){
	            String line = scnr.nextLine();
	            String[] values_=line.split("\\|");
	            if(values_.length == 6 ) {
	            	UnivPub univVal =  new UnivPub();
	            	univVal.setUrl(values_[1]);
	            	univVal.setTitle(values_[0]);
	            	univVal.setAbrJnlTitle(values_[2]);
	            	univVal.setYear(values_[3]);
	            	univVal.setMedlineJnlTl(values_[4]);
	            	univVal.setTrakerId(values_[5]);
	            	univColl.put(values_[0],univVal);
	            }
	        }

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String,UnivPub> getUnivColl(){
		return univColl;
	}
	
	public static UnivCollectioSingelton getInstance() 
    { 
        if (single_instance == null) 
            single_instance = new UnivCollectioSingelton(); 
  
        return single_instance; 
    } 
	
}
