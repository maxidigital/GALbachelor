package org.gal.info;

import java.io.File;
import java.util.Properties;


public class SystemInfo {
	public enum BackupType{FILE_SYSTEM, DATABASE}
	Properties	properties = new Properties();
	
	public SystemInfo() {
		properties.setProperty("system.id", "2e6d8a");
		properties.setProperty("system.alias", "maxi");
		properties.setProperty("system.directories.keys", "security/keys");
		properties.setProperty("system.directories.configuration", "configuration/gal");
		properties.setProperty("system.directories.backups.file_system", "backups/filesystem");
		properties.setProperty("system.directories.backups.database", "backups/database");
		properties.setProperty("system.directories.bundles", "bundles");
		
		//// External Servers
		properties.setProperty("external.servers.cas.ip", "192.168.0.1");
		properties.setProperty("external.servers.cas.alias", "cas_server");		
		properties.setProperty("external.servers.bsfs.ip", "192.168.0.2");
		properties.setProperty("external.servers.bsfs.alias", "backup_server_1");
		
		//// Security.algos
		properties.setProperty("system.security.ciphers.symmetric.default", "AES");
		properties.setProperty("system.security.ciphers.asymmetric.default", "RSA");
		properties.getProperty("system.security.ciphers.integrity.default", "SHA1");
		//// Security.keys
		properties.getProperty("system.security.keys.primary.public.alias", "main");
		properties.getProperty("system.security.keys.primary.private.alias", "main");				
		properties.getProperty("system.security.keys.secundary.public.alias", "secundary");
		properties.getProperty("system.security.keys.secundary.private.alias", "secundary");		
		properties.getProperty("system.security.keys.keyadmin.secret.hex", "315a55876a385b3cc0d5a2aa0910a683");
	}
	
	public String getProperty(String key){		
		return properties.getProperty(key);
	}
	

	public String getMyId() {
		return properties.getProperty("system.id");
	}

	public String getMyAlias() {		
		return properties.getProperty("system.alias");
	}

	public String getSystemDir() {		
		return new File(".").getAbsolutePath();
	}

	public String getKeysDir() {		
		return properties.getProperty("system.directories.keys");
	}

	public String getConfigurationDir() {		
		return properties.getProperty("system.directories.configuration");
	}

	public String getBackupsDir(BackupType backupType) {
		return properties.getProperty("system.directories.backups." + backupType.toString().toLowerCase());
	}

	public String getBundlesDir() {
		return properties.getProperty("system.directories.bundles");
	}

	public String getPrimaryPublicKeyAlias() {
		return properties.getProperty("system.security.keys.primary.public.alias");
	}

	public String getPrimaryPrivateKeyAlias() {
		return properties.getProperty("system.security.keys.primary.private.alias");
	}

	public String getSecundaryPublicKeyAlias() {
		return properties.getProperty("system.security.keys.secundary.public.alias");
	}

	public String getSecundaryPrivateKeyAlias() {
		return properties.getProperty("system.security.keys.secundary.private.alias");
	}

	public String getKeyAdminSecretKeyHex(){
		return properties.getProperty("system.security.keys.keyadmin.secret.hex");
	}

	public String getDefaultSymmetricCipher() {
		return properties.getProperty("system.security.ciphers.symmetric.default");		
	}

	public String getDefaultAsymmetricCipher() {
		return properties.getProperty("system.security.ciphers.asymmetric.default");
	}

	public String getDefaultIntegrityAlgorithm() {
		return properties.getProperty("system.security.ciphers.integrity.default");
	}

}
