과제 설명 
======

#개발환경  
* JDK : java version "1.8.0_73"
* REDIS: 4.0.8 (외부 서버로 세팅 되어 있음 : alcava00.synology.me:16379)
* Spring boot: 1.5.10.RELEASE
* Spring Data Redis: 1.5.10.RELEASE
* Spring : 4.3.14.RELEASE
* junit : 4.12
* build tool: maven 

#해결 전략
1. coupon id 생성
    * charset : 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
    * randomno.nextBytes 를 이용하여 16 사이즈의 byte array 를 생성 후 62의 나머지값으로 charset array 에서 문자 선택   
    ~~~
         public String generateUniqueId() {
             byte[] randomByte = new byte[size];
     
             SecureRandom randomno = new SecureRandom();
             randomno.nextBytes(randomByte);
             int resultSize =useIdBlockSeparator? size + (size / idBlockSize) - 1:size; //구분자를 포함한 길이
             char[] result = new char[resultSize];
     
             int position = 0;
             for (int i = 0; i < resultSize; i++) {
                 //idBlockSize 마다 구분자 추가
                 if (useIdBlockSeparator&& i % (idBlockSize + 1) == idBlockSize) {
                     result[i] = div;
                 } else {
                     result[i] = charSet[(randomByte[position] & 0xff) % charSetSize];
                     position++;
                 }
             }
     
             return new String(result);
         }
   ~~~
    
2. coupon 정보 저장
    * spring-data-redis 를 이용하여 coupon 객체 저장 
    * sorting 및 paging을 위해 sortedSet 에 coupon id 저장 하고 redis "SORT" 를 이용 
    
4. 기타 
   * 오류 처리
        * 오류 처리를 위해 @ExceptionHandler 이용  
            - 업무코드에서 발생한 오류,Spring framework 에서 발생하는 오류,기타오류로 분류하여 처리  
        * 오류 response 객체를 정의하여 오류 포맷 통일  
   * 국제화 
   * validation 
   * 로깅 
   
#실행 방법 
1. redis 기동 (외부 서버 미 사용시 )
2. application 실행
    * eclipse or intellij 사용시 : com.example.coupon.CouponApplication 실행
    * 콘솔에서 실행
        * cd ${project_root}
        * ./mvnw spring-boot:run 실행 


