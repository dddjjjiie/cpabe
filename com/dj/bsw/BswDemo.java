package com.dj.bsw;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

public class BswDemo {
	public static void main(String[] args) throws Exception{
//		BswDemo.testPolynomial();
		BswDemo.testAlgorithm();
	}
	
	static void testAlgorithm() throws Exception{
		String policy = "计科 硕士 研二 3of3 教师 网 云 1of2 2of3";
		String attr = "计科 硕士 研二";
		Element m = BswPub.pair.getGT().newElement().setToRandom();
		
		BswPub pub = new BswPub();
		BswMsk msk = new BswMsk();
		BswAccessTree accessTree = BswAccessTree.getInstance(policy);
		System.out.println("access tree:\n" + accessTree);
		
		Bsw.setup(pub, msk);
		
		System.out.println("encrypt message:" + m + "\n");
		BswCipherText cipherText = Bsw.encrypt(pub, m, accessTree);
		System.out.println("generate polynomial:\n" + accessTree.toString() + "\n");
		
		BswSk sk = Bsw.KeyGen(pub, msk, attr);
		
		try {
			Element decryptM = Bsw.decrypt(cipherText, sk);
			System.out.println("decrypt message:" + decryptM + "\n");
			System.out.println("is equal? " + m.isEqual(decryptM) + "\n");
			System.out.println("restrict(accessTree, M):\n" + accessTree.restrictToString());
		}catch(IllegalArgumentException e) {
			System.err.println("attribute s not satify the policy, decrypt error!");
		}
	}
	
	static void testPolynomial() {
		BswNode[] nodes = new BswNode[9];
		nodes[0] = new BswNode(2, 3);
		nodes[0].polynomial.setCoefficient(new int[] {5, 3});
		
		nodes[1] = new BswNode(3, 3);
		nodes[1].polynomial.setCoefficient(new int[] {8, 4, 7});
		
		nodes[2] = new BswNode("教师");
		nodes[2].polynomial.setCoefficient(new int[] {11});
		
		nodes[3] = new BswNode(1, 2);
		nodes[3].polynomial.setCoefficient(new int[] {14});
		
		nodes[4] = new BswNode("计科");
		nodes[4].polynomial.setCoefficient(new int[] {19});
		
		nodes[5] = new BswNode("硕士");
		nodes[5].polynomial.setCoefficient(new int[] {44});
		
		nodes[6] = new BswNode("研二");
		nodes[6].polynomial.setCoefficient(new int[] {83});
		
		nodes[7] = new BswNode("网");
		nodes[7].polynomial.setCoefficient(new int[] {14});
		
		nodes[8] = new BswNode("云");
		nodes[8].polynomial.setCoefficient(new int[] {14});
		
		nodes[0].children[0] = nodes[1]; nodes[0].children[1] = nodes[2]; nodes[0].children[2] = nodes[3];
		nodes[1].children[0] = nodes[4]; nodes[1].children[1] = nodes[5]; nodes[1].children[2] = nodes[6];
		nodes[3].children[0] = nodes[7]; nodes[3].children[1] = nodes[8];
		BswAccessTree tree = new BswAccessTree();
		tree.root = nodes[0];
		System.out.println(tree);
		
		BswSk sk = new BswSk();
		String[] attrs = new String[] {"计科", "硕士", "研二", "网", "云"};
		for(int i=0; i<attrs.length; i++) {
			sk.Djs.put(attrs[i], new Element[] {BswPub.pair.getG1().newElement(), BswPub.pair.getG1().newElement()});
		}
		
		Bsw.restrict(tree.root, sk);
		System.out.println(tree.restrictToString());
		
//		Element c1 = Bsw.lagrangeCoef(nodes[0], 0);
//		Element c2 = Bsw.lagrangeCoef(nodes[0], 1);
//		c1.mul(14);
//		c2.mul(8);
//		c1.add(c2);
//		System.out.println("c1:" + c1 + " c2:" + c2);
		
		String attr = "计科 硕士 研二 网 云";
		Element m = BswPub.pair.getGT().newElement().setToRandom();
		m.setToOne();
		
		BswPub pub = new BswPub();
		BswMsk msk = new BswMsk();
		
		Bsw.setup(pub, msk);
		System.out.println("pub:\n" + pub + "\nmsk:" + msk);
		System.out.println("encrypt message:" + m);
		
//		BswCipherText cipherText = Bsw.encrypt(pub, m, accessTree);
//		System.out.println("generate access tree:\n" + accessTree.toString());
//		System.out.println("cs in CT:" + cipherText.Cs);
//		
//		BswSk sk = Bsw.KeyGen(pub, msk, attr);
//		
//		Element decryptM = Bsw.decrypt(cipherText, sk);
//		
//		System.out.println("decrypt message:" + decryptM);
//		System.out.println("is equal? " + m.isEqual(decryptM));
//		System.out.println(accessTree.restrictToString());
	}
}
