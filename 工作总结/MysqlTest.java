package com.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sudytech.security.support.AES;
import com.sudytech.security.support.SUDY;

public class MysqlTest {

	public static Connection getMysqlConn() throws Exception {
		String username = "root";
		String password = "12344";
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://170.18.10.70:3306/JUST_IDSPLUS?characterEncoding=UTF-8";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static void main(String[] args) throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			System.out.println("----------------------");
			String sql = "select LoginName,Password,Field29,IdCard from T_USER where length(Field29)=64 order by Id";
			conn = getMysqlConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String field29 = rs.getString("Field29");
				String loginName = rs.getString("LoginName");
				String md5Pass = SUDY.idsField29Decrypt(field29, loginName);
				if (md5Pass.startsWith("[MD5]")) {
					String newMd5Pass = md5Pass.replace("[MD5]", "{MD5}");
					String newField29 = AES.encrypt(newMd5Pass, "ids#s12" + loginName);
					System.out.println(
							"UPDATE T_USER SET Field29='" + newField29 + "' WHERE LoginName='" + loginName + "';");
				}

				// System.out.println(SUDY.idsField29Decrypt(field29, loginName));

				// String idCard = rs.getString("IdCard");
				// if (idCard != null && idCard.length() > 6) {
				// System.out.println(SUDY.idsPasswordEncrypt("",
				// idCard.substring(idCard.length() - 6)));
				// } else {
				// System.out.println("null");
				// }
			}
		} finally {
			releaseRes(rs, "");
			releaseRes(ps, "");
			releaseRes(conn, "");
		}
	}

	public static void releaseRes(ResultSet rs, String msg) {
		try {
			rs.close();
		} catch (Exception ex) {

		}
	}

	public static void releaseRes(PreparedStatement ps, String msg) {
		try {
			ps.close();
		} catch (Exception ex) {

		}
	}

	public static void releaseRes(Connection conn, String msg) {
		try {
			conn.close();
		} catch (Exception ex) {

		}
	}

}
