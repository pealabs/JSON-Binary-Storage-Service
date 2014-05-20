package system.restful.accessories.exception.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import system.restful.accessories.exception.InvalidCertificationException;


@Provider
public class InvalidCertificationExceptionMapper  implements ExceptionMapper<InvalidCertificationException> {
    
	/**
	 * 
	 * 기본적으론 500 에러가 되나 요청된 구문 오류에 의해 발생한 에러 이므로 400 에러로 전환 시킨다.
	 * **/
	@Override
    public Response toResponse(InvalidCertificationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
        		.entity("{\"error\":\"OOPS!! Authentication information is not correct.\"}")
        		.type("application/json").build();
    }
}