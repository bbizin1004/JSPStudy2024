<%@page import="model1.board.BoardDAO"%>
<%@page import="model1.board.BoardDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./IsLoggedIn.jsp" %>

<%
//폼값으로 전송된 일련번호를 받는다.
String num = request.getParameter("num");

BoardDTO dto = new BoardDTO();
BoardDAO dao = new BoardDAO(application);
//본인 확인을 위해 게시물을 인출한다.
dto= dao.selectView(num);

/*
세션영역에 저장된 로그인 아이디를 얻어온 후 String 타입으로 변환한다.
Object타입으로 저장되므로 사용시에는 반드시 강체형변환 해야한다.
*/

//String sessionId = session.getAttribute("UserId").toString();
String sessionId = (String)session.getAttribute("UserId");

int delResult =0;

//세션아이디와 게시물 아이디가 일치하면 게시물을 삭제한다.
if(sessionId.equals(dto.getId())){
	dto.setNum(num);
	delResult = dao.deletePost(dto);
	dao.Close();
	
	if(delResult ==1){
		//게시물이 삭제되면 목록으로 이동한다.
		JSFunction.alertLocation("삭제되었습니다.", "List.jsp", out);
	}
	else{
		//실패하면 뒤로 이동한다.
		JSFunction.alertBack("삭제에 실패하였습니다.", out);
	}
}
else{
	JSFunction.alertBack("본인만 삭제할수 있습니다.", out);
	return;
}
%>