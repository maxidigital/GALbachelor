package org.gal.utils;

import java.io.File;
import java.text.StringCharacterIterator;

/*
 * 
 */
public class Folder{
	private String name = new String();
	
	/*
	 * 
	 */
	public boolean isEmpty() {
		return name.isEmpty();
	}
	
	/*
	 * 
	 */
	public Folder() {
		this.name = "";
	}
	
	/*
	 * 
	 */
	public Folder(String name) {
		if (name.contentEquals(".")) {
			name = format("");
			setAbsolutePath();
		} else {
			this.name = format(name);
		}	
	}
	
	/*
	 * 
	 */
	public void setAbsolutePath() {		
		name = format(format((new File ("")).getAbsolutePath()) + name); 
	}
	
	/*
	 * 
	 */
	public boolean exists() {
		File file = new File(name);
		if (file.exists()) {				
			return true;
		}
		return false;
	}
	
	/*
	 * 
	 */
	public boolean create() {
		if(!this.exists()) {
			File file = new File(name);
			return file.mkdirs();
		}
		return false;
	}
	
	/*
	 * 
	 */
	public int getParentDirectoriesCount() {
		return 0;
	}
	
	/*
	 * 
	 */
	public Folder getParentDirectory(int index) {
		return null;
	}
	
	/*
	 * 
	 */
	@SuppressWarnings("static-access")
	public String format(String dirName) {
		dirName = dirName.trim();
		
		StringBuilder result = new StringBuilder();
	    StringCharacterIterator iterator = new StringCharacterIterator(dirName);
	    char character =  iterator.current();
	    while (character != iterator.DONE ){
	     
	      if (character == '\\') {
	    	  result.append("/");
	      }
	       else {
	    	   result.append(character);
	      }
	      character = iterator.next();
	    }
	    dirName = new String(result.toString());		
		
		if (!dirName.endsWith("/")) {
			dirName = dirName + "/";
		}
		if (dirName.startsWith("/") && dirName.length() > 1) {
			dirName = dirName.substring(1);			
		}
		return dirName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;		
	}
}
