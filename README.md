# Sample rest API test for jsonplaceholder

## Run Automated Tests
To run the tests use simple maven command:  
```  
mvn clean test  
```  
  
## Generate Test Report  
Allure framework is used to generate report from test results - https://github.com/allure-framework/allure-maven  

To serve test report on local machine make sure you have run tests using `mvn clean test` command before, then use below command:  
```  
mvn allure:serve  
```  

In order to generate static html report from your test result just use following command.  
```  
mvn allure:report  
```
Report will be generated in: target/site/allure-maven/index.html
