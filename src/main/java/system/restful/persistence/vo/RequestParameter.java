package system.restful.persistence.vo;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import system.restful.accessories.constants.SortOrder;



public class RequestParameter {
	@DefaultValue("") @HeaderParam("X-Application-Id")
	private String appId;
	@DefaultValue("") @HeaderParam("X-API-Key")
	private String apiKey;
	@DefaultValue("") @PathParam("objectName")
	private String objectName;
	@DefaultValue("") @PathParam("objectId")
	private String objectId;
	@DefaultValue("") @PathParam("fileName")
	private String fileName;
	@DefaultValue("0") @QueryParam("skip")
	private int skip;
	@DefaultValue("100") @QueryParam("limit")
	private int limit;
	@DefaultValue(SortOrder.DESC) @QueryParam("sort")
	private String sort;
	@DefaultValue("") @QueryParam("where")
	private String where;
	
	public boolean isValidEssentialParameter() {
		boolean isValid = true;
		if(appId.isEmpty()
				|| apiKey.isEmpty()
				|| objectName.isEmpty()) {
			isValid = false;
		}
		return isValid;
	}
	
	public String toString() {
		return String.format("appid=%s, apiKey=%s, objectName=%s, objectId=%s, fileName=%s, skip=%s, limit=%s, sort=%s, where=%s", appId, apiKey, objectName, objectId, fileName, skip, limit, sort, where);
	}
	
	public String getCollectionName() {
		return new StringBuilder().append(getAppId()).append("|").append(getObjectName()).toString();
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
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	
}
