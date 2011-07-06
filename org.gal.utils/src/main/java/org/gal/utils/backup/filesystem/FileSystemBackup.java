package org.gal.utils.backup.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * @author maxi
 * 
 */
public class FileSystemBackup {
	private ArrayList<String> sources = new ArrayList<String>();
	private ArrayList<String> excludes = new ArrayList<String>();

	private String type;
	private String defaultDestinationFolder;

	private String lastBackupStoreFile;

	
	public static void main(String[] args) {
		FileSystemBackup back = new FileSystemBackup();
		back.addSource(".settings/");
		back.makeBackup();
	}
	
	
	/*
	 * 
	 */
	public FileSystemBackup() {
		type = "full";

		File file = new File("backups/filesystem/");
		defaultDestinationFolder = file.getPath();

		if (!file.exists()) {
			file.mkdirs();
		}

		lastBackupStoreFile = "configuration/gal/config/backup.donotdelete";
	}

	/*
	 * 
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
     * 
     */
	private String readLastBackupDir() {
		String lastBackup = new String();
		File file = new File(lastBackupStoreFile);

		if (file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				lastBackup = br.readLine().trim();
				br.close();

				if (!lastBackup.isEmpty()) {
					lastBackup = lastBackup.split(":")[1].trim();

					File dir = new File(lastBackup);
					if (!dir.exists()) {
						lastBackup = new String();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return lastBackup;
	}

	/*
	 * 
	 */
	public boolean addSource(String folderName) {
		String[] names = folderName.split(";");

		for (String string : names) {
			if (new File(string).exists()) {
				sources.add(new File(string).getAbsolutePath() + File.separator);
			}
		}

		if (sources.size() > 0) {
			return true;
		}

		return false;
	}

	/*
	 * 
	 */
	public void setExcludeDirectories(String folderNames) {
		String[] names = folderNames.split(";");

		for (String string : names) {
			if (new File(string).exists()) {
				excludes.add(string);
			}
		}

	}

	/*
	 * 
	 */
	public String makeBackup() {
		System.out.println("Starting backup process...");

		String lastBackup = new String();
		String newBackupFolderName;

		if (type != "full") {
			lastBackup = readLastBackupDir();

			if (lastBackup.isEmpty()) {
				type = "full";
			}
		}

		System.out.println("Type: " + type);

		newBackupFolderName = getNewDestinationName();
		File file = new File(defaultDestinationFolder + File.separator + newBackupFolderName);
		file.mkdirs();
		newBackupFolderName = file.getPath();

		String command = new String();
		String source = new String();
		String exclude = new String();

		System.out.println("Source directories:");
		for (String sou : sources) {
			source = source + " " + sou;
			System.out.println(sou);
		}

		for (String exc : sources) {
			exclude = exclude + " " + exc;
		}

		if (type.contentEquals("full")) {
			command = " rsync -a " + source + " "
					+ newBackupFolderName;
		} else {
			command = " rsync -a --link-dest=" + lastBackup + " "
					+ source + " " + newBackupFolderName;
		}

		/*
		 * System.out.println("last: " + last); System.out.println();
		 * System.out.println("source: " + source); System.out.println();
		 * System.out.println("dest: " + dest);
		 */

		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			try {
				System.out.println("Windows Command: " + command);

				try {
					Runtime.getRuntime().exec(command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			} catch (Exception e) {
			}
		} else {
			System.out.println("Command: " + command);

			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		storeLastBackupDir(newBackupFolderName);

		System.out.println("Backup successfully stored in "
				+ newBackupFolderName);
		
		return newBackupFolderName;
	}

	/*
	 * 
	 */
	private void storeLastBackupDir(String lastBackupDir) {
		/*
    	 * 
    	 */
		if (!lastBackupDir.isEmpty()) {
			File file = new File(lastBackupStoreFile);

			if (file.exists()) {
				file.delete();
			}

			try {
				new File(file.getParent()).mkdirs();
				file.createNewFile();
				FileWriter fr = new FileWriter(file);
				BufferedWriter br = new BufferedWriter(fr);

				br.write("Last folder: " + lastBackupDir.trim().toString());
				br.close();
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 
	 */
	private String getNewDestinationName() {
		String newFolderName;
		DateTime dt = new DateTime();
		newFolderName = dt.toString().replaceAll(":", ".");

		if (type.contains("snapshot")) {
			newFolderName += "_snapshot/";
		} else {
			newFolderName += "_full/";
		}

		return newFolderName;
	}

	@Override
	public String toString() {
		return "Backup [sourceFolders=" + sources + ", excludeFolders="
				+ excludes + ", type=" + type + ", destinationFolder="
				+ defaultDestinationFolder + ", lastBackupStoreFile="
				+ lastBackupStoreFile + "]";
	}

}
