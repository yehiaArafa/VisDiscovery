package recommendation;

import java.util.ArrayList;

/**
 *
 * @author esraa
 */

public class DropDownResponse {

	public ArrayList<String> tableNamePool;
	public ArrayList<String> dimensionAttrPool;
	public ArrayList<String> measureAttrPool;
	public ArrayList<String> specificAttrPool;
	public ArrayList<String> aggregatePool;
	public ArrayList<String> specificAttrResults;

	public DropDownResponse() {
		this.tableNamePool = new ArrayList<String>();
		this.dimensionAttrPool = new ArrayList<String>();
		this.measureAttrPool = new ArrayList<String>();
		this.specificAttrPool = new ArrayList<String>();
		this.aggregatePool = new ArrayList<String>();
		this.specificAttrResults = new ArrayList<String>();
	}

}
