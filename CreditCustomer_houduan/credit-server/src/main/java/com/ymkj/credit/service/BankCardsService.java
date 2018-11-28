package com.ymkj.credit.service;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.BankCards;
import com.ymkj.credit.common.util.PageUtils;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.springside.modules.orm.PageInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class BankCardsService {
	
	@Value("${SUPPORT_BANK_CODES}")
	private String checkBankCodes;

	/**
	 * 分页查询
	 * @param pageInfo
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageInfo<BankCards> selectPageByColumn(PageInfo<BankCards> pageInfo,BankCards bank){
		if(StringUtils.isNotEmpty(bank.getIdCard())){
			JSONObject param = new JSONObject();
			param.put("idNum", bank.getIdCard());
			JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4BindBankCard(
					Constants.CREDIT_URL_FINDBACKCARDINFO, param.toString());
			if(!("null").equals(loanJson.toString())){
				JSONArray array = loanJson.getJSONArray("bankCards");
				String name = loanJson.getString("name");
				if(array != null){
					ArrayList<BankCards> list = new ArrayList<BankCards>();
					for (int i= 0; i < array.size(); i++) {
						JSONObject obj = array.getJSONObject(i);
						BankCards bankCard = new BankCards();
						bankCard.setAccount((obj.getString("account")!=null?zh(obj.getString("account")):obj.getString("account")));
						bankCard.setAccounts(obj.getString("account"));
						bankCard.setBankName(obj.getString("bankName"));
						bankCard.setBankBranchName(obj.getString("bankBranchName"));
						bankCard.setIdCard((bank.getIdCard()!=null?zh(bank.getIdCard()):bank.getIdCard()));
						bankCard.setCard(bank.getIdCard());
						bankCard.setPhone((obj.containsKey("mobile")?(obj.getString("mobile")!=null?zh(obj.getString("mobile")):obj.getString("mobile")):""));
						bankCard.setBankCode(obj.getString("bankCode"));
						bankCard.setCheckCard(("01").equals(obj.getString("checkCard"))?obj.getString("checkCard"):(checkBankCode(bankCard) == true?obj.getString("checkCard"):"03"));
						bankCard.setName(name);
						bankCard.setCreateTime(null);
						list.add(bankCard);
					}
					Page<BankCards> page = new Page<BankCards>(pageInfo.getPageNo(), pageInfo.getPageSize());
					page.setTotal(list.size());
					int begin = (pageInfo.getPageNo()-1) * pageInfo.getPageSize();
					int end = list.size() > (pageInfo.getPageNo()*pageInfo.getPageSize())?pageInfo.getPageSize():list.size();
				    return PageUtils.convertPage(page,list.subList(begin, end));
				}
			}
		}	
		return PageUtils.convertPage(new Page(0,-1));
	}
	
	private String zh(String str){
		StringBuffer sb = new StringBuffer(str.substring(0,3));
		for (int i = 0; i < str.length()-7; i++) {
			sb.append("*");
		}
		sb.append(str.substring(str.length()-4, str.length()));
		return sb.toString();
	}
	
	/**
	 * 检测是否允许绑卡
	 * @param bankCards
	 * @return
	 */
	public Boolean checkBankCode(BankCards bankCards){
		if(checkBankCodes.indexOf(bankCards.getBankCode()) != -1){
			return true;
		}
		return false;
	}
}
