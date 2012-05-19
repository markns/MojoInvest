package com.mns.mojoinvest.test;

import com.google.inject.Singleton;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class SimpleBoatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String sailNumber = req.getParameter("sailNumber");
        // Validate and fetch the SailBoat POJO from data store
        // ...
        SailBoat boatPojo = new SailBoat("Blue Moon", 12);

        // Push the model into the request attribute under key "model".
        req.setAttribute("model", boatPojo);

        // Forward across to Silken - the path denotes the template to render.
        RequestDispatcher rd = getServletContext()
                .getRequestDispatcher("/soy/app.params");
        rd.forward(req, resp);
    }


}