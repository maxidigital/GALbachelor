package org.gal.security.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



public class KeyAdmin {
	final private String DEFAULT_SYMMETRIC_CIPHER = "AES";
	final private String DEFAULT_KEY_DIR = "gal/security/keys/";
	final private String adminKeyHex = "315a55876a385b3cc0d5a2aa0910a683";
	private SecretKey adminKey;
	public enum KeyType{SECRET, PUBLIC, PRIVATE}
	GalCipher cipher;
	
	public KeyAdmin() throws NoSuchAlgorithmException {
		byte[] secKey = hexStringToByteArray(adminKeyHex);
		adminKey = new SecretKeySpec(secKey, DEFAULT_SYMMETRIC_CIPHER);		
		cipher = new GalCipher(adminKey);
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	/**
	 * 
	 * @param alias
	 * @param keyType
	 * @return
	 */
	private String getFileName(String alias, KeyType keyType) {
		return this.DEFAULT_KEY_DIR + alias + "." + keyType.toString().toLowerCase();
	}
	
	
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException {

		KeyAdmin ka = new KeyAdmin();
		
		SecretKey key = ka.generateSecretKey();
		KeyPair keyPair = ka.generateKeyPair(1024);
							
		ka.saveKey("server2", key);
		ka.saveKey("server2", keyPair.getPublic());
		ka.saveKey("server2", keyPair.getPrivate());
		
		System.out.println(ka.toHex(key.getEncoded()));
//		System.out.println(keyPair.getPublic());
		System.out.println(keyPair.getPrivate());
		
		System.out.println();
		System.out.println();
		
		System.out.println( ka.toHex(ka.getSecretKey("server2").getEncoded()) );		
//		System.out.println( ka.getPublicKey("server2") );
		System.out.println( ka.getPrivateKey("server2") );
//		
//		
//		RSAPublicKey aa;		
//		aa = (RSAPublicKey) ka.getKey("server", KeyType.PRIVATE);
		
	}

	
	/**
	* Turns array of bytes into string
	*
	* @param buf	Array of bytes to convert to hex string
	* @return	Generated hex string
	*/
	public String toHex (byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	
	/**
	 * 
	 * @param keyName
	 * @return
	 */
	public boolean existsKey(String alias, KeyType keyType){
		return new File(getFileName(alias, keyType)).exists();
	}
	
	
	/**
	 * 
	 */
	public SecretKey generateSecretKey() {
		SecretKey key = null;
    	try {
			key = KeyGenerator.getInstance(DEFAULT_SYMMETRIC_CIPHER.toString()).generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}		
		
		return key;
	}

	
	/**
	 * 
	 */
	public SecretKey generateSecretKey(int keySize) {
    	//return  KeyGenerator.getInstance(algorithm.toString()).generateKey();
		
		KeyGenerator kg = null;
		SecretKey sk = null;
		try {
			kg = KeyGenerator.getInstance(DEFAULT_SYMMETRIC_CIPHER.toString());
			kg.init(keySize);
			sk = kg.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}					
		return sk;
	}
	
	
	public void saveKey(String alias, PublicKey key) {		
		if (!alias.isEmpty()) {
			String name = this.getFileName(alias, KeyType.PUBLIC);
//			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key.getEncoded());
//			this.writeToFile2(name, x509EncodedKeySpec);
			System.out.println("publiccc");
			
			this.writeToFile(name, key);

		}
	}

	public void saveKey(String alias, PrivateKey key) {				
		if (!alias.isEmpty()) {
			String name = this.getFileName(alias, KeyType.PRIVATE);
			this.writeToFile(name, key);
		}
	}

	public void saveKey(String alias, SecretKey key) {
		if (!alias.isEmpty()) {
			String name = this.getFileName(alias, KeyType.SECRET);
			this.writeToFile(name, key);
		}
	}
		
	public PublicKey getPublicKey(String alias) {
		String fileName = getFileName(alias, KeyType.PUBLIC);
		
		return (PublicKey)getObject(fileName);
	}

	public PrivateKey getPrivateKey(String alias) {
		String fileName = getFileName(alias, KeyType.PRIVATE);
		
		return (PrivateKey)getObject(fileName);
	}

	
	public SecretKey getSecretKey(String alias) {
		String fileName = getFileName(alias, KeyType.SECRET);
		
		return (SecretKey)getObject(fileName);		
	}	

	private Object getObject(String fileName){
		File file = new File(fileName);
		Object returnO = null;
		
		if (file.exists()) {		
			try {	            
				FileInputStream in = new FileInputStream(file);
	            int length = (int) file.length();
	            
	            byte[] wrappedKey = new byte[length];
	            in.read(wrappedKey, 0, length);
	            byte[] unwrappedKey = cipher.decrypt(wrappedKey);
	            
	            ObjectInputStream oo = new ObjectInputStream(new ByteArrayInputStream(unwrappedKey));
	            returnO = oo.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}			
		}
		return returnO;		
	}
		
	
	/**
	 * Encrypts the key and saves it on the hard disc
	 * @param name
	 * @param key
	 */
	private void writeToFile(String name, Object key){				
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);			
			oos.writeObject(key);
			
	        byte[] wrappedKey = cipher.encrypt(baos.toByteArray());
	        FileOutputStream fos = new FileOutputStream(name);
	        fos.write(wrappedKey);	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	
	/**
	 * 
	 * @param keySize
	 * @return
	 */
	public KeyPair generateKeyPair(int keySize){
		keySize = 1024; 
		
		KeyPairGenerator pairgen= null; 
		
		try {
			pairgen = KeyPairGenerator.getInstance("RSA");
			
			pairgen.initialize(keySize, new SecureRandom());			
						
		    } catch (GeneralSecurityException e) {
		    	e.printStackTrace();
		}					
		    
		return pairgen.generateKeyPair();
	} 

	
}
