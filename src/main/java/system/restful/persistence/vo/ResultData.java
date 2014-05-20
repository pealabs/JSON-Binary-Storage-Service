package system.restful.persistence.vo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import system.restful.accessories.utils.ResourceUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultData<T> implements Serializable {

	// --------------------------------------------------------------------------
	private transient static final long serialVersionUID = -5452958925419083369L;
	
	@SuppressWarnings("unused")
	private transient Logger log = LoggerFactory.getLogger(getClass());
	// --------------------------------------------------------------------------
	
	private transient Status status;
	private String objcetId;
	private String createAt;
	private String updateAt;
	private String appName;
	private String appId;
	private String apiKey;
	private String url;
	private String name;
	private T result;
	
	// --------------------------------------------------------------------------
	
	public ResultData() {
		
	}
	
	
	public String getAppName() {
		return appName;
	}

	
	public void setAppName(String appName) {
		this.appName = appName;
	}

	
	public String getAppId() {
		return appId;
	}

	
	public void setAppId(String appId) {
		this.appId = appId;
	}

	
	public String getApiKey() {
		return apiKey;
	}

	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	
	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	public String getObjcetId() {
		return objcetId;
	}


	public void setObjcetId(String objcetId) {
		this.objcetId = objcetId;
	}


	public String getCreateAt() {
		return createAt;
	}


	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}


	public String getUpdateAt() {
		return updateAt;
	}


	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}

	
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	// --------------------------------------------------------------------------

	public T getResult() {
		return result;
	}


	public void setResult(T result) {
		this.result = result;
	}



	// --------------------------------------------------------------------------

	public String getResponseJSON() throws IllegalArgumentException, IllegalAccessException, JsonProcessingException{
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		
		 Field[] allFields = this.getClass().getDeclaredFields();
		 for (Field field : allFields) {
	        if (!Modifier.isTransient(field.getModifiers())) {
		           Object value = field.get(this);
		           if(!"".equals(ResourceUtil.nvl(value))){
						resultMap.put(field.getName(), value);
				   }
	        }
		 }
		
		return (new ObjectMapper().writeValueAsString(resultMap)).replaceAll("_id", "objectId");
			
	}
	
}
