<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
  <title>Enterprise Web Management System - Accountant</title>
  <style>
    body { background-color: black; color: white; font-family: Arial, sans-serif; text-align: center; }
    h1 { color: yellow; font-size: 28px; }
    h2 { color: green; font-size: 20px; }
    .info { color: white; font-size: 14px; margin: 10px 0; }
    .info span { color: orange; font-weight: bold; }
    .report-list { text-align: left; display: inline-block; margin: 20px auto; background-color: #333; padding: 20px 40px; }
    .report-list label { color: cyan; font-size: 16px; display: block; margin: 10px 0; cursor: pointer; }
    .btn-execute { background-color: #333; color: green; border: 2px solid green; padding: 8px 16px; font-weight: bold; cursor: pointer; }
    .btn-clear   { background-color: #333; color: cyan;  border: 2px solid cyan;  padding: 8px 16px; font-weight: bold; cursor: pointer; }
    hr { border-color: white; }
    .results { margin-top: 20px; }
    table { margin: 0 auto; border-collapse: collapse; }
    th { background-color: red; color: yellow; padding: 6px 12px; border: 1px solid white; }
    td { background-color: black; color: white; padding: 6px 12px; border: 1px solid white; }
    .err-box { display: inline-block; background-color: red; color: yellow; padding: 10px 20px; font-weight: bold; margin-top: 10px; }
  </style>
</head>
<body>
  <h1>Welcome to the Enterprise Management System</h1>
  <h2>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h2>
  <hr>
  <p class="info">You are connected to the Project 3 Enterprise System database as an <span>accountant-level</span> user.<br>
  Please select the operation you would like to perform from the list below.</p>

  <form action="/Project-3/AccountantUserApp" method="post">
    <div class="report-list">
      <label><input type="radio" name="query" value="Get_The_Maximum_Status_Of_All_Suppliers">
        Get The Maximum Status Value Of All Suppliers (Returns a maximum value)</label>
      <label><input type="radio" name="query" value="Get_The_Sum_Of_All_Parts_Weights">
        Get The Total Weight Of All Parts (Returns a sum)</label>
      <label><input type="radio" name="query" value="Get_The_Total_Number_Of_Shipments">
        Get The Total Number of Shipments (Returns the current number of shipments in total)</label>
      <label><input type="radio" name="query" value="Get_The_Name_Of_The_Job_With_The_Most_Workers">
        Get The Name And Number Of Workers Of The Job With The Most Workers (Returns two values)</label>
      <label><input type="radio" name="query" value="List_The_Name_And_Status_Of_All_Suppliers">
        List The Name And Status Of Every Supplier (Returns a list of supplier names with their current status)</label>
    </div>
    <br>
    <input type="submit" class="btn-execute" value="Execute Command">
    <button type="button" class="btn-clear" onclick="document.getElementById('results').innerHTML='<b>Execution Results:</b>'">Clear Results</button>
  </form>

  <hr>
  <div class="results" id="results">
    <b>Execution Results:</b><br><br>
    <%
      String err = (String) request.getAttribute("error");
      List<String> headers = (List<String>) request.getAttribute("headers");
      List<List<String>> rows = (List<List<String>>) request.getAttribute("queryResults");

      if (err != null) { %>
        <div class="err-box"><%= err %></div>
      <% } else if (headers != null && rows != null) { %>
        <table>
          <tr><% for (String h : headers) { %><th><%= h %></th><% } %></tr>
          <% for (List<String> row : rows) { %>
            <tr><% for (String col : row) { %><td><%= col %></td><% } %></tr>
          <% } %>
        </table>
      <% } %>
  </div>
</body>
</html>
