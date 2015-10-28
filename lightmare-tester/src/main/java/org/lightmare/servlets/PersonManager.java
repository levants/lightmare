package org.lightmare.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightmare.backing.PersonBacking;
import org.lightmare.entities.Person;
import org.lightmare.utils.PersonUtils;

/**
 * Servlet implementation class PersonManager
 */
@WebServlet(description = "Test servlet", urlPatterns = { "/PersonManager" })
public class PersonManager extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PersonManager() {
	super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	BufferedReader stream = request.getReader();
	Enumeration<String> names = request.getParameterNames();
	while (names.hasMoreElements()) {
	    System.out.println(names.nextElement());
	}
	try {
	    String line;
	    while ((line = stream.readLine()) != null) {
		System.out.println(line);
	    }
	} finally {
	    stream.close();
	}
	Person person = PersonUtils.createPersonToAdd();
	PersonBacking backing = new PersonBacking();
	backing.addPerson(person);

	String persons = backing.getPersons();

	PrintWriter writer = response.getWriter();
	try {
	    writer.write(persons);
	} finally {
	    writer.close();
	}

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
    }

}
