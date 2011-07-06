package org.gal.security.crypto;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import org.gal.info.SystemInfo;


public class GalCipher{
	public enum Algorithm {AES, DES, BLOWFISH, TRIPLEDES, RC4, RSA};
	private final Algorithm DEFAULT_ALGORITHM = Algorithm.AES;
	
	private Algorithm algorithm;
	private Key key;
	private byte[] buf = new byte[1024];
	
	private Cipher ecipher;
	private Cipher dcipher;

	
	/**
	 * 
	 */
	private GalCipher() {
		SystemInfo systemInfo = new Sys
		this.algorithm = DEFAULT_ALGORITHM;
		
	}
	
	/**
	 * 
	 * @param key
	 */
    public GalCipher(Key key) {
		this();
		this.key = key;
		this.initCiphers();
	}
    
    /**
     * 
     * @param key
     * @param algorithm
     */
    public GalCipher(Algorithm algorithm, Key key) {
		this();
		this.key = key;
		this.algorithm = algorithm;
		this.initCiphers();
	}

    
    /**
     * 
     */
	public void encrypt(File file) {
		File output = new File(file.getAbsolutePath() + ".enc");
		
    	try {
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(output);
			
			this.encrypt(in, out);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}    		
		
	}

	/**
	 * 
	 */
	public void decrypt(File file) {
		String newName = file.getAbsolutePath();
		
		if (!newName.endsWith("enc")) {
			newName = newName + ".dec";
		}
		
		File output = new File(newName);
		
    	try {
			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(output);
			
			this.decrypt(in, out);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

    
    
    /**
     * 
     */
	private void encrypt(InputStream in, OutputStream out) throws IOException {
    	out = new CipherOutputStream(out, ecipher);

    	int numRead = 0;
    	while ((numRead = in.read(buf)) >= 0) {
    		out.write(buf, 0, numRead);
    	}
    	out.close();
	}
	
	/**
	 * 
	 * @param in
	 * @param out
	 * @throws Exception
	 */
    private void decrypt(InputStream in, OutputStream out) throws Exception {
    	in = new CipherInputStream(in, dcipher);

    	int numRead = 0;
    	while ((numRead = in.read(buf)) >= 0) {
    		out.write(buf, 0, numRead);
    		
    	}
    	out.close();    	
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
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		String plainText = new String("a");		
		
		System.out.println("Starting...");
		Algorithm algo;		
		algo = Algorithm.AES;
				
		KeyAdmin keyAdmin = new KeyAdmin();		
		SecretKey secKey = keyAdmin.getSecretKey("server2");
		PublicKey pubKey = keyAdmin.getPublicKey("server2");
		PrivateKey privKey = keyAdmin.getPrivateKey("server2");
		GalCipher cc = new GalCipher(algo, secKey);
		
		System.out.println("Antes de wrappear " + cc.toHex(secKey.getEncoded()));
		byte[] caca = cc.wrapKey(secKey, pubKey);
		System.out.println("Unwrapped! " + cc.toHex(cc.unwrapKey(caca, privKey).getEncoded()));
	
		
		System.out.println("Algorithmus: " + algo);
//		System.out.println("Key        : " + cc.toHex(key.getEncoded()));
//		System.out.println("Key size   : " + key.getEncoded().length * 8 + " bits");		
		System.out.println("Plain text : " + plainText + " (" + plainText.length() + ")" );
		
		byte[] cipherText = cc.encrypt(plainText.getBytes());		
		System.out.println("Cipher text: " + new String(cipherText) + " (" + new String(cipherText).length() + ")");
		
		String decrypted = new String(cc.decrypt(cipherText));		
		System.out.println("Decrypted  : " + decrypted + " (" + decrypted.length() + ")");
		
		System.out.println("-----------------------------------");
		System.out.println("-----------------------------------");
		
						
//		File source = new File("D:\\temp/Demo.avi");
//		File encryp = new File("D:\\Videos\\Hola.txt.enc");
//		File deencr = new File("D:\\Videos\\Hola_rev.txt");
		
//		cc.encrypt(source);
				
	}


	/**
	 * 
	 * @param file
	 * @param text
	 */
	public void writeToFile(File file, String text){
		try{			
			  FileWriter fstream = new FileWriter(file);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(text);
		
			  out.close();
		}catch (Exception e){
			  System.err.println("Error: " + e.getMessage());
		}
	}
	

	/**
	 * 
	 */
	public byte[] encrypt(byte[] plainText) {
		byte[] cipherText = null;
		
		try {
			cipherText = ecipher.doFinal(plainText);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return cipherText;
	}


	/**
	 * 
	 */
	public byte[] decrypt(byte[] cipherText) {
		byte[] plainText = null;
		
		try {
			plainText = dcipher.doFinal(cipherText);
			
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}		
    	
		return plainText;
	}
	
	public Key unwrapKey(byte[] wrappedKey, Key privateKey){
		Key secretKey = null;		
		
		try {
			Cipher unwrapper = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			unwrapper.init(Cipher.UNWRAP_MODE, privateKey);
			secretKey = unwrapper.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return secretKey;
	}
	
	public byte[] wrapKey(SecretKey keyToBeWrapped, Key publicKey) {
		byte[] wrappedKey = null;
		try {
			Cipher wrapper = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			wrapper.init(Cipher.WRAP_MODE, publicKey);
			wrappedKey = wrapper.wrap(keyToBeWrapped);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return wrappedKey;
	}
	
	/**
	 * 
	 */
	private void initCiphers() {
		
		SecretKeySpec skeySpec = new SecretKeySpec(this.key.getEncoded(), algorithm.toString());
		
		try {
			ecipher = Cipher.getInstance(algorithm.toString());
			dcipher = Cipher.getInstance(algorithm.toString());
			
			ecipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			dcipher.init(Cipher.DECRYPT_MODE, skeySpec);

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Returns the contents of a file in a byte array.
	 * @param file
	 * @return
	 */
	public byte[] getBytesFromFile(File file) {
	    InputStream is;
	    long length = file.length();
	    byte[] bytes = null;

    
		try {
			is = new FileInputStream(file);
			
		    if (length <= Integer.MAX_VALUE) {
			    // Create the byte array to hold the data
			    bytes = new byte[(int)length];

			    int offset = 0;
			    int numRead = 0;
			    while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length-offset)) >= 0) {
			        offset += numRead;
			    }

			    // Ensures that all the bytes have been read in
			    if (offset < bytes.length) {
			        throw new IOException("File could not completely be read" + file.getName());
			    }

			    is.close();
		    }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    

	    
	    

	    return bytes;
	}

	
}