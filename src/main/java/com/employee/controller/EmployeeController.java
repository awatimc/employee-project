package com.employee.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.employee.DTO.EmployeeDTO;
import com.employee.service.EmployeeService;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import jakarta.servlet.http.HttpSession;

//Created by mallikarjun.awati on 01/03/2024.

@RestController
public class EmployeeController {
	
	@Autowired
	EmployeeService empService;
	
	//Create new employee date formate should be yyyy-mm-dd
	@PostMapping("/employees")
	public Map<Object, Object> saveEmployeeRecord(@RequestBody @DateTimeFormat(pattern = "yyyy-MM-dd")EmployeeDTO employee,HttpSession session){
		Map<Object, Object> retMap = new HashMap<Object, Object>();
		try {
			if(!employee.getFirstName().isEmpty() && !employee.getLastName().isEmpty() && (employee.getSalary()!=null)) {
				Integer employeeId = empService.saveEmployeeRecord(employee,session);
			    retMap.put("id", employeeId);
				return retMap;
			}else {
				retMap.put("code", "400");
				retMap.put("message", "First Name, Last Name and Salary required");
				return retMap;
			}
		}catch(Exception e) {
			retMap.put("code", "500");
			retMap.put("message", "An error has occurred. Please contact the admin for assistance"+e);
			return retMap;
		}
	}
	
	@GetMapping("/employees/{id}")
	public Map<String,Object> emp(@PathVariable("id") Integer id, HttpSession session) throws StreamWriteException, DatabindException, IOException{
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			if(id!=null) {
				EmployeeDTO employee  = empService.getEmployeeById(id,session);
				if(employee!=null) {
					retMap.put("empooyee",employee);
				}else{
					retMap.put("code", "404");
					retMap.put("message", "Invalid employee id");
				}
				
			}else {
				retMap.put("code", "400");
				retMap.put("message", "Employee id required");
			}	 
		}catch(Exception e) {
			retMap.put("code", "500");
			retMap.put("message", "An error has occurred. Please contact the admin for assistance"+e);
			return retMap;
		}
	return retMap;	
	}
	
	//Search filter for employee record
	@GetMapping("/employees")
	public Map<String,Object> searchEmployee(String name, Double fromSalary,Double toSalary, HttpSession session) throws StreamWriteException, DatabindException, IOException{
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			retMap  = empService.searchEmployee(name,fromSalary,toSalary,session);
		}
		catch(Exception e) {
			retMap.put("code", "500");
			retMap.put("message", "An error has occurred. Please contact the admin for assistance"+e);
			return retMap;
		}
		return retMap;
	}
}
