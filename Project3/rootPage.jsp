<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
  <title>Enterprise Web Management System</title>
  <style>
    body { background-color: black; color: white; font-family: Arial, sans-serif; text-align: center; }
    h1 { color: green; font-size: 28px; }
    h2 { color: orange; font-size: 20px; }
    .info { color: white; font-size: 14px; margin: 10px 0; }
    .info span { color: red; font-weight: bold; }
    textarea {
      width: 60%;
      height: 150px;
      background-color: blue;
      color: white;
      font-size: 16px;
      border: none;
      padding: 10px;
    }
    .btn-execute { background-color: #333; color: green; border: 2px solid green; padding: 8px 16px; font-weight: bold; cursor: pointer; }
    .btn-reset   { background-color: red;  color: white; border: none; padding: 8px 16px; font-weight: bold; cursor: pointer; }
    .btn-clear   { background-color: #333; color: cyan;  border: 2px solid cyan; padding: 8px 16px; font-weight: bold; cursor: pointer; }
    hr { border-color: white; }
    .results { margin-top: 20px; }
    table { margin: 0 auto; border-collapse: collapse; }
    th { background-color: red; color: yellow; padding: 6px 12px; border: 1px solid white; }
    td { background-color: black; color: white; padding: 6px 12px; border: 1px solid white; }
    .msg-box { display: inline-block; background-color: green; color: yellow; padding: 10px 20px; font-weight: bold; margin-top: 10px; }
    .err-box { display: inline-block; background-color: red; color: yellow; padding: 10px 20px; font-weight: bold; margin-top: 10px; }
  </style>
</head>
<body>
  <h1>Welcome to the Enterprise Management System</h1>
  <h2>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h2>
  <hr>
  <p class="info">You are connected to the Project 3 Enterprise System database as a <span>root-level</span> user.<br>
  Please enter any SQL query or update command in the box below.</p>

  <form action="/Project-3/RootUserApp" method="post">
    <textarea id="sqlInput" name="input"><%= request.getAttribute("input") != null ? request.getAttribute("input") : "" %></textarea>
    <br><br>
    <input type="submit" class="btn-execute" value="Execute Command">
    <button type="button" class="btn-reset" onclick="document.getElementById('sqlInput').value=''">Reset Form</button>
    <button type="button" class="btn-clear" onclick="document.getElementById('results').innerHTML='<b>Execution Results:</b>'">Clear Results</button>
  </form>

  <hr>
  <div class="results" id="results">
    <b>Execution Results:</b><br><br>
    <%
      String msg = (String) request.getAttribute("message");
      String err = (String) request.getAttribute("error");
      List<String> headers = (List<String>) request.getAttribute("headers");
      List<List<String>> rows = (List<List<String>>) request.getAttribute("queryResults");

      if (msg != null) { %>
        <div class="msg-box"><%= msg %></div>
      <% } else if (err != null) { %>
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
