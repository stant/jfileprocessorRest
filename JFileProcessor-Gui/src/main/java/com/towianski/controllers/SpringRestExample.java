package com.towianski.controllers;

import com.towianski.models.JfpRestURIConstants;
import com.towianski.models.FilesTblModel;
import org.springframework.web.client.RestTemplate;

public class SpringRestExample {

//    public static final String SERVER_URI = "http://localhost:8080/rest/jfp/";
    public static final String SERVER_URI = "http://192.168.56.103:8080/rest/jfp/";

    public static void main(String args[]){

//        testGetDummyEmployee();
//        System.out.println("*****");
//        testCreateEmployee();
//        System.out.println("*****");
//        testGetEmployee();
//        System.out.println("*****");
//        searchFiles();
    }

    
     /**
  * Retrieves all records from the REST provider
  * and displays the records in a JSP page
  */
// @RequestMapping(value = "/getall", method = RequestMethod.GET)
// public String getAll(Model model) {
//  logger.debug("Retrieve all persons");
//   
//  // Prepare acceptable media type
//  List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
//  acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
//   
//  // Prepare header
//  HttpHeaders headers = new HttpHeaders();
//  headers.setAccept(acceptableMediaTypes);
//  HttpEntity<Person> entity = new HttpEntity<Person>(headers);
// 
//  // Send the request as GET
//  try {
//   ResponseEntity<PersonList> result = restTemplate.exchange("http://localhost:8080/spring-rest-provider/krams/persons", HttpMethod.GET, entity, PersonList.class);
//   // Add to model
//   model.addAttribute("persons", result.getBody().getData());
//    
//  } catch (Exception e) {
//   logger.error(e);
//  }
//   
//  // This will resolve to /WEB-INF/jsp/personspage.jsp
//  return "personspage";
// }
 
    private static void searchFiles( FilesTblModel filesTblModel ) {
        RestTemplate restTemplate = new RestTemplate();
        //we can't get List<Employee> because JSON convertor doesn't know the type of
        //object in the list and hence convert it to default JSON object type LinkedHashMap
//        FilesTblModel filesTblModel = restTemplate.getForObject( SERVER_URI+JfpRestURIConstants.GET_FILES, FilesTblModel.class, SearchModel.class );
        
        String response = restTemplate.postForEntity( SERVER_URI+JfpRestURIConstants.SEARCH, filesTblModel, String.class).getBody();
        System.out.println( "response =" + response + "=" );
    }
    
        //make the object

////set your headers
//HttpHeaders headers = new HttpHeaders();
//headers.setContentType(MediaType.APPLICATION_JSON);
//
////set your entity to send
//HttpEntity entity = new HttpEntity(obj,headers);
//
//// send it!
//ResponseEntity<String> out = restTemplate.exchange("url", HttpMethod.POST, entity
//    , String.class);
//    }

//    private static void searchFiles() {
//        RestTemplate restTemplate = new RestTemplate();
//        //we can't get List<Employee> because JSON convertor doesn't know the type of
//        //object in the list and hence convert it to default JSON object type LinkedHashMap
//        List<LinkedHashMap> emps = restTemplate.getForObject(SERVER_URI+JfpRestURIConstants.GET_FILES, List.class);
//        System.out.println(emps.size());
//        for(LinkedHashMap map : emps){
//            System.out.println("ID="+map.get("id")+",Name="+map.get("name")+",CreatedDate="+map.get("createdDate"));;
//        }
//    }

//    private static void testCreateEmployee() {
//        RestTemplate restTemplate = new RestTemplate();
//        Employee emp = new Employee();
//        emp.setId(1);emp.setName("Pankaj Kumar");
//        Employee response = restTemplate.postForObject(SERVER_URI+EmpRestURIConstants.CREATE_EMP, emp, Employee.class);
//        printEmpData(response);
//    }

//    private static void testGetEmployee() {
//        RestTemplate restTemplate = new RestTemplate();
//        Employee emp = restTemplate.getForObject(SERVER_URI+"/rest/emp/1", Employee.class);
//        printEmpData(emp);
//    }

//    private static void testGetDummyEmployee() {
//        RestTemplate restTemplate = new RestTemplate();
//        Employee emp = restTemplate.getForObject(SERVER_URI+EmpRestURIConstants.DUMMY_EMP, Employee.class);
//        printEmpData(emp);
//    }

//    public static void printEmpData(Employee emp){
//        System.out.println("ID="+emp.getId()+",Name="+emp.getName()+",CreatedDate="+emp.getCreatedDate());
//    }
}
