package recommendation;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PoolsBuilder {

	private SqLiteHelper sql;
	private ResultSet rs, rs2;

	private String concat(String... s) {
		StringBuilder b = new StringBuilder();
		for (String x : s) {
			b.append(x);
		}
		return b.toString();
	}

	public DropDownResponse getTableName() throws ClassNotFoundException, SQLException {
		sql = new SqLiteHelper();
		DropDownResponse response = new DropDownResponse();
		rs = sql.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name!='sqlite_sequence'");
		try {
			while (rs.next()) {
				response.tableNamePool.add(rs.getString(1));
			}
		} catch (SQLException ex) {
			System.out.println("HNAcatch");
			Logger.getLogger(VisExtract.class.getName()).log(Level.SEVERE, null, ex);
		}

		return response;
	}

	public DropDownResponse getAll(String tableName) throws ClassNotFoundException, SQLException {

		sql = new SqLiteHelper();
		DropDownResponse response = new DropDownResponse();

		/*
		 * response.dimensionAttrPool.add("payment_type");
		 * 
		 * response.measureAttrPool.add("trip_distance");
		 * response.measureAttrPool.add("fare_amount");
		 * response.measureAttrPool.add("total_charged_amount_");
		 * 
		 * response.specificAttrPool.add("tip_amount");
		 */

		response.aggregatePool.add("AVG");
		response.aggregatePool.add("MAX");
		response.aggregatePool.add("SUM");
		response.aggregatePool.add("COUNT");

		rs = sql.executeQuery(concat("PRAGMA table_info(", tableName, ")"));

		while (rs.next()) {
			rs2 = sql.executeQuery(concat("SELECT ", rs.getString(2), " FROM ", tableName, " LIMIT 1"));
			if (rs.getString(2).toLowerCase().equals("tpep_pickup_datetime")
					|| rs.getString(2).equals("tpep_dropoff_datetime")
					|| rs.getString(2).equals("id") 
					|| rs.getString(2).equals("post_id")
					|| rs.getString(2).equals("post_date")
					|| rs.getString(2).equals("VendorID")
					|| rs.getString(2).equals("RatecodeID")) {
				continue;
			}
			if (rs2.getString(1).matches("-?\\d+(\\.\\d+)?") == true) {
				response.measureAttrPool.add(String.valueOf(rs.getString(2)));
			} else {
				response.dimensionAttrPool.add(String.valueOf(rs.getString(2)));
				response.specificAttrPool.add(String.valueOf(rs.getString(2)));
			}
		}

		return response;
	}

	public DropDownResponse getAll_SpecificAttribute(String tableName, String attribute)
			throws ClassNotFoundException, SQLException {
		sql = new SqLiteHelper();
		DropDownResponse response = new DropDownResponse();
		rs = sql.executeQuery(concat("SELECT DISTINCT ", attribute, " FROM ", tableName));
		try {
			while (rs.next()) {
				response.specificAttrResults.add(rs.getString(1));
			}
		} catch (SQLException ex) {
			System.out.println("HNAcatch");
			Logger.getLogger(VisExtract.class.getName()).log(Level.SEVERE, null, ex);
		}

		return response;
	}

}