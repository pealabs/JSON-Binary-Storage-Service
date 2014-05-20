package system.restful.persistence.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.google.common.collect.Lists;
import com.mongodb.WriteResult;
import com.sun.jersey.api.NotFoundException;

import system.restful.accessories.constants.CollectionNames;
import system.restful.accessories.utils.ResourceUtil;
import system.restful.persistence.vo.RequestParameter;
import system.restful.persistence.vo.ResultData;


@Service
@Transactional
public class ObjectsMapper {

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
	public boolean isInvalidCertification(RequestParameter requestParameter)
	{
		boolean isInvalidCertification = true;
		
		HashMap<String,Object> findOne = 
				getCollection( CollectionNames.APPLICATION_META_INFO_COLLECTION )
				.findOne( "{_id:#, apiKey:#}", requestParameter.getAppId(), requestParameter.getApiKey() )
				.as(HashMap.class);
	
		log.debug(String.format("Request certification info %s %s", requestParameter.getAppId(), requestParameter.getApiKey()));
		
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
				getCollection( requestParameter.getCollectionName() )
				.findOne( "{_id:#}", requestParameter.getObjectId() )
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
	@SuppressWarnings("rawtypes")
	public ResultData<List<HashMap>> find(RequestParameter requestParameter) throws Throwable
	{
		ResultData<List<HashMap>> resultData 
						= new ResultData<List<HashMap>>();

		List<HashMap> list = 
					Lists.newArrayList( 
							getCollection( requestParameter.getCollectionName() )
							.find( requestParameter.getWhere() )
							.skip( requestParameter.getSkip() )
							.limit( requestParameter.getLimit() )
							.sort( requestParameter.getSort() )
							.as(HashMap.class) );
			
			
		resultData.setStatus( Status.ACCEPTED );
		
		if(isExistsData(list)) {
			resultData.setResult( list );
		} else {
			resultData.setResult( new ArrayList<HashMap>() );
		}
			
		return resultData;
	}
	/**
	 * 
	 **/
	public ResultData<String> insert(RequestParameter requestParameter
			, HashMap<String, Object> requestBody) throws Throwable {
		
		ResultData<String> resultData = new ResultData<String>();
		
		
		WriteResult wr =  
				getCollection( requestParameter.getCollectionName() )
				.insert(  ResourceUtil.jsonMapToString( requestBody )  );
	
		if(isNotError(wr.getError())) {
			resultData.setStatus( Status.CREATED );
			resultData.setObjcetId( (String) requestBody.get("_id") );
			resultData.setCreateAt( (String) requestBody.get("createAt") );
		}
		
		return resultData;
	}
	/**
	 * 
	 **/
	public ResultData<String> update(RequestParameter requestParameter
			, HashMap<String,Object> requestBody ) throws Throwable {
		
		ResultData<String> resultData = new ResultData<String>();
		
		StringBuilder sb = new StringBuilder();
		sb.append( "{\"$set\":" );
		sb.append( ResourceUtil.jsonMapToString( requestBody ) );
		sb.append( "}" );
		
		WriteResult wr = 
				getCollection( requestParameter.getCollectionName() )
				.update("{_id:#}", requestParameter.getObjectId() )
				.with( sb.toString() );
		
		if(isNotError(wr.getError())) {
			resultData.setStatus( Status.ACCEPTED );
			if(isExistsApplied(wr.getN())) {
				resultData.setUpdateAt( (String) requestBody.get("updateAt") );
			} else {
				resultData.setResult("Requested ObjectId does not seem to exist.");
			}
		}
		
		return resultData;
	}
	
	/**
	 * 
	 **/
	public ResultData<String> remove(RequestParameter requestParameter) throws Throwable {
		
		ResultData<String> resultData = new ResultData<String>();
		
			
		WriteResult wr = 
				getCollection( requestParameter.getCollectionName() )
				.remove( "{_id:#}", requestParameter.getObjectId() );
		
		if(isNotError(wr.getError())) {
			resultData.setStatus( Status.ACCEPTED );
			if(isExistsApplied(wr.getN())) {
				resultData.setObjcetId( requestParameter.getObjectId() );
			} else {
				resultData.setResult("Requested ObjectId does not seem to exist.");
			}
		} 
		
		return resultData;
	}

	
}
