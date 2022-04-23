package com.datagrate.exercise1.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.datagrate.exercise1.processors.Route1Processor;

@Component
public class Route1 extends RouteBuilder {
	
		@BeanInject
		private Route1Processor processor;
	
		@Override
		public void configure() throws Exception {
			restConfiguration().component("servlet").port(8080).host("localhost").bindingMode(RestBindingMode.json);

			// solution 1 using processor - opens file using FileReader/JSONParsers utils
			rest().get("processor/getUserById/")
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.route()
				.process(processor)
				.endRest();	
			
			// solution 2 using strictly camel DSL - using pollEnrich to load file into message 
			rest().get("camel-dsl/getUserById")
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.route()
				.setProperty("userid", simple("${headers.userid}"))
				.pollEnrich("file:userData/?fileName=userData.json&noop=true&idempotent=false", 5000)
				.setBody().jsonpath("$.users..[?(@.userid==${exchangeProperty.userid})]")
				.endRest();
			
			// solution 3 using strictly camel DSL - using simple language to read file resource and route to extractUserData endpoint
			rest().get("camel-dsl-simple/getUserById")
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.route()
				.setProperty("userid", simple("${headers.userid}"))
				.setBody().simple("resource:file:userData/userData.json")
				.to("direct-vm:extractUserData");
			
			from("direct-vm:extractUserData")
				.setBody()
				.jsonpath("$.users..[?(@.userid==${exchangeProperty.userid})]")
				.endRest();

		}
}
