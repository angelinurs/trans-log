package com.beat.trans_log.scp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.beat.trans_log.config.SftpAuthenticationProperties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UploadComponent {
	
	@Autowired
	SftpAuthenticationProperties authenticationProperties;
	
	public UploadComponent(SftpAuthenticationProperties authenticationProperties) {
		this.authenticationProperties = authenticationProperties; 
	}
	
	public void doScpIdPassword( String localFilePath, String remoteFilePath ) {
		
		String username = authenticationProperties.getUser();
		String host = authenticationProperties.getHost();
		int port = Integer.parseInt( authenticationProperties.getPort() );
		
		String pwd = authenticationProperties.getPassword();
		
		Path dirPath = Paths.get( authenticationProperties.getRemoteDir() );		
		Path remoteFileFullPath = dirPath.resolve( remoteFilePath );
		
		JSch jsch = new JSch();
		
		// 임시 로그 상세
		JSch.setLogger(new Logger() {
		    @Override
		    public boolean isEnabled(int level) {
		        return true;
		    }
		    @Override
		    public void log(int level, String message) {
		        log.debug("JSch: {}", message);
		    }
		});
		
		try { 
			
			Session session = jsch.getSession( username, host, port );
			Properties config = new Properties();
			session.setPassword( pwd );
			config.put("StrictHostKeyChecking", "no");
			session.setConfig( config );
			
			session.connect();
			
			// file upload
			uploadFile(session, localFilePath, remoteFileFullPath.toString().replace("\\", "/"));
			
			session.disconnect();
			
		} catch (JSchException | IOException | SftpException e) {
			log.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * @implNote 공개키를 서버에 저장하여 인증하는 방식  password 필요없음.
	 * @param localFilePath
	 * @param remoteFilePath
	 */
	public void doScpKeyExchange( String localFilePath, String remoteFilePath ) {
		
		String username = authenticationProperties.getUser();
		String host = authenticationProperties.getHost();
		int port = Integer.parseInt( authenticationProperties.getPort() );
		
		String privateKey = authenticationProperties.getPrivateKey();
//		String privateKeyPassPhrase = authenticationProperties.getPrivateKeyPassPhrase();
		
		log.info("Private Key Path: {}", privateKey);
		
		JSch jsch = new JSch();
		
		// 임시 로그 상세
		JSch.setLogger(new Logger() {
		    @Override
		    public boolean isEnabled(int level) {
		        return true;
		    }
		    @Override
		    public void log(int level, String message) {
		        log.debug("JSch: {}", message);
		    }
		});
		
		try { 
//			jsch.addIdentity(privateKey, privateKeyPassPhrase);
			jsch.addIdentity(privateKey);
			
			Session session = jsch.getSession( username, host, port );
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			session.connect();
			
			// file upload
			uploadFile(session, localFilePath, remoteFilePath);
			
			session.disconnect();
			
		} catch (JSchException | IOException | SftpException e) {
			log.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * @author gipark
	 * @implNote Upload File
	 * @param session
	 * @param localFilePath
	 * @param remoteFilePath
	 * @throws JSchException
	 * @throws IOException
	 * @throws SftpException
	 */
	private void uploadFile( 
			Session session,
			String localFilePath, 
			String remoteFilePath ) throws JSchException, IOException, SftpException  {
		
		Channel channel = session.openChannel( "sftp" );
		channel.connect();
		
		ChannelSftp channelSftp = (ChannelSftp) channel;
		channelSftp.put( new FileInputStream(localFilePath), remoteFilePath );
		
		channelSftp.disconnect();
		channel.disconnect();
	}

	/**
	 * @implNote key reader
	 * @deprecated
	 * @param filePath
	 */
	public void privateKeyRead(String filePath) {
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    while ((line = reader.readLine()) != null) {
		        log.info(line);
		    }
		} catch (IOException e) {
		    log.error("Error reading file: {}", e.getMessage());
		    e.printStackTrace();
		}
	}
	
	/**
	 * @implNote pemkey generator
	 * @deprecated
	 * @param directory
	 */
	public void keyPairGenerator( String directory ) {
        try {
            // Create RSA Key Pair Generator
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            // Get Private and Public Key
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Create directory if it does not exist
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Save Private Key to PEM File
            try (FileOutputStream privateKeyOut = new FileOutputStream(Paths.get(directory, "private_key.pem").toFile() );
                 JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(privateKeyOut))) {
                pemWriter.writeObject(privateKey);
            }

            // Save Public Key to PEM File
            try (FileOutputStream publicKeyOut = new FileOutputStream(Paths.get(directory, "public_key.pem").toFile());
                 JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(publicKeyOut))) {
                pemWriter.writeObject(publicKey);
            }

            log.info("Keys generated and saved to {} and {}", 
                     Paths.get(directory, "private_key.pem").toAbsolutePath(), 
                     Paths.get(directory, "public_key.pem").toAbsolutePath());

        } catch (Exception e) {
        	log.error("Error generating keys: {}", e.getMessage(), e);
            
        }
	}
}
