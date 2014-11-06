package com.welcohealth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.logging.Logger;

import org.slf4j.*;

import web.servlets.PostDelayServlet;


public class DelayPostJob implements Job {
	
	
	Logger log = Logger.getLogger(this.getClass().getName());
	final static org.slf4j.Logger logger = LoggerFactory.getLogger(DelayPostJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		StringBuilder sb = new StringBuilder();
		JobDataMap jdm = context.getJobDetail().getJobDataMap();
		for (Map.Entry<String, Object> detail: jdm.entrySet()){
			if(!(detail.getKey().equals("endpoint")) && !(detail.getKey().equals("requestpath")) ){
			   sb.append(String.format("&%s=%s",detail.getKey(), String.valueOf(detail.getValue()) ));
			}
		}
		String qs = sb.toString().substring(1);
	
		String postResponse = callPost(URLDecoder.decode(jdm.getString("endpoint")), URLDecoder.decode(jdm.getString("requestpath")), qs);
		log.info("Third Party Post Response: " + postResponse);
		logger.info("Third Party Post Response: " + postResponse);
	}
	
	
	
    private String callPost(String endpoint, String requestpath, String requestParams){

    	String result = "";
		HttpURLConnection conn = null;
		InputStream is = null;
		try{
			 String urlString = String.format("%s%s?%s", endpoint, requestpath, requestParams);
			 log.info("Third Party Post Request: " + urlString);
			 logger.info("Third Party Post Request: " + urlString);
		     URL url = new URL(urlString);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         conn.setDoInput(true);
		     conn.connect();
	         int response = conn.getResponseCode();
	         if (response == 200){
	              is = conn.getInputStream();
	              /*
	              for (Map.Entry<String,List<String>> hf : conn.getHeaderFields().entrySet()){
	            	  log.info("PostResponseHeader " + hf.getKey() + hf.getValue().get(0) );
	              }
	              */
	         }
	         else{
	        	 is = conn.getErrorStream();
	              for (Map.Entry<String,List<String>> hf : conn.getHeaderFields().entrySet()){
	            	  log.info("Error " + hf.getKey() + hf.getValue().get(0) );
	            	  logger.error("Error " + hf.getKey() + hf.getValue().get(0));
	              }	        	 
	         }
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	         StringBuilder sb =  new StringBuilder();
	         String sLine = "";

	     	 while ((sLine = reader.readLine()) != null) {
	     		sb.append(sLine);
	     	 }
	         result = sb.toString();
	         
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally {
			    conn.disconnect();
			   }
			
			return result;
	  
	   } 
	

}
