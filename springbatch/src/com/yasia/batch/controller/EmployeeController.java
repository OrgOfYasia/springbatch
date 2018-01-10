package com.yasia.batch.controller;

@Controller
@RequestMapper("/employee")
public class EmployeeController {
	
	@ResponseBody
	@RequestMapping(value="/list", method={RequestMethod.GET, RequestMethod.POST})
	public List<Employee> listAll(){
		
		return null;
	}

	@RequestGet("/toMgt")
	public ModelAndView toMgt(){
		
		return new ModelAndView("employeeMgt");
	}
}
