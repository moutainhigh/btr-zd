package com.baturu.zd.util;

import java.awt.*;
import java.sql.SQLOutput;

/**
 * created by ketao by 2019/03/22
 **/
public class ZDStringUtil {


    /**
     * 返回至少多少位的编号,不足补0
     * @param num
     * @param bit
     * @return
     */
    public static String getNextFullZero(int num,int bit){
        StringBuilder result=new StringBuilder();
        String temp=(num+1)+"";
        if(temp.length()>bit){
            return temp;
        }
        for(int i=0;i<(bit-temp.length());i++){
            result.append("0");
        }
        return result.append(temp).toString();
    }

    public static void main(String[] arg){
        System.out.println(getNextFullZero(1,3));
    }
}
