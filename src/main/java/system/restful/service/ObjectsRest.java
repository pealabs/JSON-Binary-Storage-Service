package system.restful.service;

import java.net.URI;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.api.core.InjectParam;

import system.restful.accessories.aspect.AroundCertification;
import system.restful.accessories.aspect.AroundLogs;
import system.restful.accessories.aspect.AroundPostProcessing;
import system.restful.accessories.aspect.AroundPreProcessing;
import system.restful.accessories.utils.ResourceUtil;
import system.restful.accessories.constants.RestfulPath;
import system.restful.persistence.mapper.ObjectsMapper;
import system.restful.persistence.vo.RequestParameter;
import system.restful.persistence.vo.ResultData;



@Component
@Scope("request")
@Path(RestfulPath.OBJECTS_ROOT_V1)
public class ObjectsRest {
	
	/*
	 * ref's
	 * 
	 * https://docs.pealabs.net/
	 * 
	 * examples
	 * 	REST with Java (JAX-RS) using Jersey - Tutorial
	 * 	http://www.vogella.com/articles/REST/article.html
	 * 
	 * 	Extracting Request Parameters
	 * 	http://docs.oracle.com/cd/E19776-01/820-4867/6nga7f5np/index.html
	 * 
	 * 	Get HTTP Header In JAX-RS
	 * 	http://www.mkyong.com/webservices/jax-rs/get-http-header-in-jax-rs/
	 * 
	 * 	JAX-RS @QueryParam Example
	 * 	http://www.mkyong.com/webservices/jax-rs/jax-rs-queryparam-example/
	 * 
	 * 	Jersey Docs
	 * 	https://jersey.java.net/documentation/latest/uris-and-links.html
	 * 
	 * 	Java EE 7 Docs
	 * 	http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Response.html
	 * 	http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Response.ResponseBuilder.html
	 */

	// --------------------------------------------------------------------------
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	// --------------------------------------------------------------------------

	@Context UriInfo uriInfo;
	
	// --------------------------------------------------------------------------
	
	@Autowired
	private ObjectsMapper mapper;

	
	// --------------------------------------------------------------------------
	
	/**
	 * 
	 **/
	private URI getLocation() {
		return getLocation( "" );
	}
	/**
	 * 
	 **/
	@SuppressWarnings("unused")
	private URI getLocation( Long id ) {		
		return getLocation( "" + id );
	}
	/**
	 * 
	 **/
	private URI getLocation( String add ) {
		URI uri = null;
		
		if( uriInfo != null ) {
			UriBuilder ub = uriInfo.getAbsolutePathBuilder();
			uri = ub.path( add ).build();
		}
		
		return uri;
	}
	
	// --------------------------------------------------------------------------

	/**
	 * 
	 **/
	@AroundCertification
	@AroundPreProcessing
	@AroundPostProcessing
	@AroundLogs
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.FIND_ONE)
	public Response findOne(
			@InjectParam RequestParameter requestParameter
			) throws Throwable {
		
		ResultData<?> resultData
					= mapper.findOne( requestParameter );

		return Response
				.status( resultData.getStatus() )
				.entity( resultData.getResponseJSON() )
				.header( "Allow", "GET" )
				.location( getLocation() )
				.build();
		
	}
	/**
	 * 
	 **/
	@AroundCertification
	@AroundPreProcessing
	@AroundPostProcessing
	@AroundLogs
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.FIND)
	public Response find(
			@InjectParam RequestParameter requestParameter
			) throws Throwable {

		ResultData<?> resultData
					= mapper.find( requestParameter );

		
		return Response
			.status( resultData.getStatus() )
			.entity( resultData.getResponseJSON() )
			.header( "Allow", "GET" )
			.location( getLocation() )
			.build();
		
	}
	/**
	 * @throws JsonProcessingException 
	 * @throws Exception 
	 * 
	 **/
	@AroundCertification
	@AroundPreProcessing
	@AroundPostProcessing
	@AroundLogs
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.INSERT)
	public Response insert( 
			@InjectParam RequestParameter requestParameter
			, HashMap<String,Object> requestBody 
			) throws Throwable {
		
		String date = ResourceUtil.utcTimeToString();
    	String objectId = ResourceUtil.generatId(9);
    	
    	requestBody.put( "_id", objectId );
    	requestBody.put( "createAt", date );
    	requestBody.put( "updateAt", date );
		
    	
    	ResultData<?> resultData = mapper.insert( requestParameter
								, requestBody );
		
		
		return Response
				.status( resultData.getStatus() )
				.entity( resultData.getResponseJSON() )
				.header( "Allow", "POST" )
				.location( getLocation() )
				.build();
				
	}
	
	
	/**
	 * 
	 **/
	@AroundCertification
	@AroundPreProcessing
	@AroundPostProcessing
	@AroundLogs
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.UPDATE)
	public Response update(
			@InjectParam RequestParameter requestParameter 
			, HashMap<String,Object> requestBody 
			) throws Throwable {
		
		String date = ResourceUtil.utcTimeToString();
		
		requestBody.put( "updateAt", date );
		
		ResultData<?> resultData 
					= mapper.update( requestParameter
								, requestBody );
		
		return Response
				.status( resultData.getStatus() )
				.entity( resultData.getResponseJSON() )
				.header( "Allow", "PUT" )
				.location( getLocation() )
				.build();
	}
	/**
	 * 
	 **/
	@AroundCertification
	@AroundPreProcessing
	@AroundPostProcessing
	@AroundLogs
	@DELETE
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(RestfulPath.DELETE)
	public Response delete(
			@InjectParam RequestParameter requestParameter
			) throws Throwable {
		
		ResultData<?> resultData 
						= mapper.remove( requestParameter );
		
		return Response
				.status( resultData.getStatus() )
				.entity( resultData.getResponseJSON() )
				.header( "Allow", "DELETE" )
				.location( getLocation() )
				.build();
		
	}
	
}
