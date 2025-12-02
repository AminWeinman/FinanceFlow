package Servlets;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// database stuff
import java.sql.Connection;
import java.sql.SQLException;
import db.DBUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 *
 * @author rapid
 */
@WebServlet(urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override   
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String uname = request.getParameter("uname");
        String pwd = request.getParameter("pwd");
        int uid = -1;
        
        try (Connection conn = DBUtil.getConnection()) {
            String userQuery = "SELECT uid FROM users WHERE name = ?";
            String sql = "SELECT * FROM users WHERE name = ? AND pwd = ?";
            
            
            try (PreparedStatement a = conn.prepareStatement(userQuery)) {
                a.setString(1, uname);
                ResultSet rs = a.executeQuery();
                
                if (!rs.next()) {
                    response.sendRedirect("index.html");
                    return;
                }
                
                uid = rs.getInt("uid");
            }
            
            
            try (PreparedStatement s = conn.prepareStatement(sql)) {
                s.setString(1, uname);
                s.setString(2, pwd);
                
                ResultSet rs = s.executeQuery();
                
                if (rs.next()) {
                    request.getSession().setAttribute("user", uname);
                    request.getSession().setAttribute("uid", uid);
                    
                    response.sendRedirect("home.html");
                    return;
                }
                else {
                    response.setContentType("text/html");
                    try (PrintWriter out = response.getWriter()) {
                        out.println("<h3>invalid username or password</h3>");
                        out.println("<a href='index.html'>Try Again</a>");
                    }
                }
            }
        } catch (Exception ex) {
            System.getLogger(LoginServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
