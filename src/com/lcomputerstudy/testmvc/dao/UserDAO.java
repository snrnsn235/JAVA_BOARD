package com.lcomputerstudy.testmvc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lcomputerstudy.testmvc.vo.Pagination;
import com.lcomputerstudy.testmvc.vo.User;
import com.lcomputerstudy.testmvc.database.DBConnection;

public class UserDAO {
	private static UserDAO dao = null;
	
	private UserDAO() {
		
	}
	
	public static UserDAO getInstance() {
		if(dao == null) {
			dao = new UserDAO();
		}
		return dao;
	}
	
	public List<User> getUsers(Pagination pagination) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<User> list = null;
		int pageNum = pagination.getPageNum();
		
		try {
			conn = DBConnection.getConnection();
			String query = new StringBuilder()
					.append("SELECT 		@ROWNUM := @ROWNUM - 1 AS ROWNUM,\n")
					.append("				ta.*\n")
					.append("FROM 			user ta,\n")
					.append("				(SELECT @rownum := (SELECT	COUNT(*)-?+1 FROM user ta)) tb\n")
					.append("LIMIT			?, ?\n")
					.toString();
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, pageNum);
			pstmt.setInt(2, pageNum);
			pstmt.setInt(3, Pagination.perPage);
			rs = pstmt.executeQuery();
			list = new ArrayList<User>();
			
			while(rs.next()) {
				User user = new User();
				user.setRownum(rs.getInt("ROWNUM"));
				user.setU_idx(rs.getInt("u_idx"));
				user.setU_id(rs.getString("u_id"));
				user.setU_name(rs.getString("u_name"));
				user.setU_tel(rs.getString("u_tel"));
				user.setU_age(rs.getString("u_age"));
				
				list.add(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(rs != null) rs.close();
					if(pstmt != null) pstmt.close();
					if(conn != null) conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return list;
	}
	
	public void insertUser(User user) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBConnection.getConnection();
			String sql = "insert into user(u_id, u_pw, u_name, u_tel, u_age) values(?,?,?,?,?)";
			pstmt.setString(1,  user.getU_id());
			pstmt.setString(2,  user.getU_pw());
			pstmt.setString(3,  user.getU_name());
			pstmt.setString(4,  user.getU_tel());
			pstmt.setString(5,  user.getU_age());
			pstmt.executeUpdate();
		} catch (Exception ex) {
			System.out.println("SQLException : " + ex.getMessage());
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getUsersCount() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT COUNT(*) count FROM user ";
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				count = rs.getInt("count");
			}
		} catch (Exception e) {
			
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
}
