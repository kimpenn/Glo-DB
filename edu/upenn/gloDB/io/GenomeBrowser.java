/*
 * Copyright 2007, 2012 Stephen Fisher and Junhyong Kim, University of
 * Pennsylvania.
 *
 * This file is part of Glo-DB.
 * 
 * Glo-DB is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Glo-DB is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Glo-DB. If not, see <http://www.gnu.org/licenses/>.
 *
 * @(#)GenomeBrowser.java
 */

package edu.upenn.gloDB.io;

import edu.upenn.gloDB.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;


/**
 * Import/Export Track data from/to Genome Browser. <br><br>
 *
 * <A HREF="http://www.genome.ucsc.edu/">http://www.genome.ucsc.edu/</A>
 *
 *
 * @author  Stephen Fisher
 * @version $Id: GenomeBrowser.java,v 1.1.2.11 2007/03/01 21:17:33 fisher Exp $
 */

public class GenomeBrowser {
	 
	 private final static String SURL = "http://www.genome.ucsc.edu/";
	 // private final static String SURL = "http://192.168.189.10/";

	 private static Random random = new Random();

    //--------------------------------------------------------------------------
    // Miscellaneous Methods

	 private static String randomString() { return Long.toString(random.nextLong(), 36); }

	 /**
	  * Posts 'track' to Genome Browser.  The track being viewed must
	  * have a valid source or the Genome Browser won't be properly
	  * oriented.
	  */
	 public static String viewTrack(String id) {
		  Track track = ObjectHandles.getTrack(id);
		  if (track == null) {
				GloDBUtils.printError("Not a valid track");
				return "";
		  }

		  if (track.numSources() == 0) {
				GloDBUtils.printError("No valid source");
				return "";
		  }
				
		  // use first source
		  String header = "browser position ";
		  header += track.getSourceSet().toArray()[0] + ":";
		  header += track.getMin() + "-" + track.getMax() + "\n";

		  GFFTrack gff = new GFFTrack();
		  String data = header + gff.format(id);

		  return post(data);
	 }

	 /*
	 public static void viewGenomeBrowser(String data) {
		  // da += fb + "\"hgt.customFile\"; filename=\"gbtest.bed\"\r\nContent-Type: text/plain\r\n\r\n"
		  // da += "browser position chr22:1000-10000\r\nbrowser hide all\r\n"
		  // da += "track name=pairedReads description=\"Clone Paired Reads\" visibility=2 color=0,128,0 useScore=1\r\n"
		  // da += "chr22 1000 5000 cloneA 960 + 1000 5000 0 2 567,488, 0,3512\r\n"
		  // da += "chr22 2000 6000 cloneB 200 - 2000 6000 0 2 433,399, 0,3601\n\r\n"

		  // data = "browser position chr22:1000-10000"
		  String html = postToGenomeBrowser(data);
		  
		  // view response
		  new ViewHTML(html, STRING);
	 }

browser position chr1:6554929-6594816
chr1	DoTS2Gene	exon	6554929	6555181	200	+	.	DG.1
chr1	DoTS2Gene	exon	6576588	6576694	200	+	.	DG.2
chr1	DoTS2Gene	exon	6589784	6589856	200	+	.	DG.3
chr1	DoTS2Gene	exon	6594695	6594816	200	+	.	DG.4
	 */

	 public static String post(String data) {
		  String html = "";
		  try {
				// open connection
				URL url = new URL(SURL + "cgi-bin/hgTracks");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);

				String boundary = "---------------------------" + randomString();

				// tell server this is a form
				connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);

				// the boundary is actually 2 "-" larger than that in the header above
				boundary = "--" + boundary;

				// open output stream, write data, then close stream
				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
				out.writeBytes(boundary + "\r\n");
				out.writeBytes("Content-Disposition: form-data; name=\"hgt.customText\"\r\n\r\n");
				out.writeBytes(data + "\r\n");
				out.writeBytes(boundary + "--\r\n");
				out.flush();
				out.close();


				String tmp = "";
				tmp += boundary + "\r\n";
				tmp += "Content-Disposition: form-data; name=\"hgt.customText\"\r\n\r\n";
				tmp += data + "\r\n";
				tmp += boundary + "--\r\n";
				System.out.println(SURL + "cgi-bin/hgTracks");
				System.out.println(tmp);
				/*
				*/

				// get response
				//				new edu.upenn.gloDB.gui.ViewHTML(connection.getInputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					 html += inputLine + "\n";
				}

				// the html viewer assumes the file is local and thus
				// doesn't properly display images which are on the web
				// server, so we're hardcoding their locations here
				String replacement = "= \"" + SURL;
				String regExp1 = "=\\s*\"/";
				String regExp2 = "=\\s*\"../";
				html = html.replaceAll(regExp1, replacement);
				html = html.replaceAll(regExp2, replacement);

				// close connection
				in.close();
		  } catch (Exception e) {
				;
		  }

		  return html;
	 }

} // GenomeBrowser.java

	 
