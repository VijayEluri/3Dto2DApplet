/*
#
# ServerConnection.java
#
# copyright (c) 2009-2010, Danny Arends
# last modified Dec, 2010
# first written Dec, 2010
#
#     This program is free software; you can redistribute it and/or
#     modify it under the terms of the GNU General Public License,
#     version 3, as published by the Free Software Foundation.
# 
#     This program is distributed in the hope that it will be useful,
#     but without any warranty; without even the implied warranty of
#     merchantability or fitness for a particular purpose.  See the GNU
#     General Public License, version 3, for more details.
# 
#     A copy of the GNU General Public License, version 3, is available
#     at http://www.r-project.org/Licenses/GPL-3
#
*/

package nl.dannyarends.eventHandling;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.dannyarends.generic.Utils;
import nl.dannyarends.rendering.Engine;



/// ServerConnection
//<p>
//Handles server communication events
//</p>
//
public class ServerConnection {
	String aLine;
	URL url; 
	HttpURLConnection con; 
	OutputStream oStream;
	byte[] parameterAsBytes;
	BufferedReader in;
	public static long up;
	public static long down;
	public static boolean online = false;
	String connectionstring = "http://localhost/cgi-bin/server.cgi";
	
	public ServerConnection(String type){
		up=0;
		down=0;
		if(type=="Applet"){
			try{
			connectionstring = Engine.getAppletURL() + "cgi-bin/server.cgi";
			}catch(Exception e){
			connectionstring = "http://localhost/cgi-bin/server.cgi";	
			}
		}
	}
	
	/**
	 * Send a command to the Perl server REST Api and parse the response
	 * @param parametersAsString Parameter string formatted as REST to send to server
	 * 
	 */	
	public String commandToServer(String parametersAsString){
		String response = "\n";
		try {
			if(parametersAsString==null) return "";
			parameterAsBytes = parametersAsString.getBytes(); 
			//send parameters to server 
			//FOR DEPLOY: 
			//Utils.console(Engine.getAppletURL() + "cgi-bin/server.cgi");
			//url = new URL(Engine.getAppletURL() + "cgi-bin/server.cgi");
			url = new URL(connectionstring); 
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true); 
			con.setDoInput(true); 
			con.setRequestMethod("POST");
			con.setRequestProperty("Content=length", String.valueOf(parameterAsBytes.length)); 
			oStream = con.getOutputStream(); 
			oStream.write(parameterAsBytes); 
			oStream.flush(); 
			//Read response from server 
			in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			aLine = in.readLine(); 
			while (aLine != null){ 
				response += aLine + "\n"; 
				aLine = in.readLine();
			} 
			if(Engine.verbose) Utils.console(response);
			in.close(); 
			oStream.close(); 
		} catch (Exception e) {
			Utils.log(e.getMessage(),System.err);
		}
		if(response.length() > 1) online = true;
		up += parameterAsBytes.length;
		down += response.length();
		Utils.console("Server traffic: " + up + "/" + down);
		return response;
	}

	public boolean getOnline() {
		return online;
	}
}