<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglibs.jsp"%>
<%@ include file="/static/common/meta.jsp"%>
<%@ include file="/static/common/jscsslibs/easyui.jsp"%>
<%@ include file="/static/common/jscsslibs/easyuicrud.jsp"%>
<%@ include file="/static/common/jscsslibs/easyuicommon.jsp"%>
<%@ include file="/static/common/jscsslibs/sysstyle.jsp"%>
<%@ include file="/static/common/jscsslibs/tools.jsp"%>

<link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/cupertino/easyui.css" id="swicth-style">
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/particular.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/static/js/jquery-easyui/themes/popup.css" />
<script type="text/javascript" src="${ctx}/static/js/jquery-easyui/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-easyui/datagrid-detailview.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jquery-easyui/extension/jquery-easyui-crud/jquery.uigrid.js"></script>
<link rel="stylesheet" type="text/css" href="${ctx}/static/styles/crmmain.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/static/styles/crm.css"/>
<script type="text/javascript" src="${ctx}/static/js/date/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ctx}/static/js/utils/utilTools.js"></script>
<%@ include file="/static/common/doing.jsp"%>

<script type="text/javascript">
//-------------------------------------------myview定义--------------------------------------------
var myview = $.extend({},$.fn.datagrid.defaults.view,{
		onAfterRender:function(target){
		    $.fn.datagrid.defaults.view.onAfterRender.call(this,target);
		    var opts = $(target).datagrid('options');
		    var vc = $(target).datagrid('getPanel').children('div.datagrid-view');
		    vc.children('div.datagrid-empty').remove();
		    if (!$(target).datagrid('getRows').length){
		        var d = $('<div class="datagrid-empty"></div>').html(opts.emptyMsg || 'no records').appendTo(vc);
		        d.css({
		            position:'absolute',
		            left:0,
		            top:50,
		            width:'100%',
		            textAlign:'center'
		        });
		    }
		}
	});
	var url;
//--------------------------------------------初始化页面--------------------------------------------
$(function() {
	showCreateAnnou();
});
function formatOper(val,row,index){  
	return '<a href="#" onclick="show('+index+')">查看</a> / <a href="#" onclick="del('+index+')">删除</a> ';  
	}  
function fixWidth(percent)  
{  
    return document.body.clientWidth * percent ; 
}  

//--------------------------------------------保存--------------------------------------------	
var checkSubmitFlg = false;
	function checkSubmit(){
		if(checkSubmitFlg == true){ 
			return false; //当表单被提交过一次后checkSubmitFlg将变为true,根据判断将无法进行提交。
		}
		checkSubmitFlg = true;
		return true;
	}
function saveRate(){
		if (checkSubmit()) {
			$('#submit').linkbutton({  
			    disabled:true
			});
			$('#edit_fm').form('submit', {
				url : '${ctx}/manage/rateCalculation/saveRateCalculation',
				onSubmit : function() {
					if($(this).form('validate')){
						return true;
					}else{
						$('#submit').linkbutton({  
						    disabled:false
						});
						checkSubmitFlg = false;
						return false;
					}
				},
				success : function(result) {
					$('#submit').linkbutton({  
					    disabled:false
					});
					var result = eval('(' + result + ')');
					$.messager.show({
						title : '提示',
						msg : result.data
					});
					checkSubmitFlg = false;
				}
			})
		}
}
//--------------------------------------------显示新建公告对话框--------------------------------------------
function showCreateAnnou(){
	$('#productName').combobox('setValue','${product}');
	$('#term').combobox('setValue','${term}');
	$('#channel').combobox('setValue','${channel}');
}
//--------------------------------------------格式化日期字段--------------------------------------------
function formatterdate(val, row) {
	var date = new Date(val);
	return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
}
//扩展easyui表单的验证  
$.extend($.fn.validatebox.defaults.rules, {  
	  intOrFloat: {// 验证整数或小数
          validator: function (value) {
              return /^\d+(\.\d+)?$/i.test(value);
          },
          message: '请输入数字，并确保格式正确'
      }
})
</script>
<style type="text/css">
.datagrid-btn-separator {
    float: none;
}
.datagrid-body {
  overflow-x:auto;
  overflow-y:auto;
}
.pad{
         background-color: Red;
        }
        
body {
	font-size:12px;
}
.portlet-title .caption {
    float: left;
    display: inline-block;
    font-size: 14px;
    font-weight: 400;
    margin: 0;
    padding: 0;
    margin-bottom: 7px;
}
table td{word-break: keep-all;white-space:nowrap;}
</style>
</style>
<body>
	<div class="content" style="padding:50px;"> 
	 <form id="edit_fm" method="post">
	    <table width="100%" border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td width="45%">
		        	<table width="100%" border="0" cellspacing="20" cellpadding="0">
		              <tr>
		                <td width="20%" align="right">产品名称</td>
		                <td>
			                <select  class="easyui-combobox" name="productName" id="productName" required="true" editable="false">
										<option value="网购达人贷">网购达人贷</option>
										<option value="薪生贷">薪生贷</option>
							</select>
						</td>
		              </tr>
		              <tr>
		                <td align="right"  white-space: nowrap;>金额上下限</td>
		                <td>
		                 	<input id="minAmount" name="minAmount" type="text" class="easyui-numberbox" data-options="min:0,precision:2" value="${minAmount}" required="true"/> - <input id="maxAmount" name="maxAmount" type="text" class="easyui-numberbox" data-options="min:0,precision:2" value="${maxAmount}" required="true"/>(元)
		              </tr>
		              <tr>
		                <td width="20%" align="right">期限</td>
		                <td>
		                	<select class="easyui-combobox" name="term" id="term" required="true" editable="false">
									<option value="36期">36期</option>
									<option value="24期">24期</option>
									<option value="12期">12期</option>
							</select>
						</td>
		              </tr>
		              <tr>
		                <td align="right">费率</td>
		                <td><input id="rate" name="rate" type="text" class="easyui-numberbox" data-options="min:0,precision:3" value="${rate}" required="true"/> %</td>
		              </tr>
		              <tr>
		                <td align="right">渠道名称</td>
		                <td>
		                	<select class="easyui-combobox" name="channel" id="channel" required="true" editable="false">
									<option value="捞财宝">捞财宝</option>
									<option value="陆金所">陆金所</option>
							</select>	
						</td>
		              </tr>
		              <tr>
		                <td align="right">&nbsp;</td>
		                <td>	
		                	<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveRate();">刷新缓存</a>
						</td>
		              </tr>
		            </table>
		        </td>
		        <td align="center" valign="middle">
		        请设置费率试算！<br><br>
		      	<img src="../static/images/pic_fl.png">
		        </td>
		        <td width="10%">
		        </td>
		      </tr>
	    </table>
	   </form>
    </div>
</body>

