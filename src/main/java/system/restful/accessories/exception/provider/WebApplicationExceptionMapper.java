package system.restful.accessories.exception.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper  implements ExceptionMapper<WebApplicationException> {
    
	private final static int NOT_FOUND = 404;
	private final static int METHOD_NOT_ALLOWED = 405;
	private final static int INTERNAL_SERVER_ERROR = 500;
	
	@Override
    public Response toResponse(WebApplicationException exception) {
    	
    	Response r = exception.getResponse();
	    if (r.getStatus() == METHOD_NOT_ALLOWED) {
	    	return Response.fromResponse(r)
	    			.entity("{\"error\":\"OOPS!! The request could not be performed.\"}")
	        		.type("application/json").build();
	    }
	    if (r.getStatus() == NOT_FOUND) {
		    return Response.fromResponse(r)
		    		.entity("{\"error\":\"OOPS!! The requested could not be found.\"}")
		        	.type("application/json").build();
		}
	    if (r.getStatus() == INTERNAL_SERVER_ERROR) {
	    	return Response.fromResponse(r)
	          		.entity("{\"error\":\"OOPS!! Server Busy.\"}")
	          		.type("application/json").build();
		}
	    
	  
	    return r;
    	
    	
    	
        		
    }
}


