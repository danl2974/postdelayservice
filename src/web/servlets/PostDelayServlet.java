package web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostDelayServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
    {
		
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        Map<String,String[]> pMap = request.getParameterMap();
        for (Map.Entry<String,String[]> entry : pMap.entrySet()){
        	
        	out.println(String.format("%s = %s \r\n", entry.getKey(), entry.getValue()[0] ));
        	 
        }
        
		
		
    }
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
	{
	    doGet(request, response);
	}

}