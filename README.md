간단 설치 및 테스트 해보기.

1. gramdb-api/src/main/resources/properties/jdbc.properties 에서 설치 된 mariadb & mongodb 정보를 수정 합니다.

2. gramdb-api/pom.xml 에서 maven-war-plugin 설정에서 webappDirectory 에 설정 하거나 war로 설정하려면 warName과 outputDirectory 주석을풀어 설정 합니다.

3. mvn clean install 하여 build 합니다.

4. 각자 원하는 방법으로 서버에 deploy 하면 됩니다.
