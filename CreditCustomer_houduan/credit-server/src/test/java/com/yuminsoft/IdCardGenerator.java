package com.yuminsoft;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
/** 
 * 
 * 身份证算法实现
 * 
* 1、号码的结构 公民身份号码是特征组合码，
*     由十七位数字本体码和一位校验码组成。
* 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码  三位数字顺序码和一位数字校验码。 
* 
* 2、地址码(前六位数） 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。 
* 
* 3、出生日期码（第七位至十四位） 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。 
* 
* 4、顺序码（第十五位至十七位） 
*    表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配给女性。 
* 
* 5、校验码（第十八位数） 
*   （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 
* ，先对前17位数字的权求和
*  Ai:表示第i位置上的身份证号码数字值
*   Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2 
* （2）计算模 Y = mod(S, 11) 
* （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7  8 9 10 
*   校验码: 1 0 X 9 8 7 6 5 4 3 2 
* 
* 
 * 
 * @author longgangbai
 *
 */
public class IdCardGenerator {

	/** 
     * 获取随机生成的身份证号码 
     *  
     * @author mingzijian 
     * @return 
     */  
    public String getRandomID() {  
        String id = "420222199204179999";  
        // 随机生成省、自治区、直辖市代码 1-2  
        String provinces[] = { "11", "12", "13", "14", "15", "21", "22", "23",  
                "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",  
                "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",  
                "63", "64", "65", "71", "81", "82" };  
        String province = randomOne(provinces);  
        // 随机生成地级市、盟、自治州代码 3-4  
        String city = randomCityCode(18);  
        // 随机生成县、县级市、区代码 5-6  
        String county = randomCityCode(28);  
        // 随机生成出生年月 7-14  
        String birth = randomBirth(20, 50);  
        // 随机生成顺序号 15-17(随机性别)  
        String no = new Random().nextInt(899) + 100+"";    
        // 随机生成校验码 18  
        String checks[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",  
                "X" };  
        String check = randomOne(checks);  
        // 拼接身份证号码  
        id = province + city + county + birth + no + check;  
        return id;  
    }
    
    /** 
     * 从String[] 数组中随机取出其中一个String字符串 
     *  
     * @author mingzijian 
     * @param s 
     * @return 
     */  
    public String randomOne(String s[]) {  
        return s[new Random().nextInt(s.length - 1)];  
    }  
  
    /** 
     * 随机生成两位数的字符串（01-max）,不足两位的前面补0 
     *  
     * @author mingzijian 
     * @param max 
     * @return 
     */  
    public String randomCityCode(int max) {  
        int i = new Random().nextInt(max) + 1;  
        return i > 9 ? i + "" : "0" + i;  
    }  
  
    /** 
     * 随机生成minAge到maxAge年龄段的人的生日日期 
     *  
     * @author mingzijian 
     * @param minAge 
     * @param maxAge 
     * @return 
     */  
    public String randomBirth(int minAge, int maxAge) {  
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");// 设置日期格式  
        Calendar date = Calendar.getInstance();  
        date.setTime(new Date());// 设置当前日期  
        // 随机设置日期为前maxAge年到前minAge年的任意一天  
        int randomDay = 365 * minAge  
                + new Random().nextInt(365 * (maxAge - minAge));  
        date.set(Calendar.DATE, date.get(Calendar.DATE) - randomDay);  
        return dft.format(date.getTime());  
    } 
}