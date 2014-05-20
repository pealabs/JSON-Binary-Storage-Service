package system.restful.persistence.mapper;

import java.util.HashMap;

import javax.ws.rs.core.Response.Status;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system.restful.accessories.constants.CollectionNames;
import system.restful.accessories.utils.ResourceUtil;
import system.restful.persistence.vo.RequestParameter;
import system.restful.persistence.vo.ResultData;

import com.mongodb.WriteResult;

@Service
@Transactional
public class FilesMapper {

	// --------------------------------------------------------------------------
	
	private Logger log = LoggerFactory.getLogger(getClass());
			
	// --------------------------------------------------------------------------

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
	
	@SuppressWarnings("unused")
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
	public boolean isInvalidCertification(RequestParameter essentialParameter)
	{
		boolean isInvalidCertification = true;
		
		HashMap<String,Object> findOne = 
				getCollection( CollectionNames.APPLICATION_META_INFO_COLLECTION )
				.findOne( "{_id:#, apiKey:#}", essentialParameter.getAppId(), essentialParameter.getApiKey() )
				.as(HashMap.class);
	
		log.debug(String.format("Request certification info %s %s", essentialParameter.getAppId(), essentialParameter.getApiKey()));
		
		if(isExistsData(findOne)) {
			isInvalidCertification = false;
		} 
		
		return isInvalidCertification;
	}
	
	/**
	 * 
	 **/
	@SuppressWarnings("unchecked")
	public ResultData<HashMap<String,Object>> findOne(RequestParameter requestParameter) throws Throwable
	{
		ResultData<HashMap<String,Object>> resultData = new ResultData<HashMap<String,Object>>();
		
		HashMap<String,Object> findOne = 
				getCollection( CollectionNames.FILES_META_INFO_COLLECTION )
				.findOne( "{_id:#}", requestParameter.getFileName() )
				.as(HashMap.class);
	
		resultData.setStatus( Status.ACCEPTED );
		
		if(isExistsData(findOne)) {
			resultData.setResult( findOne );
		} else {
			resultData.setResult( new HashMap<String,Object>() );
		}
		
		return resultData;
	}
	
	
	
	/**
	 * 
	 **/
	public ResultData<String> insert(RequestParameter essentialParameter
			, HashMap<String, Object> requestBody) throws Throwable {
		
		ResultData<String> resultData = new ResultData<String>();
		
		WriteResult wr =  
				getCollection( CollectionNames.FILES_META_INFO_COLLECTION )
				.insert(  ResourceUtil.jsonMapToString( requestBody )  );
	
		if(isNotError(wr.getError())) {
		    resultData.setStatus(Status.CREATED);
		    resultData.setUrl((String)requestBody.get("urlLocation"));
		    resultData.setName((String)requestBody.get("_id"));
		}
		
		return resultData;
	}
}
