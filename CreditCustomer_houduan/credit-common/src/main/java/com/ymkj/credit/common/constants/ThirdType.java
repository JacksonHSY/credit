package com.ymkj.credit.common.constants;

/**
 * 第三方支付平台编码
 *
 * @author wulj
 */
public enum ThirdType {

    ALLINPAY("0", "通联支付"),
    FUIOUPAY("2", "富友支付"),
    SHUNIONPAY("4", "上海银联支付"),
    YONGYOUUNIONPAY("6", "用友支付"),
    SHUNIONPAY_ACCOUNT_AUTH("8", "上海银联支付-实名认证"),
    ZENDAIPAY("10", "证大爱特支付"),
    CMBPAY("12", "招商银行"),
    KFTPAY("14", "快付通支付"),
    IFREPAY("16", "数信支付"),
    KJTPAY("18", "快捷通支付"),
    BAOFOOPAY("20", "宝付支付"),
    UNSPAY("22", "银生宝支付"),
    ALLINPAY2("100", "通联支付2"),
    BAOFOOPAY2("101", "宝付协议支付"),
    ROUTEPAY("999", "默认路由规则");

    private final String code;
    private final String desc;

    private ThirdType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ThirdType get(String code) {
        for (ThirdType thirdType : ThirdType.values()) {
            if (thirdType.getCode().equals(code)) {
                return thirdType;
            }
        }
        throw new IllegalArgumentException("thirdType is not exist : " + code);
    }

    public static String getCode(String desc){
    	for (ThirdType t : ThirdType.values()) {
			if(t.getDesc().equals(desc)){
				return t.getCode();
			}
		}
		return null;
    }
}
