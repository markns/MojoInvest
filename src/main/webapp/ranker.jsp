<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ page import="com.google.appengine.demos.taskqueueexamples.Counter" %>--%>

<html>
<head>
    <title>Performance Calculator</title>
</head>
<body>

<%--<h1>Count is: <%= Counter.getCount("thecounter") %>--%>

<form action="/ranker" method="post">
    <input value="From Date" type="text" name="fromDate"/>
    <input value="To Date" type="text" name="toDate"/>
    <input value="Range" type="text" name="range"/>
    <input type="submit" value="Calculate">
</form>

</body>
</html>
