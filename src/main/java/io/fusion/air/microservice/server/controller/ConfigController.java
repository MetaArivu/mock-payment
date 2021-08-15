/**
 * (C) Copyright 2021 Araf Karsh Hamid 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.server.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fusion.air.microservice.ServiceBootStrap;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;
import io.fusion.air.microservice.server.models.EchoData;
import io.fusion.air.microservice.server.models.EchoResponseData;
import io.fusion.air.microservice.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Health Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@Configuration
@RestController
@RequestMapping("/api/v1/payments/config")
@RequestScope
@Tag(name = "System", description = "System (Health, Readiness, ReStart.. etc)")
public class ConfigController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	
	private final String title = "<h1>Welcome to Health Service<h1/>"
					+ ServiceHelp.NL
					+"<h3>Copyright (c) MetaArivu Pvt Ltd, 2021</h3>"
					+ ServiceHelp.NL
					;


	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	/**
	 * Returns the Service Name
	 * @return
	 */
	private String name() {
		if(serviceName == null) {
			if(serviceConfig == null) {
				log.info("|Error Autowiring Service config!!!");
				serviceName = "|NoServiceName";
			} else {
				serviceName = "|" + serviceConfig.getServiceName() + "Service";
				log.info("|Version="+ServiceHelp.VERSION);
			}
		}
		return serviceName;
	}
	
	@Operation(summary = "Show the Environment Settings ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Show the environment Settings",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Service Env is not ready.",
					content = @Content)
	})
	@GetMapping("/env")
	@ResponseBody
	public ResponseEntity<Map> getEnv(
			HttpServletRequest request) throws Exception {
		log.info(name()+"|Request to Get Environment Vars Check.. ");
		HashMap<String, String> sysProps = serviceConfig.systemProperties();
		return ResponseEntity.ok(sysProps);
	}

	@Operation(summary = "Show the ConfigMap Settings ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Show the ConfigMap Settings",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Service ConfigMap is not ready.",
					content = @Content)
	})
	@GetMapping("/map")
	@ResponseBody
	public ResponseEntity<String> getConfigMap(
			HttpServletRequest request) throws Exception {
		//  log.info("Pass 1");
		ObjectMapper om = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.findAndRegisterModules();
		//  log.info("Pass 2");
		 String json = serviceConfig.toJSONString();
		//  log.info("Pass 3");
		log.info(name()+"|Request to Get ServiceConfiguration .1. "+json);
		// String json2 = Utils.toJsonString(serviceConfig);
		// log.info(name()+"|Request to Get ServiceConfiguration .2. "+json2);
		// log.info("Pass 4");
		// EchoResponseData erd = new EchoResponseData();
		// return ResponseEntity.ok(Utils.toJsonString(erd));
		return ResponseEntity.ok(serviceConfig.toJSONString());
	}

	/**
	 * Check the Current Log Levels
	 * @return
	 */
    @Operation(summary = "Service Log Levels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Service Log Level Check",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Service is not ready.",
            content = @Content)
    })
	@GetMapping("/log")
    public String log() {
		log.info("|Request to Log Level.. ");
    	log.trace("HealthService|This is TRACE level message");
        log.debug("HealthService|This is a DEBUG level message");
        log.info("HealthService|This is an INFO level message");
        log.warn("HealthService|This is a WARN level message");
        log.error("HealthService|This is an ERROR level message");
        return "HealthService|See the log for details";
    }
	
	/**
	 * Print the Request
	 * 
	 * @param request
	 * @return
	 */
	private String printRequestURI(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		String[] req = request.getRequestURI().split("/");
		sb.append("Params Size = "+req.length+" : ");
		for(int x=0; x < req.length; x++) {
			sb.append(req[x]).append("|");
		}
 		sb.append("\n");
		log.info(sb.toString());
		return sb.toString();
	}
 }

