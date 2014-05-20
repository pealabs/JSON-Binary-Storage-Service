package system.restful.accessories.aspect;

import java.util.HashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import system.restful.accessories.utils.ResourceUtil;
import system.restful.accessories.exception.InvalidCertificationException;
import system.restful.persistence.mapper.ObjectsMapper;
import system.restful.persistence.vo.RequestParameter;;

@Component
@Aspect
public class CommonRestAspect
{
	// --------------------------------------------------------------------------
	
	private Logger log = LoggerFactory.getLogger(getClass());
		
	@Autowired
	private ObjectsMapper mapper;
	
	// --------------------------------------------------------------------------

    @Around("@annotation(system.restful.accessories.aspect.AroundCertification)")
    public Object certification(ProceedingJoinPoint pjp) throws Throwable 
    {
    	log.debug("certification");
        //TODO: X-Application-Id 와 X-API-Key 값 존재 여부 확인 존재 하지 않으면 joinPoint.proceed() 처리 하지 않고 메시지 리턴
    	if( isInvalidCertification(pjp.getArgs()) ){
    		throw new InvalidCertificationException();
    	}
    	
    	Object obj = pjp.proceed();
        
        return obj;
    }
    
    private boolean isInvalidCertification(Object[] args) {
    	boolean isInvalidCertification = false;
		for (int i = 0; i < args.length; i++) {
			
 	    	if ( args[i] instanceof RequestParameter ) {
 	    		//TODO : find <issuedInfo> collection
 	    		isInvalidCertification = 
 	    				mapper.isInvalidCertification(( RequestParameter ) args[i]);
 	        }
 	    }
		
		log.debug(String.format("isInvalidCertification : %s", isInvalidCertification));
		
		return isInvalidCertification;
    }
    
    @Around("@annotation(system.restful.accessories.aspect.AroundLogs)")
    public Object logs(ProceedingJoinPoint pjp) throws Throwable 
    {
    	Object obj = pjp.proceed();
    	
        //TODO: 처리 완료 Response 내용 해체 후 로그로 남김
    	log.debug("logs");
        return obj;
    }
    
    @Around("@annotation(system.restful.accessories.aspect.AroundPreProcessing)")
    public Object aroundPreprocessing(ProceedingJoinPoint pjp) throws Throwable 
    {
    	log.debug("AroundPreprocessing");
    	//TODO: 예약어가 요청에 포함 되어 있는지 확인. 존재하면 삭제
    	@SuppressWarnings("unused")
		Object[] args = removingReserved(pjp.getArgs());
    	
    	Object obj = pjp.proceed();
	    
	    return obj; 
    }
    
    @SuppressWarnings("unchecked")
	private Object[] removingReserved(Object[] args) {
    	HashMap<String,Object> map = null;
    	for (int i = 0; i < args.length; i++) {
 	    	if ( args[i] instanceof HashMap ) {
 	    		map = ( HashMap<String, Object> ) args[i];
 	    		args[i] = ResourceUtil.removingReserved(map, "objectId", "_id", "createAt", "updateAt"); 
 	        }
 	    }
    	return args;
    }
    
    
    @Around("@annotation(system.restful.accessories.aspect.AroundPostProcessing)")
    public Object aroundPostprocessing(ProceedingJoinPoint pjp) throws Throwable 
    {
    	Object obj = pjp.proceed();
    	log.debug("AroundPostprocessing");
	    return obj; 
    }
    
}