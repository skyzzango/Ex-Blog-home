package com.cos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cos.dto.MemberVO;
import com.cos.util.DBManager;

public class MemberDAO {
	private PreparedStatement pstmt;
	private ResultSet rs;

	// insert
	public int insert(MemberVO member) {
		String SQL = "INSERT INTO member VALUES(?,?,?,?,?,?,false)";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPassword());
			pstmt.setString(3, member.getUsername());
			pstmt.setString(4, member.getRoadFullAddr());
			pstmt.setString(5, member.getEmail());
			pstmt.setString(6, member.getSalt());
			pstmt.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt);
		}
		return -1;
	}

	// select_id
	public int select_id(MemberVO member) {
		String SQL = "SELECT emailcheck FROM member WHERE id = ? AND password= ?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPassword());
			rs = pstmt.executeQuery();

			if (rs.next()){
				boolean emailcheck = rs.getBoolean("emailcheck");
				if(emailcheck == true){
					return 1;
				}else{
					return 2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt, rs);
		}
		return -1;
	}

	// select_all
	public MemberVO select_all(String id) {
		String SQL = "SELECT id, password, username, roadFullAddr, email, emailcheck FROM member WHERE id = ?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				MemberVO member = new MemberVO();
				member.setId(rs.getString("id"));
				member.setUsername(rs.getString("username"));
				member.setRoadFullAddr(rs.getString("roadFullAddr"));
				member.setEmail(rs.getString("email"));
				member.setEmailcheck(rs.getBoolean("emailcheck"));
				return member;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt, rs);
		}
		return null;
	}
	
	// update
	public int update(MemberVO member) {
		String SQL = "UPDATE member SET password = ?, roadFullAddr = ?, email = ? WHERE id = ?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, member.getPassword());
			pstmt.setString(2, member.getRoadFullAddr());
			pstmt.setString(3, member.getEmail());
			pstmt.setString(4, member.getId());
			return pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt);
		}
		return -1;
	}	
	
	public String select_salt(String id){
		String SQL = "SELECT salt FROM member WHERE id = ?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String salt = rs.getString("salt");
				return salt;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt, rs);
		}
		return null;
	}
	
	public int update_emailcheck(String id){
		String SQL = "UPDATE member SET emailcheck = true WHERE id = ?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt);
		}
		return -1;
	}
	
	public int select_emailcheck(String id){
		String SQL = "SELECT emailcheck FROM member where id =?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				boolean emailcheck = rs.getBoolean("emailcheck");
				if(emailcheck == true){
					return 1;
				}else{
					return 2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt);
		}
		return -1;
	}
	
	public String select_email(String id){
		String SQL = "SELECT email FROM member WHERE id = ?";
		Connection conn = DBManager.getConnection();
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String email = rs.getString("email");
				return email;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager.close(conn, pstmt, rs);
		}
		return null;
	}
	
}
