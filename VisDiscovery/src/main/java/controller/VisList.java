package controller;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import recommendation.VisData;
import recommendation.VisExtract;
import recommendation.VisObject;
import result.*;

/**
 *
 * @author nada
 */

@Path("/charts")
public class VisList {

	private VisPools vp;
	private static String xAxis, yAxis, aggregateFunction, selectorOnDimension, SpecificAttribute, firstOperator,
			firstSelector, secondOperator, secondSelector, sameDimention, sameMeasure, sameAggrigate;
	private Result result;
	private static ArrayList<String> aggregatePool;
	private static ArrayList<String> measureAttributePool;
	private static ArrayList<String> dimentionAttributPool;

	@Path("/inputs")
	@POST
	public void inputs(@FormParam("xAxis") String xAxis, @FormParam("yAxis") String yAxis,
			@FormParam("aggregateFunction") String aggregateFunction,
			@FormParam("SpecificAttribute") String SpecificAttribute, @FormParam("firstOperator") String firstOperator,
			@FormParam("firstSelector") String firstSelector, @FormParam("secondOperator") String secondOperator,
			@FormParam("secondSelector") String secondSelector ) {

		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.aggregateFunction = aggregateFunction;
		this.selectorOnDimension = "";

		this.SpecificAttribute = SpecificAttribute;
		this.firstOperator = firstOperator;
		this.firstSelector = firstSelector;
		this.secondOperator = secondOperator;
		this.secondSelector = secondSelector;
		this.sameDimention="";
		this.sameMeasure="";
		this.sameAggrigate="";

		
		this.firstSelector = "'" + this.firstSelector + "'";
		this.secondSelector = "'" + this.secondSelector + "'";

	}

	@Path("/visualizations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)

	public ArrayList<Result> visulaiztions() throws ClassNotFoundException, SQLException {

		vp = new VisPools();
		this.dimentionAttributPool= new ArrayList<String>();
		this.measureAttributePool= new ArrayList<String>();
		this.aggregatePool= new ArrayList<String>();
		
		if (this.sameDimention.isEmpty()){
			this.dimentionAttributPool=vp.getDimentionAttributPool();
			if (this.dimentionAttributPool.contains(this.SpecificAttribute)){
				this.dimentionAttributPool.remove(this.dimentionAttributPool.indexOf(this.SpecificAttribute));
			}
		}
		else{
			this.dimentionAttributPool.add(this.sameDimention);
		}
		
		if (this.sameMeasure.isEmpty()){
			this.measureAttributePool=vp.getMeasureAttributePool();
		}
		else{
			this.measureAttributePool.add(this.sameMeasure);
		}
		
		if (this.sameAggrigate.isEmpty()){
			this.aggregatePool=vp.getAggregatePool();
		}
		else{
			this.aggregatePool.add(this.sameAggrigate);
		}
		

		VisExtract extract = new VisExtract(vp.getTableName(), this.xAxis, this.yAxis, this.aggregateFunction,
				this.selectorOnDimension, this.SpecificAttribute, this.firstOperator, this.firstSelector,
				this.secondOperator, this.secondSelector, this.dimentionAttributPool, this.measureAttributePool,
				this.aggregatePool);

		VisData mainVisualization = new VisData();
		ArrayList<VisData> recommendationList = new ArrayList<VisData>();

		mainVisualization = extract.getMainVis();
		recommendationList = extract.getRecommendations_bruteForce();
		//recommendationList = extract.getRecommendation_optimized();

		ArrayList<Result> visualizations = new ArrayList<Result>();

		result = new Result();

		result = getResult(mainVisualization.xAxisTitle, mainVisualization.yAxisTitle, mainVisualization.values,50);
		visualizations.add(result);

		for (int i = 0; i < recommendationList.size(); i++) {
			
			int width = 50;
			result = new Result();
			
			result = getResult(recommendationList.get(i).xAxisTitle,
					recommendationList.get(i).yAxisTitle, recommendationList.get(i).values, width);
			visualizations.add(result);

		}

		return visualizations;

	}

	public Result getResult(String xAxisTittle, String yAxisTittle, ArrayList<VisObject> values, int width) {

		Result result = new Result();

		Data data = new Data();
		data.setValues(values);

		Tuple tuple = new Tuple();
		tuple.setField(this.SpecificAttribute);
		tuple.setExpr("datum.queryNumber == 1 ?" + this.firstSelector + " : " + this.secondSelector);

		Transform transform = new Transform();
		transform.setCalculate(new Tuple[] { tuple });

		String[] range = { "#8B0000", "#00008B" };

		Scale scale = new Scale();
		// scale.setPadding(4);

		Scale XYscale = new Scale();
		// XYscale.setBandSize("fit");

		Scale ColorScale = new Scale();
		ColorScale.setRange(range);

		Axis axisX = new Axis();
		axisX.setAxisWidth(1);
		axisX.setOffset(-8);
		axisX.setOrient("bottom");
		axisX.setTitle(xAxisTittle);// must be imported
		axisX.setLabelAngle(-30);
		axisX.setLabelMaxLength(15);

		Axis axisY = new Axis();
		axisY.setGrid(false);
		axisY.setTitle(yAxisTittle);// must be imported

		Column column = new Column();
		column.setField("xAxis");
		column.setType("ordinal");
		column.setScale(scale);
		column.setAxis(axisX);

		xAxisFormatter x = new xAxisFormatter();
		x.setField(this.SpecificAttribute);
		x.setType("nominal");
		x.setScale(XYscale);
		x.setAxis(false);

		Column y = new Column();
		y.setField("yAxis");
		y.setType("quantitative");
		y.setAxis(axisY);
		y.setScale(XYscale);

		Column color = new Column();
		color.setField(this.SpecificAttribute);
		color.setType("nominal");
		color.setScale(ColorScale);

		Encoding encoding = new Encoding();
		encoding.setColumn(column);
		encoding.setX(x);
		encoding.setY(y);
		encoding.setColor(color);

		Cell cell = new Cell();
		cell.setStrokeWidth(0);

		Facet facet = new Facet();
		facet.setCell(cell);

		Config config = new Config();
		config.setFacet(facet);

		result.setData(data);
		result.setMark("bar");
		result.setTransform(transform);
		result.setConfig(config);
		result.setEncoding(encoding);
		result.setWidth(width);
		
		return result;
	}
}
