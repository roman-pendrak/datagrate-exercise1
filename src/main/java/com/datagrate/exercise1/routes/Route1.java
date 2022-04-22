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

			rest().get("/getUserById").produces(MediaType.APPLICATION_JSON_VALUE).route().process(processor).endRest();
			
		}
}
