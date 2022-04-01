package com.dj.bsw;

import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Element;

public class BswCipherText {
	public BswAccessTree accessTree; //access tree
	public Element C; //C=h^s
	public Element Cs; //Cs = Me(g1, g2)^(as)
	public HashMap<String, Element[]> Cys = new HashMap<>(); //(leafNode->(Cy, Cy')), map leafNode to (Cy, Cy') which Cy is g^(gy(0)) and Cy' is H(att(y))^(qy(0))
	
	public BswCipherText() {
	}
	
	/**
	 * //travel the access tree
	 * @param cipherText, CT
	 * @param root, the root node of access tree
	 * @param pub, public key
	 * @throws Exception, no such algorithm
	 */
	void generateMap(BswCipherText cipherText, BswNode root, BswPub pub) throws Exception{
		if(root.n == 0) {
			Element c1y = pub.g1.duplicate(), c2y = BswUtil.attrToG2(root.attr);
			c1y.powZn(root.getCoefficient(0));
			c2y.powZn(root.getCoefficient(0));
			cipherText.Cys.put(root.attr, new Element[] {c1y, c2y});
		}else {
			for(int i=0; i<root.n; i++) {
				generateMap(cipherText, root.children[i], pub);
			}
		}
	}
}
