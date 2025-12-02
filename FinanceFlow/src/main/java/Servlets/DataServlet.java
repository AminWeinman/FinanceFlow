package Servlets;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import db.DBUtil;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author rapid
 */
@WebServlet(urlPatterns = {"/DataServlet"})
public class DataServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DataServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DataServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        int uid = (int) request.getSession().getAttribute("uid");
        double totalIncome = 0.0;
        
        String sql = "SELECT SUM(amt) AS total_income FROM transactions WHERE uid = ? and transactions_type = 'income'";
        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement s = conn.prepareStatement(sql)) {
                s.setInt(1, uid);
                ResultSet rs = s.executeQuery();
                
                if (rs.next()) {
                    totalIncome = rs.getDouble("total_income");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            System.getLogger(DataServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"totalIncome\": " + totalIncome + "}");
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
        String call = request.getParameter("call");
        switch (call) {
            case "addTransaction" -> handleTransaction(request, response);
        }
    }
    
    protected void handleTransaction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("transaction_type");
        String amtStr = request.getParameter("amt");
        String desc = request.getParameter("description");
        
        // String uname = (String) request.getSession().getAttribute("user");
        int uid = (int) request.getSession().getAttribute("uid");
        
        // some error checks
        if (uid == -1) {
            response.sendRedirect("index.html");
            return;
        }
        
        if (amtStr == null || type == null || desc == null) {
            response.sendRedirect("home.html");
            return;
        }
        
        
        double amt = Double.parseDouble(amtStr);
        
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("SUCCESS: Connected to MySQL database!");
           
            String query = "INSERT INTO transactions (uid, transactions_type, amt, description, date) VALUES (?, ?, ?, ?, ?)";
            
            
            
            try (PreparedStatement s = conn.prepareStatement(query)) {
                s.setInt(1, uid);
                s.setString(2, type);
                s.setDouble(3, amt);
                s.setString(4, desc);
                s.setObject(5, LocalDateTime.now());

                s.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace(); // prints to Tomcat log
                response.setContentType("text/plain");
                response.getWriter().println("SERVER ERROR:\n" + e.toString());
                return;
            }
        }   catch (Exception e) {
                e.printStackTrace(); // prints to Tomcat log
                response.setContentType("text/plain");
                response.getWriter().println("SERVER ERROR:\n" + e.toString());
                return;
        }

            response.sendRedirect("home.html");
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
