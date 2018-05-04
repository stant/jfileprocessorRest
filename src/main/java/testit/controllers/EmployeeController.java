package testit.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.towianski.boot.server.TomcatApp;
import testit.model.Employee;
import testit.model.ProgramMemoryEmp;

import java.util.*;

/**
 * Handles requests for the Employee service.
 */
@Controller
@EnableAutoConfiguration
//@Controller
public class EmployeeController {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	//Map to store employees, ideally we should use database
//	Map<Integer, Employee> empData = new HashMap<Integer, Employee>();
	Map<Integer, Employee> empData = TomcatApp.myRest.readProgramMemoryFile().empData;

	@RequestMapping(value = EmpRestURIConstants.DUMMY_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getDummyEmployee() {
		logger.info("Start getDummyEmployee");
		Employee emp = new Employee();
		emp.setId(9999);
		emp.setName("Dummy");
		emp.setCompany("noCompany");
		emp.setCreatedDate(new Date());
		empData.put(9999, emp);
		return emp;
	}
	
	@RequestMapping(value = EmpRestURIConstants.GET_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getEmployee(@PathVariable("id") int empId) {
		logger.info("Start getEmployee. ID="+empId);
		
		return empData.get(empId);
	}
	
	@RequestMapping(value = EmpRestURIConstants.GET_ALL_EMP, method = RequestMethod.GET)
	public @ResponseBody List<Employee> getAllEmployees() {
		logger.info("Start getAllEmployees.");
		List<Employee> emps = new ArrayList<Employee>();
		Set<Integer> empIdKeys = empData.keySet();
		for(Integer i : empIdKeys){
			emps.add(empData.get(i));
		}
		return emps;
	}
	
	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
		logger.info("Start createEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}
	
	@RequestMapping(value = EmpRestURIConstants.DELETE_EMP, method = RequestMethod.DELETE)
	public @ResponseBody Employee deleteEmployee(@PathVariable("id") int empId) {
		logger.info("Start deleteEmployee.");
		Employee emp = empData.get(empId);
		empData.remove(empId);
		return emp;
	}

	@RequestMapping(value = "/saveToFile", method = RequestMethod.POST)
	public @ResponseBody
	ProgramMemoryEmp saveToFile() {
		logger.info("saveToFile()");
		TomcatApp.myRest.saveProgramMemoryFile( TomcatApp.getProgramMemory() );
//		return "saveToFile";
		return TomcatApp.getProgramMemory();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run( EmployeeController.class, args );
	}
}
