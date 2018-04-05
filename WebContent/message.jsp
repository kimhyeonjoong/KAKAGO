<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.*, java.lang.*, org.json.simple.*"%>

{
    "message":{
        "text" : "<%=request.getAttribute("en_reply") %>"
    }
}


