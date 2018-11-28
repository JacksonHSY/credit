package com.ymkj.credit.common.untils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSONUtils
 * <p/>
 * Author: Hao Chen
 * Date: 2016-04-06 13:49
 * Mail: haoc@zendaimoney.com
 */
public class JSONUtils {

    /**
     * 默认的处理时间
     *
     * @param obj 需要转换的对象
     * @return 转换后的json字符串
     */
    public static String toJSON(Object obj) {
        return JSON.toJSONString(obj //, new DefaultNumberFilter()
                ,SerializerFeature.WriteDateUseDateFormat
                ,SerializerFeature.WriteMapNullValue
                ,SerializerFeature.WriteNullStringAsEmpty
                ,SerializerFeature.WriteNullBooleanAsFalse
                ,SerializerFeature.WriteNullListAsEmpty
                ,SerializerFeature.WriteNullNumberAsZero
        );
    }

}