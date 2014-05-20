package system.restful.accessories.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
 

public class FileUtils {
	  /**
	   * 문자열 -> byte배열. WAS별로 인코딩이 달라질수 있어 하나로 통일
	   * @param str 문자열
	   * @return byte배열
	   */
	  public static byte[] getBytesFromString(String str)
	  {
	      byte[] b = null;
	      try {
	          b = str.getBytes("euc-kr");
	      }
	      catch(UnsupportedEncodingException ex){}
	      return b;
	  }
    /**
     * 문자열을 InputStream 으로 반환 ( 인코딩은 StringUtil.getBytesFromString 참조 )
     * @param str 문자열
     * @return
     */
    public static InputStream getTextInputStream( String str  )
    {
        byte[] b = getBytesFromString(str);
        return new ByteArrayInputStream(b);
    }
	
	public static void generalizePath( String[] paths )
	{
		for(int i=0;i<paths.length;i++)
			paths[i] = generalizePath(paths[i]);
	}
    
    /**
     * 디렉터리 경로 구분자 정리(ex c:\wwwroot/xx.jsp -> c:\wwwroot\xx.jsp)
     * @param path 디렉터리
     * @return 정리된 디렉터리
     */
    public static String generalizePath( String path )
    {
        int lp=0;
        StringBuffer sb = new StringBuffer();
        int l = path.length();
        for(int i=0;i<l;i++) {
            char ch = path.charAt(i);
            if(ch=='\\'||ch=='/') {
                //sb.append('\\');
                sb.append(File.separatorChar);
                while(i<l-1 && (path.charAt(i+1)=='\\'||path.charAt(i+1)=='/'))
                    i++;
            }
            else
                sb.append(ch);
        }
        return sb.toString();
    }
    
    /**
     * 파일 존재 유무
     * @param fileName 파일이름
     * @return 존재유무
     */
    public static boolean isExist( String fileName )
    {
        boolean ise = true;
        try {
            FileInputStream fis = new FileInputStream(generalizePath(fileName));
        } catch (FileNotFoundException e) {
            ise = false;
        }
        return ise;
    }
    
    /**
     * byte 배열로 파일 생성
     * @param fileName 파일이름
     * @param buffer 파일내용
     * @throws IOException
     */
    public static void writeBinary( String fileName, byte[] buffer ) throws IOException
    {
        File f = new File(generalizePath(fileName));
        FileOutputStream fis = new FileOutputStream( f );
        
        DataOutputStream din = new DataOutputStream(fis);
        din.write(buffer);
        din.flush();
        din.close();
        fis.close();
    }
    
    /**
     * 파일 전체를 읽어 byte 배열로 반환
     * @param fileName 파일이름
     * @return byte배열
     * @throws IOException
     */
    public static byte[] readBinary( String fileName ) throws IOException
    {
        File f = new File(generalizePath(fileName));
        FileInputStream fis = new FileInputStream( f );
        byte[] buffer = new byte[(int)f.length()];
        
        DataInputStream din = new DataInputStream(fis);
        for(int i=0;i<buffer.length;i++)
            buffer[i] = (byte)din.read();
        din.close();
        fis.close();
        return buffer;
    }
	/**
	 * 파일을 읽어서 문자열로 반환
	 * @param fileName 파일이름
	 * @return 파일내용
	 * @throws IOException
	 */
  public static String readText( String fileName ) throws IOException
  {
    BufferedReader br = new BufferedReader( new FileReader(generalizePath(fileName)));

    String s;
    StringBuffer sb=new StringBuffer();
    while((s=br.readLine())!=null) {
      sb.append(s).append('\n');
      
    }
    br.close();
    return sb.toString();
   // return new String(sb.toString().getBytes("8859_1"),"euc-kr");
  }

  /**
   * 텍스트파일 생성
   * @param fileName 파일이름
   * @param doc 내용
   * @throws IOException
   */
  public static void writeText( String fileName, String doc ) throws IOException
  {
    BufferedWriter wr = new BufferedWriter( new FileWriter(generalizePath(fileName) ));

    wr.write( doc, 0, doc.length());
    wr.flush();
    wr.close();
  }
  
  /**
   * 텍스트파일 덮어쓰기
   * @param fileName 파일이름
   * @param doc 내용
   * @throws IOException
   */
  public static void overWriteText( String fileName, String doc ) throws IOException
  {
    BufferedWriter wr = new BufferedWriter( new FileWriter(generalizePath(fileName),true ));

    wr.write( doc, 0, doc.length());
    wr.flush();
    wr.close();
  }

  /**
   * 텍스트파일 생성 ( OutputStream 사용 )
   * OutputStream 에 파일 내용을 씀. 파일을 생성하는 대신 response에 직접 쓰는 경우 유용
   * @param fileName 파일이름
   * @param os OutputStream
   * @throws IOException
   */
  public static void writeFile( String fileName, OutputStream os ) throws IOException
  {
    FileInputStream fs = new FileInputStream( generalizePath(fileName) );
    int no;
    byte[] buffer=new byte[65536];
    while((no = fs.read(buffer))>0)
      os.write(buffer,0,no);
    fs.close();
  }






/*
  private static void addFileToZip( ZipOutputStream zs, String fileName ) throws IOException
  {
    File file = new File(fileName);
    String _fileName = file.getName();

    ZipEntry ze = new ZipEntry( _fileName );

    ze.setTime( file.lastModified());

    zs.putNextEntry( ze );

    byte[] buffer=new byte[65536];

    FileInputStream fs = new FileInputStream(file);
    int no;
    while(( no=fs.read(buffer))>=0) {
      zs.write(buffer,0,no);
    }

    zs.closeEntry();
  }

  public static void makeZipSimple( String fileName, Object[] srcFiles ) throws IOException
  {
    ZipOutputStream zs = new ZipOutputStream( new FileOutputStream(fileName) );


    for(int i=0;i<srcFiles.length;i++) {
      addFileToZip(zs, srcFiles[i].toString());
    }
    zs.flush();
    zs.close();
  }
*/

  /**
   * 파일에 대해 SHA1 해쉬값 계산
   * @param fileName 파일이름
   * @return SHA1값(byte배열)
   * @throws IOException
   */
  public static byte[] getHashSHA1( String fileName )
  {
    byte[] hash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      FileInputStream fs = new FileInputStream(generalizePath(fileName));
      byte[] buff = new byte[65536];
      int r;
      while ( (r = fs.read(buff)) >= 0) {
        md.update(buff, 0, r);
      }
      fs.close();
      fs = null;
      hash=md.digest();
    }
    catch(Exception ex) {
    }

    return hash;
  }

  /**
   * 디렉터리가 있는지 검사. 없으면 생성
   * @param dir 디렉터리
   * @throws IOException
   */
  public static void ensureDirectory( String dir ) throws IOException
  {
    File f = new File( generalizePath(dir) );
    if(!f.isDirectory())
      if(!f.mkdirs())
        throw new IOException("[electria] directory creation failure");
  }

  /**
   * 디렉터리의 모든 파일을 지움
   * @param dir 디렉터리
   * @throws IOException
   */
  public static void clearDirectory( String dir ) throws IOException
  {
    File f = new File( generalizePath(dir) );
    File[] list = f.listFiles();
    if(list!=null)
      for(int i=0;i<list.length;i++) {
          
        list[i].delete();
      }
  }
  
  /**
   * 파일 삭제
   * @param file 파일명
   * @throws IOException
   */
  public static void deleteFile( String file) throws IOException
  {
      File f = new File( generalizePath(file) );
      if(f!=null ) 
          f.delete();
  }

  /**
   * 디렉터리 삭제 ( 파일 삭제후 디렉터리 삭제 )
   * @param dir 디렉터리
   * @throws IOException
   */
  public static void deleteDirectory( String dir ) throws IOException
  {
    File f = new File( generalizePath(dir) );
    
    if(f!=null && f.isDirectory()) {
        File[] list = f.listFiles();
        if(list!=null)
          for(int i=0;i<list.length;i++) {
              if(list[i].isDirectory())
                  deleteDirectory(dir+"\\"+list[i].getName());
            list[i].delete();
          }
        f.delete();
    }
  }

  /**
   * 파일 복사
   * @param destFileName 대상파일이름
   * @param srcFileName 원본파일이름
   * @throws IOException
   */
  public static void copy( String destFileName, String srcFileName ) throws IOException
  {
    byte[] buffer= new byte[65536];
    int n;

    FileInputStream is = new FileInputStream(generalizePath(srcFileName));
    FileOutputStream os = new FileOutputStream(generalizePath(destFileName),false);
    while((n=is.read(buffer))>0)
      os.write(buffer,0,n);
    os.close();
    is.close();
  }
  
  public static void copyDirectory(File sourcelocation, File targetdirectory) throws IOException {
	     //디렉토리인 경우
      if (sourcelocation.isDirectory()) {               
              //복사될 Directory가 없으면 만듭니다.
          if (!targetdirectory.exists()) {
              targetdirectory.mkdir();
          }
          
          String[] children = sourcelocation.list();
          for (int i=0; i<children.length; i++) {
              copyDirectory(new File(sourcelocation, children[i]),
                      new File(targetdirectory, children[i]));
          }
      } else {
          //파일인 경우
          InputStream in = new FileInputStream(sourcelocation);                
                  OutputStream out = new FileOutputStream(targetdirectory);
          
          // Copy the bits from instream to outstream
          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0) {
              out.write(buf, 0, len);
          }
          in.close();
          out.close();
      }
	 }

  

  /**
   * 파일명 추출
   * @param fullFileName 경로 포함된 파일명
   * @return 파일명
   */
  public static String extractName( String fullFileName )
  {
      int p = fullFileName.lastIndexOf('/');
      if(p<0)
          p = fullFileName.lastIndexOf('\\');
      return p<0?fullFileName:fullFileName.substring(p+1);
  }
  
  /**
   * 파일내용 추출
   * @param strFilename 경로 포함된 파일명
   * @return 내용
   */
  public static String getFileList(String strFilename){
	  File f = null;
	  String strFiles="";
	  
	                try {
	                        f = new File(strFilename);
	                        File[] files = f.listFiles();

	                        for(int i=0; i< files.length;i++){
	                            if (files[i].isFile()){
	                                strFiles = strFiles+","+files[i].getName();
	                            }
	                        }  
	                        strFiles = strFiles.substring(1);
	                        return strFiles ;
	                } catch(Exception e) {
	                   e.printStackTrace();
	                   return strFiles;
	                  }
 }


  
  /**
   * 경로 추출
   * @param fullFileName 경로/파일명
   * @return 경로
   */
  public static String extractPath( String fullFileName )
  {
      int p = fullFileName.lastIndexOf('/');
      if(p<0)
          p = fullFileName.lastIndexOf('\\');
      return p<0?fullFileName:fullFileName.substring(0,p);
  }
  
  
  private static String[][] mimeMapping = {
      { ".html", "text/html" },
      { ".aif", "audio/x-aiff" },
      { ".aiff", "audio/x-aiff" },
      { ".aifc", "audio/x-aiff" },
      { ".ai", "application/postscript" },
      { ".au", "audio/basic" },
      { ".asc", "text/plain" },
      { ".asf", "video/x-ms-asf" },
      { ".asx", "video/x-ms-asf" },
      { ".avi", "video/x-msvideo" },

      { ".bin", "application/octet-stream" },
      { ".bcpio", "application/x-bcpio" },
      { ".bmp", "image/bmp" },

      { ".class", "application/octet-stream" },
      { ".cpt", "application/mac-compactpro" },
      { ".css", "text/css" },
      { ".cpio", "application/x-cpio" },
      { ".csh", "application/x-csh" },
      { ".cdf", "application/x-netcdf" },

      { ".dms", "application/octet-stream" },
      { ".doc", "application/msword" },
      { ".dcr", "application/x-director" },
      { ".dir", "application/x-director" },
      { ".dxr", "application/x-director" },
      { ".dvi", "application/x-dvi" },

      { ".exe", "application/octet-stream" },
      { ".eps", "application/postscript" },
      { ".etx", "text/x-setext" },

      { ".gtar", "application/x-gtar" },
      { ".gif", "image/gif" },
      { ".gz", "application/octet-stream" },

      { ".hdml", "text/x-hdml" },
      { ".hqx", "application/mac-binhex40" },
      { ".html", "text/html" },
      { ".htm", "text/html" },
      { ".hdf", "application/x-hdf" },

      { ".ief", "image/ief" },
      { ".ice", "x-conference/x-cooltalk" },

      { ".jar", "application/java-archive" },
      { ".jnlp", "application/x-java-jnlp-file" },
      { ".jpeg", "image/jpeg" },
      { ".jpg", "image/jpeg" },
      { ".jpe", "image/jpeg" },
      { ".js", "application/x-javascript" },

      { ".kar", "audio/midi" },

      { ".latex", "application/x-latex" },
      { ".lha", "application/octet-stream" },
      { ".lhz", "application/octet-stream" },

      { ".mid", "audio/midi" },
      { ".mpeg", "video/mpeg" },
      { ".mpg", "video/mpeg" },
      { ".mpe", "video/mpeg" },
      { ".mov", "video/quicktime" },
      { ".movie", "video/x-sgi-movie" },
      { ".mpga", "audio/mpeg" },
      { ".mp2", "audio/mpeg" },
      { ".mp3", "audio/mpeg" },
      { ".man", "application/x-troff-man" },
      { ".me", "application/x-troff-me" },
      { ".ms", "application/x-troff-ms" },

      { ".nc", "application/x-netcdf" },

      { ".oda", "application/oda" },

      { ".pdf", "application/pdf" },
      { ".ps", "application/postscript" },
      { ".ppt", "application/vnd.ms-powerpoint" },
      { ".png", "image/png" },
      { ".pgn", "application/x-chess-pgn" },
      { ".pnm", "image/x-portable-anymap" },
      { ".pbm", "image/x-portable-bitmap" },
      { ".pgm", "image/x-portable-graymap" },
      { ".ppm", "image/x-portable-pixmap" },

      { ".qt", "video/quicktime" },

      { ".rtf", "application/rtf" },
      { ".ram", "audio/x-pn-realaudio" },
      { ".rm", "audio/x-pn-realaudio" },
      { ".rpm", "audio/x-pn-realaudio-plugin" },
      { ".ra", "audio/x-realaudio" },
      { ".ras", "image/x-cmu-raster" },
      { ".rgb", "image/x-rgb" },
      { ".rtx", "text/richtext" },
      { ".rtf", "text/rtf" },

      { ".smi", "application/smil" },
      { ".smil", "application/smil" },
      { ".sml", "application/smil" },
      { ".skp", "application/x-koan" },
      { ".skd", "application/x-koan" },
      { ".skt", "application/x-koan" },
      { ".skm", "application/x-koan" },
      { ".src", "application/x-wais-source" },
      { ".sh", "application/x-sh" },
      { ".shar", "application/x-shar" },
      { ".swf", "application/x-shockwave-flash" },
      { ".sit", "application/x-stuffit" },
      { ".spl", "application/x-futuresplash" },
      { ".sv4cpio", "application/x-sv4cpio" },
      { ".sv4crc", "application/x-sv4crc" },
      { ".snd", "audio/basic" },
      { ".sgml", "text/sgml" },
      { ".sgm", "text/sgml" },

      { ".t", "application/x-troff" },
      { ".tar", "application/x-tar" },
      { ".tcl", "application/x-tcl" },
      { ".tex", "application/x-tex" },
      { ".texi", "application/x-texinfo" },
      { ".texinfo", "application/x-texinfo" },
      { ".tgz", "application/octet-stream" },
      { ".tiff", "image/tiff" },
      { ".tif", "image/tiff" },
      { ".torrent", "application/x-bittorrent" },
      { ".tr", "application/x-troff" },
      { ".troff", "application/x-troff" },
      { ".tsv", "text/tab-separated-values" },
      { ".txt", "text/plain" },

      { ".ustar", "application/x-ustar" },

      { ".vcd", "application/x-cdlink" },
      { ".vrml", "model/vrml" },

      { ".wav", "audio/x-wav" },
      { ".wax", "audio/x-ms-wax" },
      { ".wrl", "model/vrml" },
      { ".wma", "audio/x-ms-wma" },
      { ".wml", "text/vnd.wap.wml" },
      { ".wmls", "text/vnd.wap.wmlscript" },
      { ".wmlc", "application/vnd.wap.wmlc" },
      { ".wmlsc", "application/vnd.wap.wmlscript" },
      { ".wbmp", "image/vnd.wap.wbmp" },

      { ".xls", "application/vnd.ms-excel" },
      { ".xbm", "image/x-xbitmap" },
      { ".xpm", "image/x-xpixmax" },
      { ".xwd", "image/x-xwindowdump" },
      { ".xml", "text/xml" },

      { ".zip", "application/zip" },
      { ".z", "application/octet-stream" }
  };
  
  private static Hashtable mimeMap = new Hashtable();
  
  static {
      for(int i=0;i<mimeMapping.length;i++)
          mimeMap.put( mimeMapping[i][0],mimeMapping[i][1]);
  }

  /**
   * 확장자로 mime-type 알아내기
   * @param fileName 파일명
   * @return mime-type
   */
  public static String getMimeTypeByExt( String fileName )
  {
      if(fileName!=null) {
          int p = fileName.lastIndexOf('.');
          if(p>=0) {
              String ext = fileName.substring(p).toLowerCase();
              p = ext.indexOf('?');
              if(p>0)
                  ext = ext.substring(0,p-1);
              return (String)mimeMap.get( ext );
          }
      }

      return null;
  }
  
  private static void listAllFiles( String path, ArrayList al, boolean scanSubDir )
  {
      File f = new File(generalizePath(path));
      File[] allf = f.listFiles();
      if(allf==null)
          return;
      for(int i=0;i<allf.length;i++) {
          File cf = allf[i];
          if(cf.isDirectory()) {
              if(scanSubDir)
                  listAllFiles(path+cf.getName()+"\\",al, true);
          }
          else {
              al.add( path+cf.getName() );
          }
      }
  }

  /**
   * 파일목록 가져오기
   * @param path 경로
   * @param scanSubDir 서브 디렉터리까지?
   * @return 파일목록
   */
  public static ArrayList listAllFiles( String path, boolean scanSubDir )
  {
      int lc =path.charAt(path.length()-1);
      if(lc!='\\' && lc!='/')
          path=path+"\\";
      
      ArrayList al = new ArrayList();
      listAllFiles( path, al, scanSubDir);
      return al;
  }
 
  /**
   * 파일명을 폴더명과 함께 리턴한다.
   * 
   * parentPath/prefix/2008/11/28/fileName.ext  ex) c:/images/QCIF/2008/11/28/test
   * 
   * @param parentPath
   * @param fileName
   * @param prefix
   * @param ext
   * @return
   */
  public static String getTimeString() {
      SimpleDateFormat sdf = null;
      sdf = new SimpleDateFormat("HHmm");
      return sdf.format(new java.util.Date());
  }

  public static String generateFileName(String parentPath, String fileName, String prefix, String ext) throws IOException
  {
  	Calendar cal = Calendar.getInstance();
	
	String newDir = parentPath + "/" + cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+prefix+"/";
	String reTime = getTimeString().substring(0,3)+"0";
	String time = "_"+reTime;
	String destFileName = newDir+fileName+time+ext;
	
	if( !"".equals(parentPath) ){
		FileUtils.ensureDirectory(newDir);
	}
	
	return destFileName;
  }
 
  public static int generateDir(String parentPath, String prefix) throws IOException
  {
  	Calendar cal = Calendar.getInstance();
	String newDir = parentPath + "/" + cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+prefix+"/";
	  
	if( !"".equals(parentPath) ){
		FileUtils.ensureDirectory(newDir);
	}
	 
	return 0;
  }
  
  public void copyAndExit( String destFileName, String srcFileName ) throws IOException
  {
	  DataInputStream in;
	  DataOutputStream out;

      in = new DataInputStream(new FileInputStream(srcFileName));
      out  = new DataOutputStream(new FileOutputStream(destFileName));

      byte[] buff = new byte[1024];
      int size;

      while((size = in.read(buff)) > -1)
           out.write(buff, 0, size);

      out.close();
      in.close();

  }  
  /**
   * 특정서버의 파일을 받아 저장한다.
   * 
   * 
   * @param host 서버 url (host + url + file_name에서 파일 받아온다)
   * @param url 서버의 파일 경로
   * @param file_name 파일명
   * @param to_dir ( to_dir+file_name에 받아온 내용 저장한다)
   * @throws IOException
   * @return
   */

	public static void downLoadFile( String host ,String url, String file_name, String to_dir )throws IOException{			

		URL http_url =null;
		InputStream fin = null;
		OutputStream fos = null;
		try 
		{  			 	
			http_url = new URL(host+url+file_name);		
			fin = http_url.openStream();	
			FileUtils.deleteFile(to_dir+file_name);
			File copy_file = new File(to_dir+file_name);			
			fos = new FileOutputStream(copy_file);			
			while(true){
				int data = fin.read(); 				
				if(data == -1){					
					break;
				}	
				fos.write(data);   			 				
			}
			fos.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//System.out.println("배포서버 값 못받아옴 : " + to_dir + file_name);
		}  
	    finally{
	    	try{
		    	if(fin != null)fin.close();
		    	if(fos != null){
		    		fos.flush();
		    		fos.close();		 	
		    	}	
	    	}catch(Exception e){
	    		
	    		
	    	}
		}
	 
	}
  /**
   * 파일이 정상적으로 다운로드 되었는지 체크함
   * 
   * 
   * @param down_check 한번만 처리되도록 파라메터를 받음(true인경우에만 체크)
   * @throws IOException
   * @return
   */
	public static boolean downLoadFileCheck( boolean down_check, String file_name )throws IOException{			
		if(down_check==true){
			down_check = FileUtils.isExist(file_name);
			if(down_check == false){
				//System.out.println(file_name + "을 다운로드 하지 못했습니다.");
			}
		}
		
		return down_check;
	}

	
};
