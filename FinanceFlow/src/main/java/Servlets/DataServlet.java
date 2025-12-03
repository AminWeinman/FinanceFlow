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
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        HttpSession session = request.getSession(false); // don’t create new session
        if (session == null || session.getAttribute("uid") == null) {
            // User not logged in
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("{\"error\":\"Not logged in\"}");
            return;
        }
        
        int uid = (int) request.getSession().getAttribute("uid");
        double totalIncome = 0.0;
        double totalExpenses = 0.0;
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        String incomeQuery = "SELECT SUM(amt) AS total_income FROM transactions WHERE uid = ? and transactions_type = 'income'";
        String expensesQuery = "SELECT SUM(amt) AS total_expenses FROM transactions WHERE uid = ? and transactions_type = 'expenses'";
        String transactionsQuery = "SELECT date, transactions_type, amt, description FROM transactions WHERE uid = ? ORDER BY date DESC";
        try (Connection conn = DBUtil.getConnection()) {
            // get income
            try (PreparedStatement s = conn.prepareStatement(incomeQuery)) {
                s.setInt(1, uid);
                ResultSet rs = s.executeQuery();
                
                if (rs.next()) {
                    totalIncome = rs.getDouble("total_income");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // get expenses
            try (PreparedStatement a = conn.prepareStatement(expensesQuery)) {
                a.setInt(1, uid);
                ResultSet rs = a.executeQuery();
                
                if (rs.next()) {
                    totalExpenses = rs.getDouble("total_expenses");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // get transactions
            
            try (PreparedStatement b = conn.prepareStatement(transactionsQuery)) {
                b.setInt(1, uid);
                ResultSet rs = b.executeQuery();
                
                while(rs.next()) {
                    Map<String, Object> tx = new HashMap<>();
                    tx.put("date", rs.getString("date"));
                    tx.put("transaction_type", rs.getString("transactions_type"));
                    tx.put("amt", rs.getDouble("amt"));
                    tx.put("description", rs.getString("description"));
                    transactions.add(tx);
                }
            }
        } catch (Exception ex) {
            System.getLogger(DataServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        response.setContentType("application/json;charset=UTF-8");
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalIncome\":").append(totalIncome).append(",");
        json.append("\"totalExpenses\":").append(totalExpenses).append(",");
        json.append("\"transactions\":[");

        for (int i = 0; i < transactions.size(); i++) {
            Map<String, Object> tx = transactions.get(i);
            json.append("{")
                .append("\"date\":\"").append(tx.get("date")).append("\",")
                .append("\"transaction_type\":\"").append(tx.get("transaction_type")).append("\",")
                .append("\"amt\":").append(tx.get("amt")).append(",")
                .append("\"description\":\"").append(tx.get("description")).append("\"")
                .append("}");
            if (i < transactions.size() - 1) json.append(",");
        }

        json.append("]}");

        response.getWriter().write(json.toString());
        
        //response.getWriter().write("{\"totalIncome\": " + totalIncome + ", \"totalExpenses\": " + totalExpenses + "}");
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
