/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package result;

/**
 *
 * @author omarkrostom
 */
public class Axis {
    String title;
    Boolean grid;
    Integer offset;
	Integer axisWidth;
    String orient;
    Integer labelAngle;
    Integer labelMaxLength;
    
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getGrid() {
		return grid;
	}
	public void setGrid(Boolean grid) {
		this.grid = grid;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getAxisWidth() {
		return axisWidth;
	}
	public void setAxisWidth(Integer axisWidth) {
		this.axisWidth = axisWidth;
	}
	public String getOrient() {
		return orient;
	}
	public void setOrient(String orient) {
		this.orient = orient;
	}
	public Integer getLabelAngle(){
		return this.labelAngle;
	}
	public void setLabelAngle(Integer labelAngle){
		this.labelAngle=labelAngle;
	}
	public Integer getLabelMaxLength(){
		return this.labelMaxLength;
	}
	public void setLabelMaxLength(Integer labelMaxLength){
		this.labelMaxLength=labelMaxLength;
	}
	
}
