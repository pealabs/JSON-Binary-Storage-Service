package system.restful.accessories.exception.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import system.restful.accessories.exception.ExceedsAllowedSizeException;
import system.restful.accessories.exception.InvalidCertificationException;


@Provider
public class ExceedsAllowedSizeExceptionMapper  implements ExceptionMapper<ExceedsAllowedSizeException> {
    
	/**
	 * 
	 * 허용된 업로드 크기를 초과. 400 에러로 리턴한
	 * **/
	@Override
    public Response toResponse(ExceedsAllowedSizeException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
        		.entity("{\"error\":\"OOPS!! Exceeds allowed size.\"}")
        		.type("application/json").build();
    }
}