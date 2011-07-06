/**
 * 
 */
package org.gal.utils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author maxi
 *
 */
public class FileSet {
	ArrayList<File> set = new ArrayList<File>();
	private String name = new String();
	
	public FileSet(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void add(File file) {
		set.add(file);
	}
	
	public int getCount() {
		return set.size();
	}
	
	public File getFile(int index) {
		return set.get(index);
	}
	
	public String getCommand() {
		String command = new String();
		for (File e : set) {
			command += e.getAbsolutePath() + " "; 
		}
		return command;
	}
}
