package system.restful.persistence.mapper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jasypt.util.text.BasicTextEncryptor;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

import system.restful.accessories.utils.PropertiesLoader;
import system.restful.accessories.constants.CollectionNames;
import system.restful.accessories.constants.SortOrder;
import system.restful.accessories.utils.ResourceUtil;
import system.restful.persistence.vo.RequestParameter;
import system.restful.persistence.vo.ResultData;


@Service
@Transactional
public class CommonsMapper {

	// --------------------------------------------------------------------------
	
	private Logger log = LoggerFactory.getLogger(getClass());
		
	// --------------------------------------------------------------------------
	@Autowired 
	private BasicTextEncryptor textEncryptor;
	
	@Autowired 
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	// --------------------------------------------------------------------------
	
	/**
	 * 
	 **/
	private MongoCollection getCollection(String collectionName) {
		return new Jongo( mongoTemplate.getDb() )
					.getCollection( collectionName );
	}
	
	private  boolean isExistsApplied(int count) {
		return count > 0?true:false;
	}
	
	private boolean isExistsData(Object object) {
		return object == null?false:true;
	}
	
	private boolean isNotError(Object object) {
		return object == null?true:false;
	}
	
	// --------------------------------------------------------------------------
	
	
	@SuppressWarnings("unchecked")
	public boolean isOwner(HashMap<String,Object> requestBody)
	{
		boolean isOwner = false;
		
		String email = (String) requestBody.get("email");
		String password = (String) requestBody.get("password");
		
		HashMap<String,Object> findOne = 
				getCollection( CollectionNames.OWNER_META_INFO_COLLECTION )
				.findOne( "{_id:#}", email)
				.as(HashMap.class);
		
		if(isExistsData(findOne)) {
			
			String orgPassword = textEncryptor.decrypt((String) findOne.get("password"));
			
			if(password.equals(orgPassword)) {
				isOwner = true;
			}
		} 
		
		return isOwner;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean isNotExistOwner(HashMap<String,Object> requestBody)
	{
		boolean isNotExistOwner = true;
		
		String email = (String) requestBody.get("email");
		
		HashMap<String,Object> findOne = 
				getCollection( CollectionNames.OWNER_META_INFO_COLLECTION )
				.findOne( "{_id:#}", email )
				.as(HashMap.class);
	
		
		if(isExistsData(findOne)) {
			isNotExistOwner = false;
		} 
		
		return isNotExistOwner;
	}
	
	/**
	 * 
	 **/
	public boolean insertOwner(HashMap<String,Object> requestBody) throws Throwable {
		
		boolean isOK = false;
		
		try{
			
			String createAt = ResourceUtil.utcTimeToString();
			String encPassword = encryptText((String)requestBody.get("password"));
			
			requestBody.put("createAt", createAt);
			requestBody.put("password", encPassword);
			
			WriteResult wr =  
					getCollection( CollectionNames.OWNER_META_INFO_COLLECTION )
					.insert(  ResourceUtil.jsonMapToString( requestBody ) );
		
			if(isNotError(wr.getError())) {
				isOK = true;
			}
		} catch (MongoException me) {
			isOK = false;
		}
		
		return isOK;
	}
	
	/**
	 * 
	 **/
	public boolean insertApplicationInfo(HashMap<String,Object> requestBody) throws Throwable {
		
		boolean isOK = false;
		
		try{
			String appId = encryptApplicationId(requestBody);
			requestBody.put("_id", appId);
			requestBody.put("email", requestBody.get("email"));
			requestBody.put("apiKey", encryptText(appId));
			requestBody.put("createAt", ResourceUtil.utcTimeToString());
			requestBody.remove("password");
			
			WriteResult wr =  
					getCollection( CollectionNames.APPLICATION_META_INFO_COLLECTION )
					.insert(  ResourceUtil.jsonMapToString( requestBody ) );
		
			if(isNotError(wr.getError())) {
				isOK = true;
			}
		} catch (MongoException me) {
			isOK = false;
		}
		
		return isOK;
	}
	private String encryptText(String appId) {
		return textEncryptor.encrypt(appId);
	}
	private String encryptApplicationId(HashMap<String,Object> requestBody) 
			throws NoSuchAlgorithmException {
		
		String sha1 = "";
		StringBuilder sb = new StringBuilder();
		sb.append( requestBody.get("email") );
		sb.append( requestBody.get("applicationName") );
		
	    try
	    {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(sb.toString().getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
	    return sha1;
	}
	private String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	/**
	 * 
	 **/
	@SuppressWarnings("rawtypes")
	public ResultData<List<HashMap>> findApplicationInfo(HashMap<String,Object> requestBody) throws Throwable
	{
		ResultData<List<HashMap>> resultData 
						= new ResultData<List<HashMap>>();
		
		List<HashMap> list = 
					Lists.newArrayList( 
							getCollection( CollectionNames.APPLICATION_META_INFO_COLLECTION )
							.find( generatefindQuery(requestBody) )
							.sort( "{createAt:-1}" )
							.as(HashMap.class) );
			
			
		resultData.setStatus( Status.ACCEPTED );
		
		if(isExistsData(list)) {
			resultData.setResult( list );
		} else {
			resultData.setResult( new ArrayList<HashMap>() );
		}
			
		return resultData;
	}
	private String generatefindQuery( HashMap<String,Object> requestBody ) {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"email\":");
		sb.append("\"");
		sb.append(requestBody.get("email"));
		sb.append("\"");
		if(requestBody.get("applicationName")!=null){
			sb.append(",");
			sb.append("\"applicationName\":");
			sb.append("\"");
			sb.append(requestBody.get("applicationName"));
			sb.append("\"");
		}
		sb.append("}");
		
		log.debug(sb.toString());
		
		return sb.toString();
	}
}
