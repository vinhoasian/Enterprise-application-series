<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Project 3 Enterprise System - Data Entry System</title>
  <style>
    body { background-color: black; color: white; font-family: Arial, sans-serif; text-align: center; }
    h1 { color: yellow; font-size: 28px; }
    h2 { color: red; font-size: 20px; }
    .info { color: white; font-size: 14px; margin: 10px 0; }
    .info span { color: cyan; font-weight: bold; }
    fieldset { border: 1px solid yellow; margin: 10px auto; width: 80%; padding: 10px; }
    legend { color: white; font-size: 14px; }
    input[type="text"] {
      background-color: #665c1e;
      border: 2px solid yellow;
      color: white;
      padding: 5px;
      width: 120px;
      font-size: 14px;
    }
    table { margin: 0 auto; }
    th { color: green; font-size: 14px; padding: 4px 8px; }
    .btn-enter { background-color: #333; color: green; border: 2px solid green; padding: 6px 14px; font-weight: bold; cursor: pointer; }
    .btn-clear { background-color: #333; color: red;   border: 2px solid red;   padding: 6px 14px; font-weight: bold; cursor: pointer; }
    hr { border-color: yellow; }
    .msg-box { display: inline-block; background-color: blue; color: white; padding: 10px 20px; font-weight: bold; margin-top: 10px; }
    .err-box { display: inline-block; background-color: red;  color: yellow; padding: 10px 20px; font-weight: bold; margin-top: 10px; }
  </style>
</head>
<body>
  <h1>Welcome to the Enterprise Management System</h1>
  <h2>Data Entry Application</h2>
  <hr>
  <p class="info">You are connected to the Project 3 Enterprise System database as a <span>data-entry-level</span> user.<br>
  Enter the data values in a form below to add a new record to the corresponding database table.</p>

  <!-- Suppliers -->
  <fieldset><legend>Suppliers Record Insert</legend>
    <form action="/Project-3/AddSupplierRecord" method="post">
      <table><tr>
        <th>snum</th><th>sname</th><th>status</th><th>city</th>
      </tr><tr>
        <td><input type="text" name="snum"></td>
        <td><input type="text" name="sname"></td>
        <td><input type="text" name="status"></td>
        <td><input type="text" name="city"></td>
      </tr></table>
      <br>
      <input type="submit" class="btn-enter" value="Enter Supplier Record Into Database">
      <input type="reset"  class="btn-clear" value="Clear Data and Results">
    </form>
  </fieldset>

  <!-- Parts -->
  <fieldset><legend>Parts Record Insert</legend>
    <form action="/Project-3/AddPartRecord" method="post">
      <table><tr>
        <th>pnum</th><th>pname</th><th>color</th><th>weight</th><th>city</th>
      </tr><tr>
        <td><input type="text" name="pnum"></td>
        <td><input type="text" name="pname"></td>
        <td><input type="text" name="color"></td>
        <td><input type="text" name="weight"></td>
        <td><input type="text" name="city"></td>
      </tr></table>
      <br>
      <input type="submit" class="btn-enter" value="Enter Part Record Into Database">
      <input type="reset"  class="btn-clear" value="Clear Data and Results">
    </form>
  </fieldset>

  <!-- Jobs -->
  <fieldset><legend>Jobs Record Insert</legend>
    <form action="/Project-3/AddJobRecord" method="post">
      <table><tr>
        <th>jnum</th><th>jname</th><th>numworkers</th><th>city</th>
      </tr><tr>
        <td><input type="text" name="jnum"></td>
        <td><input type="text" name="jname"></td>
        <td><input type="text" name="numworkers"></td>
        <td><input type="text" name="city"></td>
      </tr></table>
      <br>
      <input type="submit" class="btn-enter" value="Enter Job Record Into Database">
      <input type="reset"  class="btn-clear" value="Clear Data and Results">
    </form>
  </fieldset>

  <!-- Shipments -->
  <fieldset><legend>Shipments Record Insert</legend>
    <form action="/Project-3/AddShipmentRecord" method="post">
      <table><tr>
        <th>snum</th><th>pnum</th><th>jnum</th><th>quantity</th>
      </tr><tr>
        <td><input type="text" name="snum"></td>
        <td><input type="text" name="pnum"></td>
        <td><input type="text" name="jnum"></td>
        <td><input type="text" name="quantity"></td>
      </tr></table>
      <br>
      <input type="submit" class="btn-enter" value="Enter Shipment Record Into Database">
      <input type="reset"  class="btn-clear" value="Clear Data and Results">
    </form>
  </fieldset>

  <hr>
  <b>Execution Results:</b><br><br>
  <%
    String msg = (String) request.getAttribute("message");
    String err = (String) request.getAttribute("error");
    if (msg != null) { %>
      <div class="msg-box"><%= msg %></div>
    <% } else if (err != null) { %>
      <div class="err-box"><%= err %></div>
    <% } %>
</body>
</html>
