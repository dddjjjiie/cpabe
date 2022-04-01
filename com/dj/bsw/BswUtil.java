package com.dj.bsw;

import java.security.MessageDigest;

import it.unisa.dia.gas.jpbc.Element;

public class BswUtil {
	public static Element attrToG2(String att){
		try {
			Element res = BswPub.pair.getG2().newElement();
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(att.getBytes());
			res.setFromHash(digest, 0, digest.length);
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
}
