package web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.json.JSONObject;
import org.slf4j.*;

import com.welcohealth.DelayPostJob;


public class PostDelayServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	Logger log = Logger.getLogger(this.getClass().getName());
	final static org.slf4j.Logger logger = LoggerFactory.getLogger(PostDelayServlet.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
    {
		
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        HashMap<String,String> hmresp = new HashMap<String,String>();
        
        Map<String,String[]> pMap = request.getParameterMap();
        
        try{
    	   String email = pMap.get("email")[0];
    	   int minutes = Integer.parseInt(pMap.get("delay")[0]);
    	   long startTime = System.currentTimeMillis() + (minutes * 60000L);
    	   //String jobId = String.format("%s", email, );
    	   
           SchedulerFactory sf = new StdSchedulerFactory();
	       Scheduler scheduler = sf.getScheduler();
	       scheduler.start();
	       
	       JobDetail job = JobBuilder.newJob(DelayPostJob.class)
				    .withIdentity(email, "postdelayservice")
				    .build();
	       
	       JobDataMap jmd = job.getJobDataMap();
	       for (Map.Entry<String,String[]> entry : pMap.entrySet()){
	    	   jmd.put(entry.getKey(), entry.getValue()[0]);
	       }
	       
	       Trigger trigger = TriggerBuilder.newTrigger()
	    		   .startAt(new Date(startTime))
	    		   .withIdentity(email, "postdelayservice")
	    		   .build();

	       scheduler.scheduleJob(job, trigger);
	       
	       for (Map.Entry<String,String[]> entry : pMap.entrySet()){
	        	
	    	   hmresp.put(entry.getKey(),  entry.getValue()[0]);
	    	   jsonResponse.put("result",  "ok");
	    	   jsonResponse.put("request",  hmresp);
	        	
	       }
	       out.println("Scheduled delay post with: " + jsonResponse.toString());
	       log.info("Scheduled Delay Post: " + jsonResponse.toString());
	       logger.info("Scheduled Delay Post: " + jsonResponse.toString());
       }
	   catch(Exception e){
				 out.println(e.getMessage());
	   }
		
    }
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
	{
	    doGet(request, response);
	}

}