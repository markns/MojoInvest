package com.mns.mojoinvest.server.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class SuccessfulUploadServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String blobKey = req.getParameter("blob-key");

		resp.setContentType("text/html");
		resp.getWriter().println("Successfully uploaded. Download file: <br/>");
		resp.getWriter().println(
				"<a href='/serve?blob-key=" + blobKey
						+ "'>Click to download</a>");
	}

}