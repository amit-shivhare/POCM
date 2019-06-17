package com.mt.data;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DataStore {
	
	public static final String filePath = "\\\\ch00sdi3\\Interfaces\\cXML\\cXMLManagerData\\";
	public static final String logPath = "\\\\ch00sdi3\\Interfaces\\cXML\\cXMLManagerData\\";
	private String dataContent = "";
	private File file = null;
	private static String separator = "->";
	
	public DataStore(String subject, String server) {
		file = new File("\\\\" + server + "\\Interfaces\\cXML\\cXMLManagerData\\" + subject + ".dta");
		
	}
	
	public String getPath() {
		return file.getAbsolutePath();
	}

	public String getValueForKey(String key) {
		
		try {
		
			BufferedReader br = new BufferedReader(new FileReader(file));

			String value = null;

			try {
				String line = br.readLine();

				while (line != null) {
					int sep_index = line.indexOf(separator);
					if(sep_index>-1) {
						if(line.substring(0, sep_index).equals(key)) {
							value = line.substring(sep_index);
							if(value.length()>2) value = value.substring(2);
							else value = "";
							br.close();
							return value;
						}
					}
					line = br.readLine();
				}

				br.close();
			}
			catch(IOException e) {
				dataContent = null;
			}
			
		}
		catch(FileNotFoundException e) {
			return(">nO_FilE<");
		}
		
		return null;
	}
	
	public ArrayList<String> getValuesForID(String ID) {
		
		ArrayList<String> rows = new ArrayList<String>();
		
		try {
		
			BufferedReader br = new BufferedReader(new FileReader(file));

			try {
				String line = br.readLine();

				while (line != null) {
					int sep_index = line.indexOf("|");
					if(sep_index>-1) {
						if(line.substring(0, sep_index).equals(ID)) {
							String row = line.substring(sep_index);
							int valindex = row.indexOf(separator);
							row = row.substring(valindex+1);
							rows.add(row);
						}
					}
					line = br.readLine();
				}
				br.close();
			}
			catch(IOException e) {
				dataContent = null;
			}
			
		}
		catch(FileNotFoundException e) {
			return null;
		}
		
		return rows;
	}

	public String showFile() {
		
		String result = "";
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(file));

			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line + "<br/>");
					line = br.readLine();
				}

				result = sb.toString();
				br.close();
			}
			catch(IOException e) {
				return "IO Error";
			}
				
		}
		catch(FileNotFoundException e) {
			return "Not Found File";
		}
		
		return result + "<br/>FileFinale";
	}
	
	public int updateValueForKey(String key, String value) {
		
		int retCode = 1;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(file));

			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					if(line.indexOf(key + separator)!=0) sb.append(line + "\r\n");
					else retCode = 0;
					line = br.readLine();
				}

				dataContent = sb.toString() + key + separator + value;
				br.close();
			}
			catch(IOException e) {
				return -1;
			}
				
		}
		catch(FileNotFoundException e) {
			dataContent = key + separator + value;
		}
		
		if(writeContent()<0) retCode = -2;

		return retCode;
		
	}
	
	public int deleteKey(String key) {
		
		int retCode = 0;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(file));

				try {
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();

					while (line != null) {
						if(line.indexOf(key + separator)!=0) {
							sb.append(line);
							line = br.readLine();
							if(line != null) sb.append("\r\n");
						}
						else {
							line = br.readLine();
							retCode = 1;
						}
					}
					
					dataContent = sb.toString();
					br.close();
				}
				catch(IOException e) {
					return -1;
				}
				
		}
		catch(FileNotFoundException e) {
			return -1;
		}
		
		if(writeContent()<0) retCode = -2;

		return retCode;
		
	}
	
	private int writeContent() {

		try{
			PrintWriter pw = new PrintWriter(file);
			pw.write(dataContent);
			pw.close();
		}
		catch(FileNotFoundException e) {
			return -1;
		}
		
		return 0;
	}
	
	public ArrayList<String> getRows() throws IOException, FileNotFoundException {

		ArrayList<String> rows = new ArrayList<String>(); 
		
		BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
					rows.add(line);
					line = br.readLine();
			}
			br.close();
			
		return rows;
		
	}

}
