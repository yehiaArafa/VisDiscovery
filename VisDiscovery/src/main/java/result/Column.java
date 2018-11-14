/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package result;

/**
 *
 * @author nada
 */
public class Column {
    String field;
    String type;
    String aggreggate;
    Scale scale;
    Axis axis;
    
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
	public String getAggreggate() {
		return aggreggate;
	}
	public void setAggreggate(String aggreggate) {
		this.aggreggate = aggreggate;
	}
	public Scale getScale() {
		return scale;
	}
	public void setScale(Scale scale) {
		this.scale = scale;
	}
	public Axis getAxis() {
		return axis;
	}
	public void setAxis(Axis axis) {
		this.axis = axis;
	}
}
