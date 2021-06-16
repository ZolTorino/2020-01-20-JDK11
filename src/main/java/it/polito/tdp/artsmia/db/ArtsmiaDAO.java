package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Arco;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
public List<String> roles() {
		
		String sql = "SELECT distinct role from authorship";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("role"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
public void vertices(String role, Map<Integer, Artist>idMap) {
	
	String sql = "SELECT ar.artist_id AS id,ar.name AS name "
			+ "FROM artists ar, authorship au "
			+ "WHERE ar.artist_id=au.artist_id AND au.role=?";
	
	Connection conn = DBConnect.getConnection();

	try {
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, role);
		ResultSet res = st.executeQuery();
		while (res.next()) {

			idMap.put(res.getInt("id"), new Artist(res.getInt("id"),res.getString("name")));
		}
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
		
	}
}

public List<Arco> edges(String role, Map<Integer, Artist>idMap) {
	List<Arco> result = new ArrayList<>();
	String sql = "SELECT ar1.artist_id as id1, ar2.artist_id as id2, COUNT(e1.object_id) as peso "
			+ "FROM artists ar1, authorship au1,artists ar2, authorship au2, objects o1, objects o2, exhibition_objects e1, exhibition_objects e2 "
			+ "WHERE ar1.artist_id=au1.artist_id "
			+ "AND au1.role=? "
			+ "AND  ar2.artist_id=au2.artist_id "
			+ "AND au2.role=au1.role "
			+ "AND au1.object_id= o1.object_id "
			+ "AND au2.object_id= o2.object_id "
			+ "AND e1.object_id= o1.object_id "
			+ "AND e2.object_id= o2.object_id "
			+ "AND e1.exhibition_id=e2.exhibition_id "
			+ "AND ar1.artist_id> ar2.artist_id "
			+ "GROUP BY ar1.artist_id, ar2.artist_id "
			+ "";
	
	Connection conn = DBConnect.getConnection();

	try {
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, role);
		ResultSet res = st.executeQuery();
		while (res.next()) {
			result.add(new Arco(idMap.get(res.getInt("id1")),idMap.get(res.getInt("id2")),res.getInt("peso")));
		}
		conn.close();
		return result;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	}
}
}
