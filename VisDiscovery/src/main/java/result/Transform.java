
package result;

import java.io.Serializable;

/**
 *
 * @author omarkrostom
 */
public class Transform implements Serializable {
    //String filter;
    Tuple[] calculate;
	/*public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}*/
	public Tuple[] getCalculate() {
		return calculate;
	}
	public void setCalculate(Tuple[] calculate) {
		this.calculate = calculate;
	}
}
