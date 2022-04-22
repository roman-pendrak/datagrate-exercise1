package com.datagrate.exercise1.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.springframework.stereotype.Component;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import com.jayway.jsonpath.JsonPath;
import java.io.FileReader;

@Component
public class Route1Processor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			int userId = exchange.getIn().getHeader("userid", Integer.class);
		
			JSONParser parser = new JSONParser();
			Object jsonFileObj = parser.parse(new FileReader("userData/userData.json"));
			
			Object jsonPathObj = (Object) JsonPath.parse(jsonFileObj).read("$.users..[?(@.userid==" + String.valueOf(userId) + ")]");
			Object obj = parser.parse(jsonPathObj.toString());
			JSONArray jsonObj = (JSONArray) obj;
			exchange.getIn().setBody(jsonObj);
			
		} catch (TypeConversionException ex) {
			exchange.getIn().setBody("Invalid userid: " + ex.getMessage());
		} catch (Exception ex) {
			exchange.getIn().setBody("Error: " + ex.getMessage());
		} 
	}
}
