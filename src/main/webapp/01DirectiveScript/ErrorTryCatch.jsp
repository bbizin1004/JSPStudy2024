<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
/*
에러페이지 처리방법1
:try~catch문으로 예외가 발생되는 지점을 감싸준다.
*/

try{
	
int myAge = Integer.parseInt(request.getParameter("age"))+10;
out.println("10년 후 당신으 나이는 "+ myAge + "입니다.");
}catch(Exception e){
	out.println("예외 발생: 매개변수 age가 null입니다.");
}
%>
</body>
</html>