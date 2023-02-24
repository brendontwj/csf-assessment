package vttp2022.csf.assessment.server.repositories;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Repository
public class MapCache {

	@Autowired
	private AmazonS3 s3;

	// TODO Task 4
	// Use this method to retrieve the map
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public byte[] getMap(float lng, float lat) {
	// 	Implmementation in here
		try {
			GetObjectRequest getReq = new GetObjectRequest("deadmanfred", "%f%f".formatted(lng, lat));
			S3Object result = s3.getObject(getReq);
			try (S3ObjectInputStream is = result.getObjectContent()) {
				byte[] buffer = is.readAllBytes();
				return buffer;
			}
		} catch (AmazonS3Exception exception) {

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	// You may add other methods to this class
	public String saveMap(float lng, float lat, byte[] image) {
		ObjectMetadata metadata = new ObjectMetadata();
		String key = "%f%f".formatted(lat,lng);
		metadata.setContentLength(image.length);
		InputStream is = new ByteArrayInputStream(image);
		s3.putObject("deadmanfred", key, is, metadata);

		return key;
	}
}
