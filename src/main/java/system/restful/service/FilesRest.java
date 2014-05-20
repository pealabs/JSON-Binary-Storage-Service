package system.restful.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.multipart.MultiPart;

import system.restful.accessories.utils.FileUtils;
import system.restful.accessories.utils.PropertiesLoader;
import system.restful.accessories.aspect.AroundCertification;
import system.restful.accessories.aspect.AroundLogs;
import system.restful.accessories.aspect.AroundPostProcessing;
import system.restful.accessories.aspect.AroundPreProcessing;
import system.restful.accessories.constants.RestfulPath;
import system.restful.accessories.exception.ExceedsAllowedSizeException;
import system.restful.accessories.utils.ResourceUtil;
import system.restful.persistence.mapper.FilesMapper;
import system.restful.persistence.mapper.ObjectsMapper;
import system.restful.persistence.vo.RequestParameter;
import system.restful.persistence.vo.ResultData;


@Component
@Scope("request")
@Path(RestfulPath.FILES_ROOT_V1)
public class FilesRest {
	
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
	
	private static final String SYSTEM_FILES_ROOT = (String) PropertiesLoader.getInstance().get("system.files.root");
	
	private static final Long MAX_UPLOAD_SIZE = 10*1024*1024L;
	
	// --------------------------------------------------------------------------

	@Context UriInfo uriInfo;
	
	// --------------------------------------------------------------------------
	
	@Autowired
	private FilesMapper mapper;

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
	@SuppressWarnings("unchecked")
	@AroundPostProcessing
	@AroundLogs
	@GET
	@Path(RestfulPath.FILES_DOWNLOAD)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(
			@InjectParam RequestParameter requestParameter
			) throws Throwable {

		ResultData<?> resultData 
					= mapper.findOne(requestParameter);
		
		HashMap<String,String> fileInfoMap 
						= ( HashMap<String, String> ) resultData.getResult();
		
		File file = new File(fileInfoMap.get("saveFileLocation"));

		return Response
				.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header( "Allow", "GET" )
				.header("Content-Disposition", genAttachmentFileName(
														fileInfoMap.get("orginFileName")) )
				.location( getLocation() )
				.build();

	}
	
	private String genAttachmentFileName(String fileName){
		return new StringBuilder()
						.append("attachment; filename=\"")
						.append(fileName)
						.append("\"")
						.toString();
	}
	
	@AroundCertification
	@AroundPreProcessing
	@AroundPostProcessing
	@AroundLogs
	@POST
	@Path(RestfulPath.FILES_UPLOAD)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces({ MediaType.APPLICATION_JSON+ ";charset=utf-8" })
	public Response upload(
			@InjectParam RequestParameter requestParameter
			, InputStream uploadedInputStream 
			) throws Throwable
	{
		String fileName = genFileName( requestParameter.getFileName(), UUID.randomUUID() );
		String saveFileLocation = genSaveFileLocation( requestParameter.getAppId(), fileName );
		String urlLocation = genUrlLocation( fileName );
		
		HashMap<String,Object> requestBody = new HashMap<String,Object>();
		ResultData<?> resultData = new ResultData<String>();
		
		if( writeToFile(
				saveFileLocation
				, uploadedInputStream ) ){
			
			String date = ResourceUtil.utcTimeToString();
	    	
	    	requestBody.put( "_id", fileName );
	    	requestBody.put( "orginFileName", requestParameter.getFileName() );
	    	requestBody.put( "appId", requestParameter.getAppId() );
	    	requestBody.put( "saveFileLocation", saveFileLocation );
	    	requestBody.put( "urlLocation", urlLocation );
	    	requestBody.put( "createAt", date );
	    	
	    	resultData = mapper.insert( requestParameter, requestBody );
		}else{
			throw new ExceedsAllowedSizeException();
		}
	  
	    return Response
				.status( resultData.getStatus() )
				.entity( resultData.getResponseJSON() )
				.header( "Allow", "POST" )
				.location( getLocation() )
				.build();
	}
	
	private String genFileName( String fileName, UUID uuid ){
		StringBuilder sb = new StringBuilder();
		sb.append( uuid );
		sb.append( "-" );
		sb.append( fileName );
		return sb.toString();
	}
	private String genSaveFileLocation( String appId, String fileName ) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append( SYSTEM_FILES_ROOT );
		sb.append( FileSystems.getDefault().getSeparator() );
		sb.append( appId );
		sb.append( FileSystems.getDefault().getSeparator() );
		sb.append( ResourceUtil.currentDate() );
		FileUtils.ensureDirectory(sb.toString());
		sb.append( FileSystems.getDefault().getSeparator() );
		sb.append( fileName );
		return sb.toString();
	}
	private String genUrlLocation( String fileName ){
		StringBuilder sb = new StringBuilder();
		sb.append( uriInfo.getBaseUri() );
		sb.append("1/files/download/");
		sb.append( fileName );
		return sb.toString();
	}
	private boolean writeToFile( String uploadedFileLocation, InputStream uploadedInputStream ) {
		boolean isWrite = true;
		FileOutputStream uploadedOutputStream = null;
		try {
			
			uploadedOutputStream = new FileOutputStream( uploadedFileLocation );
		
			uploadedOutputStream.write(getBytes(uploadedInputStream));
		    //IOUtils.copy( uploadedInputStream, uploadedOutputStream );
		    uploadedOutputStream.flush();
		    uploadedOutputStream.close();
		    uploadedInputStream.close();
		} catch (IOException e) {
			if(uploadedOutputStream != null){
				try {
					uploadedOutputStream.close();
				} catch (IOException e1) {
				}
			}
			isWrite = false;
		}
		return isWrite;
	}
	
	private byte[] getBytes(InputStream is) throws IOException {

	    int len;
	    int size = 1024;
	    byte[] buf;

	    if (is instanceof ByteArrayInputStream) {
	      size = is.available();
	      buf = new byte[size];
	      len = is.read(buf, 0, size);
	    } else {
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      buf = new byte[size];
	      while ((len = is.read(buf, 0, size)) != -1){
	        bos.write(buf, 0, len);
	      }
	      buf = bos.toByteArray();
	    }
	    
	    if(buf.length > MAX_UPLOAD_SIZE){
	    	throw new IOException();
	    }
	    
	    return buf;
	  }
}
