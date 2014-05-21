간단 설치 및 테스트 해보기.

1. gramdb-api/src/main/resources/properties/jdbc.properties 에서 설치 된 mariadb & mongodb 정보를 수정 합니다.
   gramdb-api/src/main/resources/properties/env.properties 에서 파일 업로드 경로를 변경하기 위해
   system.file.root 를 설정 합니다.

   !!!파일 업로드시 악의 적인 webshell을 차단 하기 위해서는 해당 업로드 디렉토리의 실행 권한을 박탈해야 합니다.
   
   !!!유닉스나 리눅스의 경우 chmod 로 변경을 하시고 NT의 경우에는 디렉토리 속성 에서 실행 권한 체크
	    를 해제 하셔야 합니다. 

2. gramdb-api/pom.xml 에서 maven-war-plugin 설정에서 webappDirectory 에 설정 하거나 war로 설정하려면 warName과 outputDirectory 주석을풀어 설정 합니다.

3. mvn clean install 하여 build 합니다.

4. 각자 원하는 방법으로 서버에 deploy 하면 됩니다.
