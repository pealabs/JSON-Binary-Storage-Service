package system.restful.accessories.exception.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonParseException;

@Provider
public class JsonParseExceptionMapper  implements ExceptionMapper<JsonParseException> {
    
	/**
	 * 
	 * 기본적으론 500 에러가 되나 요청된 구문 오류에 의해 발생한 에러 이므로 400 에러로 전환 시킨다.
	 * **/
	@Override
    public Response toResponse(JsonParseException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
        		.entity("{\"error\":\"OOPS!! Expected to be a syntax error. Please check the details of the request.\"}")
        		.type("application/json").build();
    }
}