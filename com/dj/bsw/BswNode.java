package com.dj.bsw;

import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;

public class BswNode {
	int k; //threshold, if it's a leaf node, k=1
	int n; //the number of its children
	String attr; //attribute of leaf node
	BswNode[] children; //children of internal node
	BswPolynomial polynomial; // the polynomial of each node
	
	/**used to restrict the access tree*/
	ArrayList<Integer> minChildrenIndex = new ArrayList<>(); // the minimum children's index of internal node that satisfy k
	int minLeaf; //the number of leaf nodes with this node as the root node tree
	
	public BswNode(String _attr) { //the constructor of lead node
		k = 1;
		n = 0;
		attr = _attr;
		polynomial = new BswPolynomial(k);
	}
	
	public BswNode(int _k, int _n) { //the constructor of internal node
		k = _k;
		n = _n;
		children = new BswNode[n];
		polynomial = new BswPolynomial(k);
	}
	
	public Element getCoefficient(int idx) {
		return polynomial.coefficient[idx];
	}
	
	public String toString() {
		if(n == 0) return attr + "(" + polynomial + ")";
		else return "" + k + "/" + n + "(" + polynomial + ")";
	}
}
