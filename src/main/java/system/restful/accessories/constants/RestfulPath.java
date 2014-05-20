package system.restful.accessories.constants;

public class RestfulPath {
	public static final String OBJECTS_ROOT_V1 = "/1/objects";
	public static final String FILES_ROOT_V1 = "/1/files";
	
	public static final String INSERT = "{objectName}";
	public static final String FIND = "{objectName}";
	public static final String FIND_ONE = "{objectName}/{objectId}";
	public static final String UPDATE = "{objectName}/{objectId}";
	public static final String DELETE = "{objectName}/{objectId}";
	public static final String ERROR = "error/{code}";
	public static final String SIGN_UP = "common/signup";
	public static final String SIGN_IN = "common/signin/{email}/{password}";
	public static final String APPLICATION_REGISTRATION = "common/application/registraion";
	public static final String APPLICATION_FIND	= "common/application/find";
	public static final String FILES_UPLOAD = "{fileName}";
	public static final String FILES_DOWNLOAD = "download/{fileName}";
}
