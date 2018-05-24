// Will Dahl
// 001273655
// ICSI 403
// May 3rd, 2018

package csi403;

// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;
import java.util.*;


// Extend HttpServlet class
public class FOAF extends HttpServlet {

  // Standard servlet method 
  public void init() throws ServletException
  {
      // Do any required initialization here - likely none
  }

  // Standard servlet method - we will handle a POST operation
  public void doPost(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      doService(request, response); 
  }

  // Standard servlet method - we will not respond to GET
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type and return an error message
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      out.println("{ 'message' : 'Use POST!'}");
  }

  private void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    // Set response content type to be JSON
    response.setContentType("application/json");
    // Send back the response JSON message
    PrintWriter out = response.getWriter();
    try{
  	// Get received JSON data from HTTP request
      BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
      String jsonStr = "";
      if(br != null){
          jsonStr = br.readLine();
      }
      // Create JsonReader object
      StringReader strReader = new StringReader(jsonStr);
      JsonReader reader = Json.createReader(strReader);
      
      // Get the singular JSON object (name:value pair) in this message.    
      JsonObject obj = reader.readObject();
      // From the object get the array named "inList"
      JsonArray inArray = obj.getJsonArray("inList");
      String[][] array = new String[inArray.size()][2];
      LinkedList<String[]> outList = new LinkedList<String[]>();
      for(int i = 0; i < inArray.size(); i++){
        obj = inArray.getJsonObject(i);
        JsonArray currArray = obj.getJsonArray("friends");
        array[i][0] = currArray.getString(0);
        array[i][1] = currArray.getString(1);
      }

      //Creates the Json Array Builders needed for output
      for(int i = 0; i < array.length-1; i++){
        String root = array[i][0];
        String friend = array[i][1];
        for(int j = (i+1); j < array.length; j++){
          boolean added = false;
          String[] innerArray = new String[2];
          if(array[j][0].equals(friend)){
            if(!contains(root, array[j][1], outList)){
              innerArray[0] = root;
              innerArray[1] = array[j][1];
              added = true;
            }
          }

          else if(array[j][1].equals(friend)){
            if(!contains(root, array[j][0], outList)){
              innerArray[0] = root;
              innerArray[1] = array[j][0];
              added = true;
            }
          }

          else if(array[j][0].equals(root)){
            if(!contains(friend, array[j][1], outList)){
              innerArray[0] = friend;
              innerArray[1] = array[j][1];
              added = true;
            }
          }

          else if(array[j][1].equals(root)){
            if(!contains(friend, array[j][0], outList)){
              innerArray[0] = friend;
              innerArray[1] = array[j][0];
              added = true;
            }
          }

          if(added){
            outList.add(innerArray);
          }
        }
      }

      //prints to output
      out.print("{ \"outList\" : [");
      for(int i = 0; i < outList.size(); i++){
        if(i != 0){
          out.print(",[\"" + outList.get(i)[0] + "\",\"" + outList.get(i)[1] + "\"]");
        }

        else{
          out.print("[\"" + outList.get(i)[0] + "\",\"" + outList.get(i)[1] + "\"]");
        }
        
      } 

      out.print("]}");
      out.println();
    }
    //catches any Exceptinos
    catch(Exception e){
      out.println("{ \"message\":\"Malformed JSON\" }");
    }
  }

  // Standard Servlet method
  public void destroy()
  {
      // Do any required tear-down here, likely nothing.
  }

  public boolean contains(String str1, String str2, LinkedList<String[]> list){
    for(int i = 0; i < list.size(); i++){
      String[] array = list.get(i);
      if(str1.equals(array[1]) && str2.equals(array[0])){
        return true;
      }

      if(str1.equals(array[0]) && str2.equals(array[1])){
        return true;
      }
    }

    return false;
  }
}