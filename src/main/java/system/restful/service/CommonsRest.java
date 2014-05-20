package system.restful.service;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.api.view.Viewable;

import system.restful.accessories.aspect.AroundCertification;
import system.restful.accessories.aspect.AroundLogs;
import system.restful.accessories.aspect.AroundPostProcessing;
import system.restful.accessories.aspect.AroundPreProcessing;
import system.restful.accessories.constants.RestfulPath;
import system.restful.persistence.mapper.CommonsMapper;
import system.restful.persistence.vo.RequestParameter;
import system.restful.persistence.vo.ResultData;


@Component
@Scope("request")
@Path("/")
public class CommonsRest {
	
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
	
	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	
	// --------------------------------------------------------------------------

	@Context UriInfo uriInfo;
	
	// --------------------------------------------------------------------------
	
	@Autowired
	private CommonsMapper mapper;

	// --------------------------------------------------------------------------
	
	/**
	 * 
	 **/
	@SuppressWarnings("unused")
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

    @GET
    public Response index( @Context final HttpServletRequest request,
                                @Context final HttpServletResponse response) throws MalformedURLException{
        return Response.ok(new Viewable("/index")).type(MediaType.TEXT_HTML).build();
    }
    
    
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.SIGN_UP)
	public Response signup( HashMap<String,Object> requestBody ) throws Throwable {
		
		HashMap<String, Object> entityMap = new HashMap<String, Object>();
		
		entityMap.put("status", "Already exists or is invalid input.");
		
		if( mapper.isNotExistOwner( requestBody ) ) {
			if( mapper.insertOwner( requestBody ) ) {
				entityMap.put("status", "Registered.");
			} 
		} 
		
		return Response
				.status( Response.Status.CREATED )
				.entity( new ObjectMapper().writeValueAsString( entityMap ) )
				.header( "Allow", "POST" )
				.location( getLocation() )
				.build();
				
	}
	
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.APPLICATION_REGISTRATION)
	public Response applicationRegistration( HashMap<String,Object> requestBody ) throws Throwable {
		
		HashMap<String, Object> entityMap = new HashMap<String, Object>();
		
		entityMap.put("status", "Owner does not exist or the application name already exists. Check back or sign up.");
		
		if( mapper.isOwner( requestBody ) ) {
			if( mapper.insertApplicationInfo( requestBody ) ) {
				entityMap.put("status", "Registered. Use the lookup feature , check out the registered application.");
			}
		}
		
		return Response
				.status( Response.Status.CREATED )
				.entity( new ObjectMapper().writeValueAsString( entityMap ) )
				.header( "Allow", "POST" )
				.location( getLocation() )
				.build();
				
	}
	
	
	
	/**
	 * 
	 **/
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	@Path(RestfulPath.APPLICATION_FIND)
	public Response find( 
			@Context HttpServletRequest request
		) throws Throwable {

		HashMap<String, Object> entityMap = new HashMap<String, Object>();
		HashMap<String, Object> requestBody = toMap(request.getParameterMap());
		
		ResultData<?> resultData
					= mapper.findApplicationInfo( requestBody );
		
		entityMap.put("status", "Owner does not exist or is invalid input. Check back or sign up.");
		
		if( mapper.isOwner( requestBody ) ) {
			entityMap.put( "status", "Find." );
			entityMap.put( "list", resultData.getResult() );
		}
		
		return Response
				.status( resultData.getStatus() )
				.entity( new ObjectMapper().writeValueAsString( entityMap ) )
				.header( "Allow", "GET" )
				.location( getLocation() )
				.build();
		
	}
	private HashMap<String, Object> toMap(Map<String, String[]> map) {
		HashMap<String, Object> requestBody = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();         // parameter name
            String[] value = entry.getValue();   // parameter values as array of String
            Object valueString = "";
            
            if (value.length > 1) {
                for (int i = 0; i < value.length; i++) {
                    valueString += value[i] + " ";
                }
            } else {
                valueString = value[0];
            }
            requestBody.put(key, valueString);
        }
		return requestBody;
	}
}
