package system.restful.accessories.security;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
 


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
 
public class ResponseCorsFilter implements ContainerResponseFilter {
	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	/*
	 * ref's
	 * 
	 * http://paransilverlight.tistory.com/155
	 * 
	 * http://breadmj.wordpress.com/2013/08/21/implement-cors-using-spring-mvc/
	 * 
	 */
	 @Override
	    public ContainerResponse filter(ContainerRequest creq, ContainerResponse cres) {
	        cres.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
	        cres.getHttpHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, X-Application-Id, X-API-Key");
	        cres.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
	        cres.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	        cres.getHttpHeaders().add("Access-Control-Max-Age", "1209600");
	        return cres;
	    }
}