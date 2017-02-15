package org.jenkinsci.plugins.minio;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;

import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.NoResponseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.jenkinsci.remoting.RoleChecker;
import org.xmlpull.v1.XmlPullParserException;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

public class MinioAllPathUploader implements FileCallable<Void> {
	 private static final long serialVersionUID = 1;
	
	private transient MinioClient minioClient;
	private String bucketName;
	private FilePath path;
	private String fileName;
	TaskListener listener;
	
	public MinioAllPathUploader(MinioClient minioClient,String bucketName,FilePath path,String fileName,TaskListener listener )
	{
		this.bucketName=bucketName;
		this.minioClient=minioClient;
		this.path=path;
		this.fileName=fileName;
		this.listener=listener;
	}
	
	

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public Void invoke(File f, VirtualChannel channel) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		long size = path.length();
		InputStream stream= null;
		
		
		try{
			
			  stream = new FileInputStream(path.getRemote());
		      String contentType = "application/octet-stream";
		      minioClient.putObject(bucketName, fileName, stream, size, contentType);
		     
		}
		 catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
			        | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException
			         | XmlPullParserException e) {
			      e.printStackTrace(listener.error("Minio error, failed to upload files"));
			     // run.setResult(Result.UNSTABLE);
			    } catch (IOException e) {
			      e.printStackTrace(listener.error("Communication error, failed to upload files"));
			     // run.setResult(Result.UNSTABLE);
			    } 
		finally{
			if(stream!=null)
			stream.close();
		}
		return null;
		
	}

}
