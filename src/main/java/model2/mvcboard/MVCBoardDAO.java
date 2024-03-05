package model2.mvcboard;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import common.DBConnPool;

//MVC 게시판은 DBCP(커넥션풀)를 통해 DB에 연결한다.
public class MVCBoardDAO extends DBConnPool {

	public MVCBoardDAO() {
		super();
	}

	// 게시물의 갯수 카운트
	public int selectCount(Map<String, Object> map) {
		int totalCount = 0;

		String query = "SELECT COUNT(*) FROM mvcboard ";
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " " + " LIKE '%" + map.get("searchWord") + "%'";
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			rs.next();
			totalCount = rs.getInt(1);
		} catch (Exception e) {
			System.out.println("게시물 수를 구하는 중 예외 발생");
			e.printStackTrace();
		}
		return totalCount;

	}

	// 게시물의 목록 출력시 페이징 기능 추가
	public List<MVCBoardDTO> selectListPage(Map<String, Object> map) {
		List<MVCBoardDTO> board = new Vector<MVCBoardDTO>();

		/*
		 * 검색조건에 일치하는 게시물을 얻어온 후 각 페이지에 출력할 구간까지 설정한 서브쿼리문 작성
		 */
		String query = "SELECT * FROM ( " + "  SELECT Tb.*, ROWNUM rNum FROM ( " + "     SELECT * FROM mvcboard ";
		// 검색어가 있는 경우에만 where절을 추가한다.
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " LIKE '%" + map.get("searchWord") + "%' ";
		}
		/*
		 * 게시물의 구간을 결정하기 위해 between 혹은 비교연산자를 사용할 수 있다. 아래의 where절은 rNum>? 과 같이 변경할 수
		 * 있다.
		 */
		query += "     ORDER BY idx DESC " + "    ) Tb " + " ) " + " WHERE rNum BETWEEN ? AND ?";

		try {
			// 인파라미터가 있는 쿼리문이므로 prepared 인스턴스 생성
			psmt = con.prepareStatement(query);
			// 인파라미터 설정(출력할 페이지의 구간)
			psmt.setString(1, map.get("start").toString());
			psmt.setString(2, map.get("end").toString());
			rs = psmt.executeQuery();
			while (rs.next()) {
				MVCBoardDTO dto = new MVCBoardDTO();
				// 일련번호~조회수까지 DTO에 저장
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				dto.setDowncount(rs.getInt(8));
				dto.setPass(rs.getString(9));
				dto.setVisitcount(rs.getInt(10));
				// 하나의 레코드를 저장한 DTO를 List에 추가
				board.add(dto);
			}
		} catch (Exception e) {
			System.out.println("게시물 조회 중 예외 발생");
			e.printStackTrace();
		}
		return board;
	}

	public int insertWrite(MVCBoardDTO dto) {
		int result = 0;
		try {
			
			/*
			쿼리문의 일련번호는 모델1 게시판에서 생성한 시퀀스를 그대로
			사용한다. 나머지 값들은 컨트롤러(서블릿)에서 받은 후 모델(DAO)로
			전달한다.
			*/
			String query = "INSERT INTO mvcboard ( " + " idx,name,title,content,ofile,sfile,pass) "
					+ " VALUES ( "
					+ " seq_board_num.NEXTVAL,?,?,?,?,?,?)";
			
			psmt = con.prepareStatement(query);
			psmt.setString(1, dto.getName());
			psmt.setString(2, dto.getTitle());
			psmt.setString(3, dto.getContent());
			psmt.setString(4, dto.getOfile());
			psmt.setString(5, dto.getSfile());
			psmt.setString(6, dto.getPass());
			result = psmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("게시물 입력 중 예외 발생");
			e.printStackTrace();
		}
		return result;
	}
	
	//내용보기
	public MVCBoardDTO selectView(String idx) {
		MVCBoardDTO dto = new MVCBoardDTO();
		//일련번호와 일치하는 게시물 1개 인출
		String query = "SELECT * FROM mvcboard WHERE idx=?";
		try {
			psmt= con.prepareStatement(query);
			psmt.setString(1, idx);
			rs=psmt.executeQuery();
			
			if(rs.next()) {
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				dto.setDowncount(rs.getInt(9));
				dto.setPass(rs.getString(9));
				dto.setVisitcount(rs.getInt(10));
			}
			
			
		} catch (Exception e) {
			System.out.println("게시물 상세보기 중 예외발생");
			e.printStackTrace();
		}
		return dto;
	}
	//조회수 증가
	 public void updateVisitCount(String idx) {
		String query = "UPDATE mvcboard SET "
					+ " visitcount=visitcount+1 "
					+ "WHERE idx=?";
		try {
			psmt =con.prepareStatement(query);
			psmt.setString(1, idx);
			psmt.executeQuery();
		} catch (Exception e) {
			System.out.println("게시물 조회수 증가중 예외발생");
			e.printStackTrace();
		}
	}
	 
	 public void downCountPlus(String idx) {
		String sql = "UPDATE mvcboard SET "
				+  " downcount=downcount+1 "
				+ " WHERE idx=? ";
		try {
			psmt= con.prepareStatement(sql);
			psmt.setString(1, idx);
			psmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	 
}
