package com.shenkar.controller;

import com.shenkar.model.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProgramController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher = null;	

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
			
		String path = request.getPathInfo();

		System.out.println("Path before change - "+path);
		if (path.contains("ChangingTasks"))path = "/ChangingTasks";
		else if (path.contains("AddingTasks"))path = "/AddingTasks";
		else if (path.contains("DeleteTasks"))path = "/DeleteTasks";
		else if (path.contains("Register"))path = "/Register";
		else if (path.contains("LoginForm"))path = "/LoginForm";
		else if (path.contains("UserTask"))path = "/UserTask";
		else if (path.contains("clientList"))path = "/clientList";
		else if (path.contains("DeleteAccount"))path = "/DeleteAccount";
		System.out.println("Path after change - "+path);

		HttpSession session = request.getSession();

		try{
			switch (path) 
			{
			default:case "/LoginForm":
					dispatcher = getServletContext().getRequestDispatcher("/LoginForm.jsp");
					dispatcher.forward(request, response);
					break;
			case "/Register":
					String firstName = (String)request.getParameter("FirstName");
					String lastName = (String)request.getParameter("LastName");
					String password = (String)request.getParameter("Password");
					String Email = (String)request.getParameter("Email");
					int phoneNumer = Integer.parseInt(request.getParameter("PhoneNumber"));
					int id = Integer.parseInt(request.getParameter("UserID"));
					session.setAttribute("thisUser", id);
					User userReg = new User(firstName, lastName, id, phoneNumer, Email, password);
					HibernateToDoListDAO.Instance().addUser(userReg);
					
					request.setAttribute("MyUser",userReg);
					request.setAttribute("TasksLists",new ArrayList<Task>()); //HibernateToDoListDAO.Instance().getTasksForUser(userReg.getId()));
			case "/UserTask":			
					dispatcher = getServletContext().getRequestDispatcher("/UserTask.jsp");
					String Pawword = request.getParameter("Password");				
					int userid = Integer.parseInt(request.getParameter("UserID"));
					session = request.getSession();
					session.setAttribute("thisUser", userid);
					if (HibernateToDoListDAO.Instance().CheckUserInDB(userid,Pawword)==false)
					{
						dispatcher = getServletContext().getRequestDispatcher("/LoginForm.jsp");				
						request.setAttribute("RequestDeleteAnswer","User ID or Password are wrong");
						dispatcher.forward(request, response);
						break;
					}
					com.shenkar.model.User user = HibernateToDoListDAO.Instance().getUser(userid,Pawword);
					request.setAttribute("MyUser",user);
					request.setAttribute("TasksLists", HibernateToDoListDAO.Instance().getTasksForUser(user.getId()));
					dispatcher.forward(request, response);
					break;				
			case "/DeleteAccount":
					dispatcher = getServletContext().getRequestDispatcher("/LoginForm.jsp");
					String Pawword1 = request.getParameter("Password");				
					int userid1 = Integer.parseInt(request.getParameter("UserID"));
					session = request.getSession();
					session.setAttribute("thisUser", userid1);
					String answer = HibernateToDoListDAO.Instance().deleteUser(userid1, Pawword1);
					request.setAttribute("RequestDeleteAnswer",answer);
					dispatcher.forward(request, response);
					break;
			case "/AddingTasks":
					dispatcher = getServletContext().getRequestDispatcher("/UserTask.jsp");
					String taskName1 = (String)request.getParameter("taskname");
					String taskDescription1 = (String)request.getParameter("taskdescription");
					session = request.getSession();
					int thisUser  = (int) session.getAttribute("thisUser");
					Task newTask = new Task(thisUser, taskName1, taskDescription1);
					request.setAttribute("TasksLists", HibernateToDoListDAO.Instance().getTasksForUser(thisUser));
					HibernateToDoListDAO.Instance().addTask(newTask);
					dispatcher.forward(request, response);
					break;	
			case "/clientList":
					dispatcher = getServletContext().getRequestDispatcher("/clientList.jsp");
					List vec = HibernateToDoListDAO.Instance().getUsers();
					request.setAttribute("UsersList", vec);
					dispatcher.forward(request, response);
					break;
			case "/ChangingTasks":
					dispatcher = getServletContext().getRequestDispatcher("/UserTask.jsp");
					int taskNumber = Integer.parseInt((String)request.getParameter("taskNumber"));
					String taskName = (String)request.getParameter("taskname");
					String description = (String)request.getParameter("taskdescription");
					HibernateToDoListDAO.Instance().updateTask(taskNumber, taskName, description);
					dispatcher.forward(request, response);
					break;	
			case "/DeleteTasks":
					dispatcher = getServletContext().getRequestDispatcher("/UserTask.jsp");
					int taskNumber1 = Integer.parseInt((String)request.getParameter("taskNumber"));
					HibernateToDoListDAO.Instance().deleteTask(taskNumber1);
					dispatcher.forward(request, response);
					break;				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			dispatcher = getServletContext().getRequestDispatcher("/errorpage.jsp");
			dispatcher.forward(request, response);
		}
		/*
			//adding the user-agent to the DB also		
			String strUserAgent = request.getHeader("User-Agent");
			if(strUserAgent.contains("Chrome"))
			{
				strUserAgent = "Chrome";
			}
			else if(strUserAgent.contains("OS"))
			{
				strUserAgent = "Safari";
			}
			else if(strUserAgent.contains("Firefox"))
			{
				strUserAgent = "Firefox";
			}
			else strUserAgent = "Explorer/Edge";
	
			addTask2DB(request.getRequestedSessionId(),taskName,taskDescription,strUserAgent);
		*/
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
