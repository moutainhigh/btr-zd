package com.baturu.btrzd;

import com.alibaba.druid.filter.config.ConfigTools;

public class ConfigToolsTest {

	public static void main(String[] args) throws Exception {
		String password = "immfBjshmJJed80";
        String[] arr = ConfigTools.genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + ConfigTools.encrypt(arr[0], password));
	}

}
