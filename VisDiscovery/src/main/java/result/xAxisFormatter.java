
package result;

/**
*
* @author nada
*/

public class xAxisFormatter {
	
	String field;
    String type;
    Scale scale;
    boolean axis;
    
    public boolean getAxis() {
		return axis;
	}
	public void setAxis(boolean axis) {
		this.axis = axis;
	}
    public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Scale getScale() {
		return scale;
	}
	public void setScale(Scale scale) {
		this.scale = scale;
	}

}
