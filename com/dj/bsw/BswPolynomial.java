package com.dj.bsw;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class BswPolynomial {
	int degree; //degree of the polynomial
	Element[] coefficient; //the i'th element is the coefficientficient of x^i
	
	public BswPolynomial(int _degree) {
		degree = _degree;
		coefficient = new Element[degree];
		for(int i=0; i<degree; i++) coefficient[i] = BswPub.pair.getZr().newElement();
	}
	
	public void setCoefficient(int[] num) {
		for(int i=0; i<degree; i++) {
			coefficient[i].set(num[i]);
		}
	}
	
	/**
	 * calculate f(x)
	 * @param x
	 * @return the result of f(x)
	 */
	public Element calculate(int value) {
		Element x = BswPub.pair.getZr().newElement();
		x.set(value);
		Element res = coefficient[0].duplicate();
		Element xx = x.duplicate();
		for(int i=1; i<degree; i++) {
			res.add(x.duplicate().mul(coefficient[i]));
			x.mul(xx);
		}
		return res;
	}
	
	public String toString() {
		if(coefficient == null) return "";
		StringBuffer sb = new StringBuffer("f(x)=");
		for(int i=0; i<coefficient.length; i++) {
			if(i == 0) sb.append(coefficient[i]);
			else sb.append("+" + coefficient[i] + "x^" + i);
		}
		return sb.toString();
	}
}
