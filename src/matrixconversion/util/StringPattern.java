/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixconversion.util;

import java.util.regex.Pattern;

/**
 *
 * @author jingliu5
 */
public class StringPattern {
    
    /* 
  * 判断是否为浮点数，包括double和float 
  * @param str 传入的字符串 
  * @return 是浮点数返回true,否则返回false 
*/ 
  public static boolean isDouble(String str) {  
    Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");  
    return pattern.matcher(str).matches();  
  }
  
  /*
  * 判断是否为整数 
  * @param str 传入的字符串 
  * @return 是整数返回true,否则返回false 
*/ 
  public static boolean isInteger(String str) {  
    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
    return pattern.matcher(str).matches();  
  }
  
    public static boolean isENum(String str) {//判断输入字符串是否为科学计数法
        String regx = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";//科学计数法正则表达式
        Pattern pattern = Pattern.compile(regx);
        return pattern.matcher(str).matches();
    }
}
