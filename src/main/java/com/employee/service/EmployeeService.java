package com.employee.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.employee.DTO.EmployeeDTO;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

//Created by mallikarjun.awati on 01/03/2024.

@Service
public class EmployeeService {
	
	//Common method for get all employe record
	public EmployeeDTO[] getAllEmployeeData(HttpSession session) throws StreamReadException, DatabindException, IOException {
		String path =session.getServletContext().getRealPath("/")+"File"+File.separator+"employee" +".json";
		File file = new File(path);
		//if .JSON file not exist create first than go to get record 
		if(!file.exists()) {
			 file.createNewFile();
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		//reading record and mapping in to DTO class
		EmployeeDTO[] getAllEmployeeRecord = objectMapper.readValue(new File(path), EmployeeDTO[].class);
		return getAllEmployeeRecord;
	}
	
	public int saveEmployeeRecord(EmployeeDTO employee,HttpSession session) throws StreamWriteException, DatabindException, IOException{
		ObjectMapper objectMapper = new ObjectMapper(); 
		EmployeeDTO[] getAllEmployeeRecord = getAllEmployeeData(session);
		String path =session.getServletContext().getRealPath("/")+"File"+File.separator+"employee" +".json";
	    List<EmployeeDTO> ListOfEmployee = new ArrayList<EmployeeDTO>();
	    //First i am fetching existing record than i am appending to new record than i am saving
	    //If i not fetch existing record this method will override existing record
	    if(getAllEmployeeRecord.length>0) {
		    for (EmployeeDTO allRecord : getAllEmployeeRecord) {  	
		    	ListOfEmployee.add(allRecord);
	          }
	    }
	    // Checking last employee id and adding by one for next id
	    OptionalInt  lastEmployeeId = Arrays.stream(getAllEmployeeRecord)
                .mapToInt(EmployeeDTO::getId)
                .max();
	    employee.setId(lastEmployeeId.orElse(-1)+1);
	    ListOfEmployee.add(employee);
	    objectMapper.writeValue(new File(path), ListOfEmployee);
	    return lastEmployeeId.orElse(-1)+1;
	}
	
	public EmployeeDTO getEmployeeById(int id, HttpSession session) throws StreamReadException, DatabindException, IOException {
		EmployeeDTO[] employee = getAllEmployeeData(session);	    
	    EmployeeDTO targetData = null;
	    for (EmployeeDTO data : employee) {
            if (data.getId() == id) {
                targetData = data;
                break;
            }
        }
		return targetData;
	}
	
	public Map<String, Object> searchEmployee(String name,Double fromSalary,Double toSalary,HttpSession session) throws StreamReadException, DatabindException, IOException {
		Map<String, Object> retMap = new HashMap<String, Object>();
		EmployeeDTO[] employee = getAllEmployeeData(session);	
		List<EmployeeDTO> targetData =null;
	    // Checking all the condition as per user given paramater
	    if(name!=null && name.length()>0 && !name.isEmpty()) {
	    	 targetData = Arrays.stream(employee)
                .filter(data -> data.getFirstName().equals(name) || data.getLastName().equals(name) )
                .collect(Collectors.toList());
	    }else if(fromSalary !=null && toSalary !=null) {
	    	 targetData = Arrays.stream(employee)
	                 .filter(data -> data.getSalary()>=fromSalary &&  data.getSalary()<=toSalary )
	                 .collect(Collectors.toList());
	    }else if(fromSalary !=null) {
	    	 targetData = Arrays.stream(employee)
	                 .filter(data -> data.getSalary()>=fromSalary)
	                 .collect(Collectors.toList());
	    }else if(toSalary !=null) {
	    	targetData = Arrays.stream(employee)
	                 .filter(data ->  data.getSalary()<=toSalary)
	                 .collect(Collectors.toList());
	    }
	    
	    retMap.put("responce", targetData);
	    return retMap;
	}
	

}
