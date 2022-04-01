package com.dj.bsw;

import java.io.ByteArrayInputStream;

import it.unisa.dia.gas.jpbc.CurveParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.DefaultCurveParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class BswPub {
	public Element g1;						//generator of G1
	public Element g2;						//generator of G2
	public Element h;						//g1^b
	public Element f;						//g1^(1/b)
	public Element g_hat_alpha;				//e(g1, g2)^a
	public static Pairing pair;					//G0, G1, GT
	
	
	private static String curveParams = "type a\n"
			+ "q 87807107996633125224377819847540498158068831994142082"
			+ "1102865339926647563088022295707862517942266222142315585"
			+ "8769582317459277713367317481324925129998224791\n"
			+ "h 12016012264891146079388821366740534204802954401251311"
			+ "822919615131047207289359704531102844802183906537786776\n"
			+ "r 730750818665451621361119245571504901405976559617\n"
			+ "exp2 159\n" + "exp1 107\n" + "sign1 1\n" + "sign0 1\n"; //the parameters of elliptic curve, don't care
	
	static {
		CurveParameters parms = new DefaultCurveParameters().load(new ByteArrayInputStream(curveParams.getBytes()));
		pair = PairingFactory.getPairing(parms); //generate G1, G1 and Gt
	}
	
	public BswPub(){
		g1 = pair.getG1().newElement();
		g2 = pair.getG2().newElement();
		h = pair.getG1().newElement();
		f = pair.getG1().newElement();
		g_hat_alpha = pair.getGT().newElement();
	}
	
	public String toString() {
		return "{g1:" + g1 + "\ng2:" + g2 + "\nh:" + h + "\nf:" + f + "\ng_hat_alpha:" + g_hat_alpha + "}";
	}
}
