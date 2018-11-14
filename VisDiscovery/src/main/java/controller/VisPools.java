package controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.*;

import recommendation.DropDownResponse;
import recommendation.PoolsBuilder;

/**
 *
 * @author nada
 */

@Path("/choices")
public class VisPools {

	private PoolsBuilder pb;
	private static String tableName;
	private static ArrayList<String> aggregatePool;
	private static ArrayList<String> measureAttributePool;
	private static ArrayList<String> dimentionAttributPool;
	private static ArrayList<String> specificAttributePool;
	private static JSONObject pools;

	@Path("/getTableNamesPool")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DropDownResponse getTablesnames() throws ClassNotFoundException, SQLException {
		DropDownResponse res = new DropDownResponse();
		PoolsBuilder data = new PoolsBuilder();
		res = data.getTableName();
		return res;
	}

	@Path("/postTableName")
	@POST
	public Response postTables(@FormParam("tableName") String tableName)
			throws URISyntaxException, ClassNotFoundException, SQLException {
		this.tableName = tableName;

		return Response.temporaryRedirect(new URI("http://localhost:8080/VisDiscovery/inputs.html")).build();
	}

	@Path("/getPools")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DropDownResponse loadDropDowns() throws ClassNotFoundException, SQLException {

		this.dimentionAttributPool = new ArrayList<String>();
		this.measureAttributePool = new ArrayList<String>();
		this.aggregatePool = new ArrayList<String>();

		DropDownResponse res = new DropDownResponse();
		PoolsBuilder data = new PoolsBuilder();
		res = data.getAll(this.tableName);

		this.dimentionAttributPool = res.dimensionAttrPool;
		this.measureAttributePool = res.measureAttrPool;
		this.aggregatePool = res.aggregatePool;

		return res;
	}

	@Path("/getSAttrPoolsRes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DropDownResponse loadDropDowns_SAttribute() throws ClassNotFoundException, SQLException {

		DropDownResponse res = new DropDownResponse();
		PoolsBuilder data = new PoolsBuilder();
		res = data.getAll_SpecificAttribute(this.tableName, "tip_amount");

		return res;
	}

	public String getTableName() {
		return this.tableName;
	}

	public static ArrayList<String> getAggregatePool() {
		return aggregatePool;
	}

	public static ArrayList<String> getMeasureAttributePool() {
		return measureAttributePool;
	}

	public static ArrayList<String> getDimentionAttributPool() {
		return dimentionAttributPool;
	}

}
