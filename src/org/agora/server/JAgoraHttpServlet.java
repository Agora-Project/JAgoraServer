
package org.agora.server;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author angle
 */


public class JAgoraHttpServlet extends HttpServlet {
    
    public void init() throws ServletException {
        
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException
    {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        File file = new File(getServletContext().getRealPath("/index.htm"));
        FileUtils.copyFile(file, response.getOutputStream());
    }
         
}
