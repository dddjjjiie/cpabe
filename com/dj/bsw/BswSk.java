package com.dj.bsw;

import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Element;

public class BswSk {
	public Element D; //D=g2^((a+r)/b)
	public HashMap<String, Element[]> Djs = new HashMap<>(); //attr -> (Dj, Dj')
	
	/**
	 * Djs whether contains attribute attr
	 * @param attr, attribute
	 * @return boolean
	 */
	public boolean contains(String attr) {
		return Djs.containsKey(attr);
	}
}
