/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package result;

import java.io.Serializable;
import java.util.ArrayList;

import recommendation.VisData;

/**
 *
 * @author omarkrostom
 */
public class Url implements Serializable {
	ArrayList<VisData> url;

	public ArrayList<VisData> getUrl() {
		return url;
	}

	public void setUrl(ArrayList<VisData> url) {
		this.url = url;
	}
    
}
