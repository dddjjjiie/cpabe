package com.dj.bsw;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Comparator;

import it.unisa.dia.gas.jpbc.CurveParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.pairing.DefaultCurveParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Bsw {
	/**
	 * generate parameters of pub and msk
	 * @param pub, public key
	 * @param msk, master key
	 */
	public static void setup(BswPub pub, BswMsk msk) {
		//reset the zl
		res.setToOne();
		
		//init alpha and beta
		Element alpha, beta; //a,b
		alpha = pub.pair.getZr().newElement();
		beta = pub.pair.getZr().newElement();
		alpha.setToRandom();
		beta.setToRandom();
		
		//generate public key
		pub.g1.setToRandom();
		pub.g2.setToRandom();
		pub.h = pub.g1.duplicate();
		pub.h.powZn(beta);
		pub.f = pub.g1.duplicate();
		pub.f.powZn(beta.duplicate().invert());
		
		//generate master key
		msk.beta = beta;
		msk.g2_alpha = pub.g2.duplicate();
		msk.g2_alpha.powZn(alpha);
		
		pub.g_hat_alpha = pub.pair.pairing(pub.g1, msk.g2_alpha);
	}
	
	/**
	 * encrypt message m, first generate polynomial for access tree and calculate  corresponding parameters
	 * @param pub, public key
	 * @param m, message
	 * @param accessTree
	 * @throws Exception, no such algorithm
	 */
	public static BswCipherText encrypt(BswPub pub, Element m, BswAccessTree accessTree) throws Exception{
		BswCipherText cipherText = new BswCipherText();
		accessTree.generatePolynomial();
		Element s = accessTree.root.polynomial.coefficient[0];
		cipherText.accessTree = accessTree;
		cipherText.Cs = m.duplicate();
		cipherText.Cs.mul(pub.g_hat_alpha.duplicate().powZn(s));
		cipherText.C = pub.h.duplicate();
		cipherText.C.powZn(s);
		
		cipherText.generateMap(cipherText, accessTree.root, pub);
		return cipherText;
	}
	
	/**
	 * generate random number to generate sk
	 * @param pub, public key
	 * @param msk, master key
	 * @param attr, attribute
	 */
	public static BswSk KeyGen(BswPub pub, BswMsk msk, String attr) {
		Element r = BswPub.pair.getZr().newElement(); //random number r
		r.setToRandom();
		
		Element betaInvert = msk.beta.duplicate(); //1/b
		betaInvert.invert();
		
		BswSk sk = new BswSk();
		sk.D = msk.g2_alpha.duplicate();
		sk.D.mul(pub.g2.duplicate().powZn(r));
		sk.D.powZn(betaInvert);
		
		String attrs[] = attr.split(" ");
		for(int i=0; i<attrs.length; i++) {
			Element ri = r.duplicate().setToRandom(); //random number ri
			Element D1 = pub.g2.duplicate();
			Element D2 = pub.g1.duplicate();
			D1.powZn(r).mul(BswUtil.attrToG2(attrs[i]).powZn(ri));
			D2.powZn(ri);
			sk.Djs.put(attrs[i], new Element[] {D1, D2});
		}
		return sk;
	}
	
	/**
	 * 
	 * @param cipherText
	 * @param sk
	 */
	public static Element decrypt(BswCipherText cipherText, BswSk sk) {
		Element cs = cipherText.Cs.duplicate();
		Element e_c_d = BswPub.pair.pairing(cipherText.C, sk.D);
		decryptNode(null, cipherText.accessTree.root, BswPub.pair.getZr().newElement().setToOne(), 0, sk, cipherText);
		Element a = res.duplicate();
		Element m = cs.div(e_c_d.div(a));
		return m;
	}
	
	/**
	 * postorder traversal to calculate the minimum number of leaf node the node can possess
	 * @param root, the root node of the tree
	 * @param sk, to judge attr(leftnode) is in S
	 * @return the number of child nodes that satisfy the requirement is greater than or equal to k
	 */
	static boolean restrict(BswNode root, BswSk sk) {
		if(root.n == 0 && sk.contains(root.attr)) {
			root.minLeaf = 1;
			return true;
		}else {
			for(int i=0; i<root.n; i++) {
				if(restrict(root.children[i], sk)) {
					root.minChildrenIndex.add(i);
				}
			}
			if(root.minChildrenIndex.size() < root.k) return false;
			Collections.sort(root.minChildrenIndex, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return root.children[o1].minLeaf - root.children[o2].minLeaf;
				}
			});
			for(int i=0; i<root.k; i++) { //attention: we don't delete the element whose position in node.children is great equal node.k
				root.minLeaf += root.children[root.minChildrenIndex.get(i)].minLeaf;
			}
			return true;
		}
	}
	
	static Element res = BswPub.pair.getGT().newElement(); //intermidiate result of zl
	static {
		res.setToOne();
	}
	
	/**
	 * decrypt the value of leaf node, e(Di, Cl) / e(Di', Cl'), l is leaf node, i is attr(l)
	 * @param leafNode
	 * @param sk
	 * @param cipherText
	 * @return, e(Di, Cl) / e(Di', Cl')
	 */
	static Element decryptLeafNode(BswNode leafNode, BswSk sk, BswCipherText cipherText) {
		Element numerator = BswPub.pair.pairing(sk.Djs.get(leafNode.attr)[0], cipherText.Cys.get(leafNode.attr)[0]);
		Element denominator = BswPub.pair.pairing(sk.Djs.get(leafNode.attr)[1], cipherText.Cys.get(leafNode.attr)[1]);
		numerator.mul(denominator.invert());
		return numerator;
	}
	
	/**
	 * first call restrict function to flattern out the tree, then calculate the secret value of the root
	 * @param parent
	 * @param node
	 * @param lc, the intermediat result of zl
	 * @param idx, the node's position in minChildrenIndex of parent 
	 * @param sk
	 * @param cipherText
	 */
	static void decryptNode(BswNode parent, BswNode node, Element lc, int idx, BswSk sk, BswCipherText cipherText) {
		if(parent == null) { // we should restrict the tree for subsequent calculation.
			if(!restrict(node, sk)) throw new IllegalArgumentException("attribute s not satisfy the policy!");
			for(int i=0; i<node.k; i++) {
				decryptNode(node, node.children[node.minChildrenIndex.get(i)], lc.duplicate(), i, sk, cipherText);
			}
		}else if(node.n != 0) { // for internal node, we need the lagrange coefficient of it.
			Element tmp = lagrangeCoef(parent, idx);
			tmp.mul(lc);
			for(int i=0; i<node.k; i++) {
				decryptNode(node, node.children[node.minChildrenIndex.get(i)], tmp.duplicate(), i, sk, cipherText);
			}
		}else if(node.n == 0) { // for leaf node, we can the final result of zl.
			lc.mul(lagrangeCoef(parent, idx));
			Element leafNodeValue = decryptLeafNode(node, sk, cipherText);
			leafNodeValue.powZn(lc);
			res.mul(leafNodeValue);
		}
	}
	
	/**
	 * get the idx'th child's lagrange coefficient of parent
	 * @param parent, parent node
	 * @param idx, index of parent.minChildren
	 * @return lagrange coefficient
	 */
	static Element lagrangeCoef(BswNode parent, int idx) {
		Element res = BswPub.pair.getZr().newElement();
		res.setToOne();
		for(int j=0; j<parent.k; j++) {
			if(j == idx) continue;
			Element numerator = BswPub.pair.getZr().newElement();
			Element denominator = BswPub.pair.getZr().newElement();
			numerator.set(-(parent.minChildrenIndex.get(j) + 1));
			denominator.set((parent.minChildrenIndex.get(idx) + 1) - (parent.minChildrenIndex.get(j) + 1));
			numerator.mul(denominator.invert());
			res.mul(numerator);
		}
		return res;
	}
}
