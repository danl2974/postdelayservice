package web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.welcohealth.DelayPostJob;


public class PostEditServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	Logger log = Logger.getLogger(this.getClass().getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
    {
		
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Map<String,String[]> pMap = request.getParameterMap(); 
       try{
    	   String email = pMap.get("email")[0];

           SchedulerFactory sf = new StdSchedulerFactory();
	       Scheduler scheduler = sf.getScheduler();
	       
	       JobDetail jd = scheduler.getJobDetail(new JobKey(email, "postdelayservice"));
	       JobDataMap incumbentJobMap = jd.getJobDataMap();
	      
	       JobDetail newjob = JobBuilder.newJob(DelayPostJob.class)
				    .withIdentity(email, "postdelayservice")
				    .build();
	       
	       JobDataMap newjmd = newjob.getJobDataMap();
	       for (Map.Entry<String,Object> entry : incumbentJobMap.entrySet()){
	    	   newjmd.put(entry.getKey(), entry.getValue());
	    	   log.info(String.format("%s=%s", entry.getKey(), entry.getValue()));
	       }
	       for (Map.Entry<String,String[]> pentry : pMap.entrySet()){
	    	   newjmd.put(pentry.getKey(), pentry.getValue()[0]);
	    	   log.info(String.format("%s=%s", pentry.getKey(), pentry.getValue()[0]));
	       }
	       
	       if (scheduler.checkExists(new JobKey(email, "postdelayservice"))){
	          scheduler.addJob(newjob, true, true);
	          scheduler.triggerJob(new JobKey(email, "postdelayservice"));
		      scheduler.deleteJob(new JobKey(email, "postdelayservice"));
	          out.println(String.format("Scheduled job for %s edited and triggered", email ));
	       }
	       else{
	    	   out.println(String.format("No job scheduled for %s to edit",  email)); 
	       }
	       
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