package system.restful.accessories.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.util.Base64;


public class ResourceUtil {
	public static String utcTimeToString(){
		String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		return sdf.format(new Date());
	}
	public static String currentTime(){
		String ISO_FORMAT = "yyyyMMddHHmmssSSS";
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		return sdf.format(new Date());
	}
	public static String currentDate(){
		String ISO_FORMAT = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		return sdf.format(new Date());
	}
	public static String nvl(Object key)
	{
		if(key == null) return nvl("");
		else return nvl(key.toString());
	}

	public static String nvl(String key)
	{
		return nvl(key, "");
	}

	public static String nvl(String key, String defaultValue)
	{
		if(key == null || key.equals("null") || key.equals("")) key = defaultValue;
		return key;
	}

	public static String[] nvl(String key[])
	{
		for(int i = 0; i < key.length; i++)
		{
			key[i] = nvl(key[i]);
		}
		return key;
	}
	
	public static String generatId(int len)
	{
    	char[] idchars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    	char[] id = new char[len];
    	Random r = new Random(System.currentTimeMillis());
	    for (int i = 0;  i < len;  i++) {
	        id[i] = idchars[r.nextInt(idchars.length)];
	    }
	    return new String(id);
    }
	
	public static HashMap<String, Object> removingReserved(HashMap<String, Object> map, Object ... Keys) 
	{
    	for (int i = 0; i < Keys.length; i++) {
			map.remove(Keys[i]);
		}
    	return map;
    }
	
	public static HashMap<String,Object> replaceKeyNameToObjectId(HashMap<String,Object> map) {
		Object obj = map.remove("_id");
		map.put("objectId", obj);
		return map;
	}
	
	public static String jsonMapToString(HashMap<String, Object> map) throws JsonProcessingException {
		String jsonString = "";
		
		jsonString = new ObjectMapper().writeValueAsString( map );
		
		return jsonString;
	}
	
	public static String generateHmacSHA256Signature(String data, String key) throws GeneralSecurityException {
        byte[] hmacData = null;
 
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            hmacData = mac.doFinal(data.getBytes("UTF-8"));
			return Base64.encode(hmacData).toString();
        } catch (UnsupportedEncodingException e) {
            throw new GeneralSecurityException(e);
        }
    }

	
}
