package recommendation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninja
 */
public class VisExtract {

	private String tableName;
	private String x_axis;
	private String y_axis;
	private String aggregateFunction;
	private String selectorOnDimension;
	private String SpecificAttribute;
	private String firstOperator;
	private String firstSelector;
	private String secondOperator;
	private String secondSelector;
	private ArrayList<String> dimentionAttributeArray;
	private ArrayList<String> aggregateArray;
	private ArrayList<String> MeasureAttributeArray;

	private ArrayList<VisData> mainVisList;
	private ArrayList<VisData> recommendationList_bruteForce;
	private ArrayList<VisData> recommendationList_optimized;

	private VisData vd;
	private SqLiteHelper sql;
	private ResultSet rs;

	public VisExtract(String tableName, String xAxis, String yAxis, String aggregate, String selectorOnDimension,
			String attribute, String firstOperator, String firstSelector, String secondOperator, String secondSelector,
			ArrayList<String> dimentionAttributeArray, ArrayList<String> MeasureAttributeArray,
			ArrayList<String> aggregateArray) throws ClassNotFoundException, SQLException {

		this.dimentionAttributeArray = new ArrayList<String>();
		this.MeasureAttributeArray = new ArrayList<String>();
		this.aggregateArray = new ArrayList<String>();

		this.tableName = tableName;
		this.x_axis = xAxis;// D
		this.y_axis = yAxis;// M
		this.aggregateFunction = aggregate;// F
		this.selectorOnDimension = selectorOnDimension;
		this.SpecificAttribute = attribute;
		this.firstOperator = firstOperator;
		this.firstSelector = firstSelector;
		this.secondOperator = secondOperator;
		this.secondSelector = secondSelector;
		this.dimentionAttributeArray = dimentionAttributeArray;
		this.MeasureAttributeArray = MeasureAttributeArray;
		this.aggregateArray = aggregateArray;

		this.mainVisList = new ArrayList<VisData>();
		this.recommendationList_bruteForce = new ArrayList<VisData>();
		this.recommendationList_optimized = new ArrayList<VisData>();

		this.sql = new SqLiteHelper();
	}

	private String concat(String... s) {
		StringBuilder b = new StringBuilder();
		for (String x : s) {
			b.append(x);
		}
		return b.toString();
	}

	public VisData getMainVis() {
		/*
		 * Expected Distribution: //Run the query on the entire data - not
		 * spacifying any selection this is is case the user specify only one
		 * selector
		 * 
		 * Actual Distribution://Run the same query on the subset of data we are
		 * interested in this os case he give only one selector
		 */

		vd = new VisData();
		vd.xAxisTitle = this.x_axis;
		vd.yAxisTitle = concat(this.aggregateFunction, " ", "(", this.y_axis, ")");

		/*
		 * e.g: "SELECT x-axis, aggregate(y-axis),CASE WHEN Specific-attribute
		 * '=' first-selector THEN 1 WHEN Specific-attribute '=' second-selector
		 * THEN 2 END as g1 FROM table-name WHERE y-axis '='
		 * selector-on-dimension GROUP BY x-axis, g1"
		 */
		if (this.selectorOnDimension.equals("")) {

			rs = sql.executeQuery(concat("SELECT ", this.x_axis, ",", this.aggregateFunction, "(", this.y_axis, ")",
					",", "CASE WHEN ", this.SpecificAttribute, this.firstOperator, this.firstSelector, " THEN 1",
					" WHEN ", this.SpecificAttribute, this.secondOperator, this.secondSelector, " THEN 2 END as g1",
					" FROM ", this.tableName, " GROUP BY ", this.x_axis, ",", "g1"));

		} else {

			rs = sql.executeQuery(concat("SELECT ", this.x_axis, ",", this.aggregateFunction, "(", this.y_axis, ")",
					"," + "CASE WHEN ", this.SpecificAttribute + this.firstOperator + this.firstSelector, " THEN 1",
					" WHEN ", this.SpecificAttribute, this.secondOperator, this.secondSelector, " THEN 2 END as g1",
					" FROM ", this.tableName, " WHERE ", this.y_axis, "=", this.selectorOnDimension, " GROUP BY ",
					this.x_axis, ",", "g1"));
		}

		try {
			while (rs.next()) {

				if (rs.getString(3) != null) {

					if (rs.getString(3).equals("1")) {
						vd.recomQuery1.put(rs.getString(1), rs.getString(2));
					} else if (rs.getString(3).equals("2")) {
						vd.recomQuery2.put(rs.getString(1), rs.getString(2));
					}
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(VisExtract.class.getName()).log(Level.SEVERE, null, ex);
		}

		this.mainVisList.add(vd);
		this.euclidean_populate(mainVisList);
		return this.mainVisList.get(0);
	}

	// Try all: D * F * M
	public ArrayList<VisData> getRecommendations_bruteForce() {

		for (int i = 0; i < this.dimentionAttributeArray.size(); i++) {

			for (int j = 0; j < this.aggregateArray.size(); j++) {

				for (int k = 0; k < this.MeasureAttributeArray.size(); k++) {

					vd = new VisData();
					vd.xAxisTitle = this.dimentionAttributeArray.get(i);
					vd.yAxisTitle = concat(this.aggregateArray.get(j), " ", "(", this.MeasureAttributeArray.get(k),
							")");

					rs = sql.executeQuery(concat("SELECT ", this.dimentionAttributeArray.get(i), ",",
							this.aggregateArray.get(j), "(", this.MeasureAttributeArray.get(k), ")", "," + "CASE WHEN ",
							this.SpecificAttribute + this.firstOperator, this.firstSelector, " THEN 1", " WHEN ",
							this.SpecificAttribute, this.secondOperator, this.secondSelector, " THEN 2 END as g1",
							" FROM ", this.tableName, " GROUP BY ", this.dimentionAttributeArray.get(i), ",", "g1"));

					try {

						while (rs.next()) {

							if (rs.getString(3) != null) {

								if (rs.getString(3).equals("1")) {
									vd.recomQuery1.put(rs.getString(1), rs.getString(2));
								} else if (rs.getString(3).equals("2")) {
									vd.recomQuery2.put(rs.getString(1), rs.getString(2));
								}
							}
						}
					} catch (SQLException ex) {
						Logger.getLogger(VisExtract.class.getName()).log(Level.SEVERE, null, ex);
					}

					this.recommendationList_bruteForce.add(vd);
				}
			}
		}

		
		this.euclidean_populate(this.recommendationList_bruteForce);	
		this.rank(this.recommendationList_bruteForce);
		return this.recommendationList_bruteForce;
	}

	public ArrayList<VisData> getRecommendation_optimized() {

		String query = "";
		int countCaseConditionPlace = this.dimentionAttributeArray.size()
				+ (this.aggregateArray.size() * this.MeasureAttributeArray.size()) + 1;
		int countColumns = 0;

		ArrayList<String> status = new ArrayList<String>();
		ArrayList<String> row;

		status.add("");
		query = concat(query, "SELECT ");

		for (int i = 0; i < this.dimentionAttributeArray.size(); i++) {
			query = concat(query, this.dimentionAttributeArray.get(i), ",");
			status.add(concat("D-", this.dimentionAttributeArray.get(i)));
			for (int j = 0; j < this.aggregateArray.size(); j++) {
				for (int k = 0; k < this.MeasureAttributeArray.size(); k++) {
					vd = new VisData();
					vd.xAxisTitle = this.dimentionAttributeArray.get(i);
					vd.yAxisTitle = this.aggregateArray.get(j) + "(" + this.MeasureAttributeArray.get(k) + ")";
					this.recommendationList_optimized.add(vd);
					if (i == this.dimentionAttributeArray.size() - 1) {
						query = concat(query, this.aggregateArray.get(j), "(", this.MeasureAttributeArray.get(k), ")",
								",");
						status.add(
								concat("A-", this.aggregateArray.get(j), "(", this.MeasureAttributeArray.get(k), ")"));
					}
				}
			}
		}

		query = concat(query, "CASE WHEN ", this.SpecificAttribute + this.firstOperator, this.firstSelector, " THEN 1",
				" WHEN ", this.SpecificAttribute, this.secondOperator, this.secondSelector, " THEN 2 END as g1",
				" FROM ", this.tableName, " GROUP BY ");

		status.add(concat("C-", "coniditon"));

		for (int i = 0; i < this.dimentionAttributeArray.size(); i++) {
			query = concat(query, this.dimentionAttributeArray.get(i), ",");
		}

		query = concat(query, "g1");

		/*
		 * e.g: rs = sql.
		 * executeQuery("SELECT state,race,SUM(Charge),COUNT(Charge),CASE WHEN gender='M' THEN 1 WHEN gender='F' THEN 2 END as g1 "
		 * + "FROM Traffic_Violations " + "GROUP BY state,race,g1" );
		 */
		rs = sql.executeQuery(query);

		try {
			while (rs.next()) {

				row = new ArrayList<String>();

				if (rs.getString(countCaseConditionPlace) != null) {

					countColumns = 0;

					while (countColumns < countCaseConditionPlace) {

						countColumns++;
						row.add(concat(status.get(countColumns), "-", rs.getString(countColumns)));
					}
				}

				for (int i = 0; i < row.size(); i++) {

					String xAxisTag, yAxisTag, xValue, yValue;

					String[] rowSplitArray = row.get(i).split("-");

					if (rowSplitArray[0].equals("D")) {

						xAxisTag = rowSplitArray[1];
						xValue = rowSplitArray[2];

						for (int j = i; j < row.size(); j++) {

							String[] rowSplitArray2 = row.get(j).split("-");

							if (rowSplitArray2[0].equals("D")) {
								continue;
							} else if (rowSplitArray2[0].equals("A")) {

								yAxisTag = rowSplitArray2[1];
								yValue = rowSplitArray2[2];

								for (int k = 0; k < this.recommendationList_optimized.size(); k++) {
									if (this.recommendationList_optimized.get(k).xAxisTitle.equals(xAxisTag)
											&& this.recommendationList_optimized.get(k).yAxisTitle.equals(yAxisTag)) {

										if (row.get(countCaseConditionPlace - 1).split("-")[2].equals("1")) {
											String returnedValue = (this.recommendationList_optimized.get(k).recomQuery1
													.get(xValue));
											if (returnedValue == null) {
												this.recommendationList_optimized.get(k).recomQuery1.put(xValue,
														yValue);
											} else {
												String newValue = String.valueOf(Double
														.valueOf(this.recommendationList_optimized.get(k).recomQuery1
																.get(xValue))
														+ Double.valueOf(yValue));
												this.recommendationList_optimized.get(k).recomQuery1.put(xValue,
														newValue);
											}

										} else {
											String returnedValue = (this.recommendationList_optimized.get(k).recomQuery2
													.get(xValue));
											if (returnedValue == null) {
												this.recommendationList_optimized.get(k).recomQuery2.put(xValue,
														yValue);
											} else {
												String newValue = String.valueOf(Double
														.valueOf(this.recommendationList_optimized.get(k).recomQuery2
																.get(xValue))
														+ Double.valueOf(yValue));
												this.recommendationList_optimized.get(k).recomQuery2.put(xValue,
														newValue);
											}
										}
									} else {
										continue;
									}
								}
							}
						}
					} else {
						break;
					}
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(VisExtract.class.getName()).log(Level.SEVERE, null, ex);
		}

		this.euclidean_populate(this.recommendationList_optimized);
		this.rank(this.recommendationList_optimized);
		return this.recommendationList_optimized;

	}

		
		// normalize the scale and compute the euclidean metric + 
		// update the "value" array list in each visualization
		private void euclidean_populate(ArrayList<VisData> myList) {

			/*
			 * Euclidean Distance d(p,q)=sqrt( (p1-q1)^2 + (p2-q2)^2 + (p2-q2)^2 
			 */
			
			double sum=0;
			int countAttributes = 0;
			String temp,firstQueryKey, secondQueryValueString;
			double firstQueryValue, secondQueryValue,pValue, qValue;
			ArrayList arrayOfValues = new ArrayList();
			
			/* populate */
			VisObject vb;
			
			for (int i = 0; i < myList.size(); i++) {
				sum = 0;
				countAttributes = 0;
				for (Map.Entry iterator : myList.get(i).recomQuery1.entrySet()) {

					vb = new VisObject();
					firstQueryKey = (String) iterator.getKey();
					vb.xAxis = firstQueryKey;
					vb.yAxis = (String) iterator.getValue();
					vb.queryNumber = 1;
					myList.get(i).values.add(vb);
					firstQueryValue = Double.valueOf((String) iterator.getValue());
					secondQueryValueString = myList.get(i).recomQuery2.get(firstQueryKey);

					vb = new VisObject();

					if (secondQueryValueString == null) {
						vb.xAxis = firstQueryKey;
						vb.yAxis = "0";
						vb.queryNumber = 2;
						myList.get(i).values.add(vb);

						secondQueryValue = 0;

					} else {
						vb.xAxis = firstQueryKey;
						vb.yAxis = secondQueryValueString;
						vb.queryNumber = 2;
						myList.get(i).values.add(vb);

						secondQueryValue = Double.valueOf(secondQueryValueString);

					}
					sum = firstQueryValue + secondQueryValue;
					if (sum == 0)
						continue;
					temp = String.valueOf((firstQueryValue / sum));
					pValue=Double.valueOf(temp);
					
					if (secondQueryValueString != null) {
						temp = String.valueOf((secondQueryValue / sum));
						qValue=Double.valueOf(temp);
					}
					else{
						qValue=0;
					}
					
					myList.get(i).totalDeviation += Math.pow(pValue - qValue, 2);
					arrayOfValues.add(firstQueryKey);
				}

				countAttributes = myList.get(i).recomQuery1.size();
				
				for (Map.Entry iterator : myList.get(i).recomQuery2.entrySet()) {
					if (arrayOfValues.contains((String) iterator.getKey())) {
						continue;
					}
					vb = new VisObject();
					vb.xAxis = (String) iterator.getKey();
					vb.yAxis = (String) iterator.getValue();
					vb.queryNumber = 2;
					myList.get(i).values.add(vb);

					vb = new VisObject();
					vb.xAxis = (String) iterator.getKey();
					vb.yAxis = "0";
					vb.queryNumber = 1;
					myList.get(i).values.add(vb);

					secondQueryValue = Double.valueOf((String) iterator.getValue());
					sum = secondQueryValue;
					if (sum == 0)
						continue;
					temp = String.valueOf((secondQueryValue / sum));
					myList.get(i).totalDeviation += Math.pow(Double.valueOf(temp), 2);
					countAttributes++;
				}
				
				myList.get(i).totalDeviation = Math.sqrt(myList.get(i).totalDeviation);
				myList.get(i).totalDeviation = myList.get(i).totalDeviation / countAttributes;
			}
		}
			
						
	// rank visualizations according to Deviations
	private void rank(ArrayList<VisData> myList) {
		Collections.sort(myList);
	}

	
/*
	// normalize the scale: get the probablity of each aggregate
	private void normalization(ArrayList<VisData> myList) {

		double sum;
		String temp;
		String firstQueryKey, secondQueryValueString;
		double firstQueryValue, secondQueryValue;
		ArrayList arrayOfValues = new ArrayList();

		for (int i = 0; i < myList.size(); i++) {
			sum = 0;
			for (Map.Entry iterator : myList.get(i).recomQuery1.entrySet()) {
				firstQueryKey = (String) iterator.getKey();
				firstQueryValue = Double.valueOf((String) iterator.getValue());
				secondQueryValueString = myList.get(i).recomQuery2.get(firstQueryKey);
				if (secondQueryValueString == null) {
					secondQueryValue = 0;
				} else {
					secondQueryValue = Double.valueOf(secondQueryValueString);
				}
				sum = firstQueryValue + secondQueryValue;
				if (sum == 0)
					continue;
				temp = String.valueOf((firstQueryValue / sum));
				myList.get(i).recomQuery1_normalized.put(firstQueryKey, temp);
				if (secondQueryValueString != null) {
					temp = String.valueOf((secondQueryValue / sum));
					myList.get(i).recomQuery2_normalized.put(firstQueryKey, temp);
				}

				arrayOfValues.add(firstQueryKey);
			}

			for (Map.Entry iterator : myList.get(i).recomQuery2.entrySet()) {
				if (arrayOfValues.contains((String) iterator.getKey())) {
					continue;
				}
				secondQueryValue = Double.valueOf((String) iterator.getValue());
				sum = secondQueryValue;
				temp = String.valueOf((secondQueryValue / sum));
				myList.get(i).recomQuery2_normalized.put((String) iterator.getKey(), temp);
			}
		}
	}


		// update the total_devation entry in each object
		private void euclidean_metric(ArrayList<VisData> myList) {
			//
			 // Euclidean Distance d(p,q)=sqrt( (p1-q1)^2 + (p2-q2)^2 + (p2-q2)^2 +
			 //
			ArrayList arrayOfValues = new ArrayList();
			String pKey, qValueString;
			double pValue, qValue;
			int countAttributes = 0;
			for (int i = 0; i < myList.size(); i++) {
				countAttributes = 0;
				for (Map.Entry iterator : myList.get(i).recomQuery2_normalized.entrySet()) {
					pKey = (String) iterator.getKey();
					pValue = Double.valueOf((String) iterator.getValue());
					qValueString = myList.get(i).recomQuery1_normalized.get(pKey);

					if (qValueString == null) {
						qValue = 0;
					} else {
						qValue = Double.valueOf(qValueString);
					}
					myList.get(i).totalDeviation += Math.pow(pValue - qValue, 2);
					arrayOfValues.add(pKey);
				}
				countAttributes = myList.get(i).recomQuery2_normalized.size();
				for (Map.Entry iterator : myList.get(i).recomQuery1_normalized.entrySet()) {
					if (arrayOfValues.contains((String) iterator.getKey())) {
						continue;
					}
					myList.get(i).totalDeviation += Math.pow(Double.valueOf((String) iterator.getValue()), 2);
					countAttributes++;
				}
				myList.get(i).totalDeviation = Math.sqrt(myList.get(i).totalDeviation);
				myList.get(i).totalDeviation = myList.get(i).totalDeviation / countAttributes;
			}
		}

		
		private void manhattan_metric(ArrayList<VisData> myList) {
			//
			 // manhattan Distance d(p,q)=abs (p1-q1) + abs(p2-q2) + abs(p3-q3)
			 //
			 //

			ArrayList<String> arrayOfValues = new ArrayList<String>();
			String pKey, qValueString;
			double pValue, qValue;

			for (int i = 0; i < myList.size(); i++) {

				for (Map.Entry iterator : myList.get(i).recomQuery2.entrySet()) {
					pKey = (String) iterator.getKey();
					pValue = Double.valueOf((String) iterator.getValue());
					qValueString = myList.get(i).recomQuery1.get(pKey);

					if (qValueString == null) {
						qValue = 0;
					} else {
						qValue = Double.valueOf(qValueString);
					}
					myList.get(i).totalDeviation += Math.abs(qValue - pValue);
					arrayOfValues.add(pKey);
				}

				for (Map.Entry iterator : myList.get(i).recomQuery1.entrySet()) {
					if (arrayOfValues.contains((String) iterator.getKey())) {
						continue;
					}
					myList.get(i).totalDeviation += Math.abs(Double.valueOf((String) iterator.getValue()));
				}
			}
		}


	// update value array list in each visualization
	private void populateData(ArrayList<VisData> myList) {

		ArrayList<String> arrayOfValues = new ArrayList<String>();
		String firstKey, secondKey, firstValue, secondValue;
		VisObject vb;

		for (int i = 0; i < myList.size(); i++) {

			for (Map.Entry iterator : myList.get(i).recomQuery1.entrySet()) {
				vb = new VisObject();
				firstKey = (String) iterator.getKey();
				vb.xAxis = firstKey;
				firstValue = (String) iterator.getValue();
				vb.yAxis = firstValue;
				vb.queryNumber = 1;
				myList.get(i).values.add(vb);

				secondValue = myList.get(i).recomQuery2.get(firstKey);
				vb = new VisObject();

				if (secondValue == null) {
					vb.xAxis = firstKey;
					vb.yAxis = "0";
					vb.queryNumber = 2;
					myList.get(i).values.add(vb);

				} else {
					vb.xAxis = firstKey;
					vb.yAxis = secondValue;
					vb.queryNumber = 2;
					myList.get(i).values.add(vb);
				}

				arrayOfValues.add(firstKey);
			}

			for (Map.Entry iterator : myList.get(i).recomQuery2.entrySet()) {
				if (arrayOfValues.contains((String) iterator.getKey())) {
					continue;
				} else {
					vb = new VisObject();
					secondKey = (String) iterator.getKey();
					vb.xAxis = secondKey;
					secondValue = (String) iterator.getValue();
					vb.yAxis = secondValue;
					vb.queryNumber = 2;
					myList.get(i).values.add(vb);

					firstValue = myList.get(i).recomQuery1.get(secondKey);
					vb = new VisObject();

					if (firstValue == null) {
						vb.xAxis = secondKey;
						vb.yAxis = "0";
						vb.queryNumber = 1;
						myList.get(i).values.add(vb);

					} else {
						vb.xAxis = secondKey;
						vb.yAxis = firstValue;
						vb.queryNumber = 1;
						myList.get(i).values.add(vb);
					}
				}
			}
		}
	}
*/



}
