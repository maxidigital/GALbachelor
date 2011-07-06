package org.gal.utils.archiver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 
 * @author maxi
 *
 */
public class FileArchiver {
	public enum CompressionLevel{NONE, VERY_LOW, NORMAL, HIGH, ULTRA}
	public enum Mode{COMPRESS, EXTRACT}
	private ArrayList<String> sourceFiles = new ArrayList<String>();
	private String		password = new String();
	private String		archiveName;
	private String		solidArchive = new String();
	private String		compLevel = new String();
	private Mode		mode;
	private String		extractFolder = new String();

	
	/**
	 * 
	 * @param extractFolder
	 */
	public void setExtractFolder(String extractFolder){
		if(mode == Mode.EXTRACT){		
			this.extractFolder = " -o" + new File(extractFolder).getAbsolutePath() + " ";
		}

		
	}
	
	/**
	 * 
	 * @param mode
	 * @param archive
	 */
	public FileArchiver(Mode mode) {
		this.mode = mode;		
		this.setArchiveName("NewArchive.7z");
		
		if(mode == Mode.COMPRESS){
			this.compLevel = " -mx9 ";
		}
	}
	
	public void setArchiveName(String archiveName){		
		if(archiveName.trim().endsWith(".7z")){
			this.archiveName = archiveName;
		}else{
			this.archiveName = " " + archiveName + ".7z ";
		}			
	}
	
	/**
	 * 
	 * @param compressionLevel
	 */
	public void setCompressionLevel(CompressionLevel compressionLevel){
		if( mode == Mode.COMPRESS){
			switch (compressionLevel) {
			case NONE:
				compLevel = " -mx0 ";
				break;
			case VERY_LOW:
				compLevel = " -mx1 ";
				break;
			case NORMAL:
				compLevel = " -mx5 ";
				break;
			case HIGH:
				compLevel = " -mx7 ";
				break;
			case ULTRA:
				compLevel = " -mx9 ";
				break;
			default:
				compLevel = " -mx9 ";
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param solid
	 */
	public void setSolidArchiveOff(){
		if (mode == Mode.COMPRESS) {		
			this.solidArchive = " -ms=off ";			
		}		 
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
						
		FileArchiver archiver = new FileArchiver(Mode.COMPRESS);
		String fileName = archiver.generateArchiveName();
		System.out.println(fileName);
		
//		archiver.setArchiveName(fileName);		
//		archiver.setCompressionLevel(CompressionLevel.NONE);
//		archiver.addSourceFile("target/classes");
//		archiver.setPassword("cocho");
//		archiver.justDoIt();		

//		FileArchiver dearchiver = new FileArchiver(Mode.EXTRACT);
//		dearchiver.setArchiveName("20110404-164616.7z");
//		dearchiver.setExtractFolder("src/");
//		dearchiver.justDoIt();
	}

	
	/**
	 * 
	 * @param psw
	 */
	public void setPassword(String psw) {
		this.password = " -p{" + psw.trim() + "} ";		
	}

	
	/**
	 * Add files to be compressed
	 * @param fileOrDir
	 */
	public void addSource(String source) {
		if (mode == Mode.COMPRESS) {
			String[] temo = source.split(";");
	
			File file;		
			for (String string : temo) {
				file = new File(source);
				
				if(file.isAbsolute()){
					if(file.exists()){
						sourceFiles.add(string);
						System.out.println(string + " was added");
					}
				}else {
					source = new File(".").getPath() + File.separator + source;
					sourceFiles.add(source);
					System.out.println(string + " was added");
				}
			}
		}
	}

	/**
	 *  Returns source files names in a "command" format 
	 */
	private String getSource() {
		String temp = "";
		for (String s : sourceFiles) {
			temp = temp + " " + s;
		}
		return temp;
	}

	
	/**
	 * 
	 */
	public String justDoIt() {
		String command;
		String _source = getSource();
		String _mode = (mode==Mode.COMPRESS)?" a ":" x ";
 				
		command = solidArchive + compLevel + password + archiveName + " " + _source + extractFolder;
		
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			command = "7za " + _mode + command;
			System.out.println(command);			
			
			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			command = "7z " + _mode + command;
			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return _source;
	}

	/**
	 * 
	 * @return
	 */
	public String generateArchiveName() {		
		
		Calendar cale = Calendar.getInstance();
		int hour = cale.get(Calendar.HOUR_OF_DAY);
		int min = cale.get(Calendar.MINUTE);
		int sec = cale.get(Calendar.SECOND);
		int day = cale.get(Calendar.DAY_OF_MONTH);
		int month = cale.get(Calendar.DATE);
		int year = cale.get(Calendar.YEAR);
		int millis = cale.get(Calendar.MILLISECOND);
			
//		return new File(".").getPath() + File.separator + year + "." + 
//					month + "." + day + "-" + hour + "." + min + "." + sec;
		
		return year + "-" + completeZeros(month,2) + "-" + completeZeros(day,2) + "T" + 
				completeZeros(hour,2) + "." + completeZeros(min,2) + "." + completeZeros(sec,2) + "." + completeZeros(millis, 3);
	}
	
	private String completeZeros(int number, int zeros){
		if (zeros == 2) {
			if(number < 10 ){
				return "0" + number;
			}			
		}else if (zeros == 3){
			if(number < 10 ){
				return "00" + number;
			}else if(number < 100){
				return "0" + number;
			}			
		}
		return "" + number;
	}
	
}
