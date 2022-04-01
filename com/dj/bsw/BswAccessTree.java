package com.dj.bsw;

import java.util.LinkedList;
import java.util.Stack;

import it.unisa.dia.gas.jpbc.Element;

public class BswAccessTree {
	public static BswNode root; // the root node of access tree
	
	public static BswAccessTree getInstance(String policy) throws Exception{
		BswAccessTree accessTree = new BswAccessTree();
		accessTree.root = parsePolicyPostfix(policy);
		return accessTree;
	}
	/**
	 * convert policy into a access tree
	 * @param policy, string of policy which uses space to separate
	 * @return the root node of access tree
	 */
	public static BswNode parsePolicyPostfix(String policy) {
		String[] attrs = policy.split(" ");
		Stack<BswNode> stack = new Stack<>();
		for(int i=0; i<attrs.length; i++) {
			if(attrs[i].contains("of")) {
				String[] k_n = attrs[i].split("of");
				int k = Integer.valueOf(k_n[0]), n = Integer.valueOf(k_n[1]);
				//the minimum value of k is 1, a internal node must have 2 nodes, and k must less equal n  
				if(k < 1 || k > n || n <= 1|| n > stack.size()) throw
													new IllegalArgumentException("the value of k and n is error: k:" + k + " n:" + n + " stack.size:" + stack.size());
				BswNode internalNode = new BswNode(k, n);
				for(int j=n-1; j>=0; j--) {
					internalNode.children[j] = stack.pop();
				}
				stack.push(internalNode);
			}else {
				BswNode leafNode = new BswNode(attrs[i]);
				stack.push(leafNode);
			}
		}
		if(stack.size() == 1) return stack.pop();
		else throw new IllegalArgumentException("policy is wrong, exists more than one root!");
	}
	
	/**
	 * generate polynomials for all nodes in access tree
	 */
	public static void generatePolynomial() {
		//generate s
		Element s = BswPub.pair.getZr().newElement();
		s.setToRandom();
		generatePolynomialForEachNode(s, root);
	}
	
	public static void generatePolynomialForEachNode(Element constantTerm, BswNode node) {
		node.polynomial.coefficient[0] = constantTerm;
		for(int i=1; i<node.k; i++) {
			node.polynomial.coefficient[i].setToRandom();
		}
		for(int i=0; i<node.n; i++) {
			generatePolynomialForEachNode(node.polynomial.calculate(i+1), node.children[i]);
		}
	}
	
	
	public String toString() {
		 StringBuilder sb = new StringBuilder();
		 LinkedList<BswNode> queue = new LinkedList<>();
		 queue.offer(root);
		 while(!queue.isEmpty()) {
			 int size = queue.size();
			 for(int j=0; j<size; j++) {
				 BswNode node = queue.poll();
				 for(int i=0; i<node.n; i++) {
					 queue.offer(node.children[i]);
				 }
				 sb.append(node + " ");
			 }
			 sb.append("\n");
		 }
		 return sb.toString();
	}
	
	public String restrictToString() {
		StringBuilder sb = new StringBuilder();
		 LinkedList<BswNode> queue = new LinkedList<>();
		 queue.offer(root);
		 while(!queue.isEmpty()) {
			 int size = queue.size();
			 for(int j=0; j<size; j++) {
				 BswNode node = queue.poll();
				 for(int i=0; node.n !=0 && i<node.k; i++) {
					 queue.offer(node.children[node.minChildrenIndex.get(i)]);
				 }
				 sb.append(node + " ");
			 }
			 sb.append("\n");
		 }
		 return sb.toString();
	}
}
