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
var InterValObj; //timer变量，控制时间
var count = 60; //间隔函数，1秒执行
var curCount;//当前剩余秒数

var InterValObjzx; //timer变量，控制时间
var countzx = 60; //间隔函数，1秒执行
var curCountzx;//当前剩余秒数

//timer处理函数
function SetRemainTime() {
    if (curCount == 0) {                
        window.clearInterval(InterValObj);//停止计时器
        $("#btnSendCode").removeAttr("disabled");//启用按钮
        $("#btnSendCode").val("重新发送验证码");
    }
    else {
        curCount--;
        $("#btnSendCode").val("请在" + curCount + "秒内输入验证码");
    }
}

//timer处理函数
function SetRemainTimezx() {
    if (curCountzx == 0) {                
        window.clearInterval(InterValObjzx);//停止计时器
        $("#btnSendCodezx").removeAttr("disabled");//启用按钮
        $("#btnSendCodezx").val("重新发送验证码");
    }
    else {
        curCountzx--;
        $("#btnSendCodezx").val("请在" + curCountzx + "秒内输入验证码");
    }
}

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
//--------------------------------------------初始化页面--------------------------------------------
$(function() {
		$('#searchBt').click(function() {
			var queryParams = $('#bank_List').datagrid('options').queryParams;
			queryParams.idCard = $('#idcard_s').val();
			$('#bank_List').datagrid('options').queryParams = queryParams;
			$("#bank_List").datagrid('load');
			$('#bank_List').datagrid({onLoadSuccess : function(data){
				var rows=$('#bank_List').datagrid("getRows");
				 if (rows.length > 0) {
					 $('#bank_List').datagrid('selectRow',0);
					 var row=$('#bank_List').datagrid("getSelections");//获取选中的数据
		             $("#ckr").html(row[0].name);
		             $("#addBank").linkbutton({disabled:false});
				 }else{
					 $("#ckr").html("");
					 $("#addBank").linkbutton({disabled:true});
				 }
			}});
			//alert($('#bank_List').datagrid('getData').rows[0]);
		});
		$('#clearQry').click(function() {
			$('#queryForm').form('clear');
		});
		$('#bankName').combobox({ 
            required:true,    
            multiple:false, //多选
            editable:false  //是否可编辑
        });
});

//--------------------------------------------保存--------------------------------------------	
var checkSubmitFlg = false;
function checkSubmit(){
	if(checkSubmitFlg == true){ 
		return false; //当表单被提交过一次后checkSubmitFlg将变为true,根据判断将无法进行提交。
	}
	checkSubmitFlg = true;
	return true;
}
function savezxzh(){
	//if (checkSubmit()) {
		var pattern = /^1\d{10}$/;
		var phone = $.trim($("#phonezx").val());
		if(!pattern.test(phone)){
			$.messager.alert('温馨提示',"手机输入不正确！",'info');
			return false;
		}
		var verifyzx = $.trim($("#verifyzx").val());
		if(verifyzx == ''){
			$.messager.alert('温馨提示',"验证码不能为空！",'info');
			return false;
		}
		var flowIdzx = $.trim($("#flowIdzx").val());
		if(flowIdzx == ''){
			$.messager.alert('温馨提示',"验证码过期请重新获取！",'info');
			return false;
		}
		if(curCountzx > 0){
			$('#edit_fmzx').form('submit', {
				url : '${ctx}/manage/bank/updateVerify',
				onSubmit : function() {
					if($(this).form('validate')){
						return true;
					}else{
						return false;
					}
				},
				success : function(result) {
					var result = eval('(' + result + ')');
					if (result.status) {
						$('#edit_fmzx').form('clear');
						$('#edit_dlgzx').dialog('close');
						$('#bank_List').datagrid('reload');
					} else {
						$.messager.alert('温馨提示',result.data,'info');
					}
					checkSubmitFlg = false;
				}
			});
		}else{
			$.messager.alert('温馨提示',"验证码超时，请重新获取！",'info');
			return false;
		}
		
	//}
}
function saveczmm(){
		var row = $('#bank_List').datagrid('getSelected');
		//if (checkSubmit()) {
			var account = $.trim($("#account").val());
			if(account == ''){
				$.messager.alert('温馨提示',"银行卡号必填！",'info');
				return false;
			}
			var bankBranchName = $.trim($("#bankBranchName").val());
			if(bankBranchName == ''){
				$.messager.alert('温馨提示',"所属支行不能为空！",'info');
				return false;
			}
			var pattern = /^1\d{10}$/;
			var phone = $.trim($("#phone").val());
			if(!pattern.test(phone)){
				$.messager.alert('温馨提示',"手机输入不正确！",'info');
				return false;
			}
			var verifyzx = $.trim($("#verify").val());
			if(verifyzx == ''){
				$.messager.alert('温馨提示',"验证码不能为空！",'info');
				return false;
			}
			var flowIdzx = $.trim($("#flowId").val());
			if(flowIdzx == ''){
				$.messager.alert('温馨提示',"验证码过期请重新获取！",'info');
				return false;
			}
			if(curCount > 0){
				$('#edit_fm').form('submit', {
					url : '${ctx}/manage/bank/saveVerify?bankName='+$('#bankName').combobox('getText'),
					onSubmit : function() {
						if($(this).form('validate')){
							return true;
						}else{
							return false;
						}
					},
					success : function(result) {
						var result = eval('(' + result + ')');
						if (result.status) {
							$('#edit_fm').form('clear');
							$('#edit_dlg').dialog('close');
							$('#bank_List').datagrid('reload');
						} else {
							$.messager.alert('温馨提示',result.data,'info');
						}
						checkSubmitFlg = false;
					}
				});
			}else{
				$.messager.alert('温馨提示',"验证码超时，请重新获取！",'info');
				return false;
			}
	//}
}
//--------------------------------------------显示新建公告对话框--------------------------------------------
//添加银行卡
function showTjyh(){
	var row = $('#bank_List').datagrid('getSelected');
	if(row){
		 window.clearInterval(InterValObj);//停止计时器
         $("#btnSendCode").removeAttr("disabled");//启用按钮
         $("#btnSendCode").val("发送验证码");
         count = 60;
		 var data = $('#bankName').combobox('getData');
		 $('#bankName').combobox('setValue', data[0].id);//设置默认值
		 $("#phone").val("");
		 $("#verify").val("");
		 $("#bankBranchName").val("");
		 $("#account").val("");
      	 $("#idcard").val(row.idCard);
      	 $("#name").val(row.name);
      	 $("#idcard_t").val(row.card);
      	 
		 $('#edit_dlg').dialog({
		 	title: '新增银行卡',
		    modal: true
		 });
		
		$('#edit_dlg').dialog('open');
       	 
    }else{
		$.messager.alert('温馨提示',"请选择一条数据",'info');
	}
}
//绑定银行卡
function showZxzh(index){
	 $('#bank_List').datagrid('selectRow',index);
	 var row = $('#bank_List').datagrid("getSelections")[0];//获取选中的数据
	if(row){
		$.ajax({
            type: "post",
            url: "${ctx}/manage/bank/chackBankCode",
            data: {
           	 	bankCode:row.bankCode,
           	 },
            dataType: "json",
            success: function(result){
				if (result.status) {
					window.clearInterval(InterValObjzx);//停止计时器
			        $("#btnSendCodezx").removeAttr("disabled");//启用按钮
			        $("#btnSendCodezx").val("发送验证码");
			        countzx = 60;
					$("#phonezx").val("");
					$("#verifyzx").val("");
					 $("#idcardzx").val(row.idCard);
			      	 $("#namezx").val(row.name);
			      	 $("#idcard_tzx").val(row.card);
			      	 $("#accountszx").val(row.accounts);
			      	 $("#accountzx").val(row.account);
			      	 $("#bankNamezx").val(row.bankName);
			      	 $("#bankBranchNamezx").val(row.bankBranchName);
			      	 $("#bankCodezx").val(row.bankCode);
			      	 
					$('#edit_dlgzx').dialog({
					 	title: '绑定银行卡',
					    modal: true
					});
					$('#edit_dlgzx').dialog('open');
				} else {
					$.messager.alert('温馨提示',result.data,'info');
				}
            }
        });
	}else{
		$.messager.alert('温馨提示',"请选择一条数据",'info');
	}
}
//--------------------------------------------格式化日期字段--------------------------------------------
function formatterdate(val, row) {
	if(val == null){
		return "";
	}else{
		var datetime = new Date(val);
		var year = datetime.getFullYear();
	    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
		return year + '.' + month + '.' + date;
	}
}
function sfbd(val,row,index){
	if(val == "00"){
		return '<a href="#" class="easyui-linkbutton" onclick="showZxzh('+index+');">绑定银行卡</a>';
	}
	if(val == "01"){
		return "<label class='bitian' style='color: green;'>已绑定</label>";
	}
	return "<label class='bitian' style='color: green;'>未绑定</label>";
}
//绑定发送验证码
function fsVerifyzx(){
	var pattern = /^1\d{10}$/;
	var phone = $.trim($("#phonezx").val());
	if(phone == ''){
		$.messager.alert('温馨提示',"手机号不能为空！",'info');
		return false;
	}
	if(!pattern.test(phone)){
		$.messager.alert('温馨提示',"手机输入不正确！",'info');
		return false;
	}
	 $.ajax({
            type: "post",
            url: "${ctx}/manage/bank/sendVerify",
            data: {
           	 	bankCode:$("#bankCodezx").val(),
           	 	card:$("#idcard_tzx").val(),
           	 	name:$("#namezx").val(),
           	 	accounts:$("#accountszx").val(),
           	 	bankName:$("#bankNamezx").val(),
           	 	phone:$("#phonezx").val()
           	 },
            dataType: "json",
            success: function(result){
			if (result.status) {
				var msg = eval('(' + result.data + ')');
				$("#flowIdzx").val(msg.flowId);
				curCountzx = countzx;
				//设置button效果，开始计时
			    $("#btnSendCodezx").attr("disabled", "true");
			    $("#btnSendCodezx").val("请在" + curCountzx + "秒内输入验证码");
			    InterValObjzx = window.setInterval(SetRemainTimezx, 1000); //启动计时器，1秒执行一次
			} else {
				$.messager.alert('温馨提示',result.data,'info');
			}
            }
        });
}
//新增发送验证码
function fsVerify(){
	var account = $.trim($("#account").val());
	if(account == ''){
		$.messager.alert('温馨提示',"银行卡号必填！",'info');
		return false;
	}
	var bankBranchName = $.trim($("#bankBranchName").val());
	if(bankBranchName == ''){
		$.messager.alert('温馨提示',"所属支行不能为空！",'info');
		return false;
	}
	var pattern = /^1\d{10}$/;
	var phone = $.trim($("#phone").val());
	if(phone == ''){
		$.messager.alert('温馨提示',"手机号不能为空！",'info');
		return false;
	}
	if(!pattern.test(phone)){
		$.messager.alert('温馨提示',"手机输入不正确！",'info');
		return false;
	}
	 $.ajax({
            type: "post",
            url: "${ctx}/manage/bank/sendVerify",
            data: {
           	 	bankCode:$('#bankName').combobox('getValue'),
           	 	card:$("#idcard_t").val(),
           	 	name:$("#name").val(),
           	 	accounts:$("#account").val(),
           	 	bankName:$('#bankName').combobox('getText'),
           	 	phone:$("#phone").val()
           	 },
            dataType: "json",
            success: function(result){
			if (result.status) {
				var msg = eval('(' + result.data + ')');
				$("#flowId").val(msg.flowId);
				curCount = count;
				//设置button效果，开始计时
			    $("#btnSendCode").attr("disabled", "true");
			    $("#btnSendCode").val("请在" + curCount + "秒内输入验证码");
			    InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
			} else {
				$.messager.alert('温馨提示',result.data,'info');
			}
            }
        });
}

function clearz(){
	$('#edit_fm').form('clear');
	$('#edit_dlg').dialog('close');
}
function clearzx(){
	$('#edit_fmzx').form('clear');
	$('#edit_dlgzx').dialog('close');
}
</script>
<style type="text/css">
.datagrid-btn-separator {
    float: none;
}
.datagrid-body {
  overflow-x:auto;
  overflow-y:auto;
}
</style>
<body>
	<table id="bank_List" class="easyui-datagrid" striped="true"  toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true" fitColumns="true" fit="true" style="overflow-x:hidden"
	pageSize="15"  pageList=[15,30,45,60] 
	 data-options="url :'${ctx}/manage/bank/bankPage',
	 			view: myview,
		        emptyMsg: '暂无数据',
		onDblClickRow : function(rowIndex, rowData) {
		},
		onSelect : function() {
		}" >
		<thead>
			<tr>
			    <th field="id" sortable="true" hidden="hidden" formatter : controllIfSelected sortable="true">ID</th>
			    <th field="name" width ="20" align="center">持卡人</th>
			    <th field="account" width ="20" align="center">银行卡号</th>
				<th field="bankName" width ="20" align="center">所属银行</th>
				<th field="bankBranchName"  width ="20" align="center">所属支行</th>
				<th field="phone"  width ="20" align="center">手机号</th>
				<th field="idCard"  width ="20" align="center">身份证号码</th>
				<th field="checkCard"  width ="20" formatter="sfbd" align="center">是否绑定</th>
				<th field="createTime"  width ="20" formatter="formatterdate" align="center">绑定时间</th>
			</tr>
		</thead>
	</table>
	<div id="tb" style="padding: 5px; height: auto">
		
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
			<form id="querymessage" method="post">
				<table border="0" width="50%" style="margin-left: 20px;">
					<tr>
						<td>
							身份证号码：<input id="idcard_s" name="idCard" type="text" value=""/>
							<span class="datagrid-btn-separator"></span>	
							<a href="#" id="searchBt" class="easyui-linkbutton" iconCls="icon-search" plain="true">查询</a>	
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
			<div style="margin-left:10px;float: left;">
				<span class="datagrid-btn-separator"></span>
				<a href="javascript:void(0);" id="addBank" class="easyui-linkbutton" iconCls="icon-add" plain="true" onClick="showTjyh()" disabled ="true">添加银行卡</a>
			</div>
			<div style="margin-right:100px; text-align: right; margin-top: 4px;">
				<span class="datagrid-btn-separator"></span>
				持卡人:<label class="bitian" id ="ckr"></label>
			</div>
		</div>
	</div>
	<!-- 添加银行卡-->
	<div id="edit_dlg" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttons" >
			<form id="edit_fm" method="post">
			 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
			   <tr>
	      			<td align="left"><label class="bitian">身份证号</label></td>
	      			<td align="left">
	      				<input type="hidden" id="idcard_t" name="idCard"/>
	      				<input type="hidden" id="flowId" name="flowId">
	      				<input type="text" id="idcard" style="width:200px" readonly="readonly"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">持卡人</label></td>
	      			<td align="left">
	      				<input type="text" id="name" style="width:200px" name="name"  readonly="readonly"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">银行卡号</label></td>
	      			<td align="left">
	      				<input type="text" id="account" style="width:200px" class="easyui-validatebox" onkeyup="this.value=this.value.replace(/\D/g,'')" maxlength="23" name="account"  required="true"/>
	      			</td>
			   </tr>
			    <tr>
	      			<td align="left"><label class="bitian">所属银行</label></td>
	      			<td align="left">
	      				<input id="bankName" class="easyui-combobox" name="bankCode" style="width:200px" data-options="valueField:'id',textField:'text',url:'${ctx }/manage/bank/bankName'">
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">所属支行</label></td>
	      			<td align="left">
	      				<input type="text" id="bankBranchName" style="width:200px" class="easyui-validatebox" name="bankBranchName"  required="true"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">预留手机号</label></td>
	      			<td align="left">
	      				<input type="text" id="phone" style="width:200px" class="easyui-validatebox" onkeyup="this.value=this.value.replace(/\D/g,'')" maxlength="11" name="phone"  required="true"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">验证码</label></td>
	      			<td align="left">
	      				<input type="text" id="verify" style="width:200px" class="easyui-validatebox" size="18" name="verCode"  required="true"/>
<!-- 	      				<a href="#" id="submit"  class="easyui-linkbutton btnSendCode" onclick="fsVerify();">发送验证码</a> -->
	      				<input id="btnSendCode" class="easyui-linkbutton" type="button" value="发送验证码" onclick="fsVerify()" />
	      			</td>
			   </tr>
			</table>
			</form>
		</div>
		<div id="${entityName}_dlg-buttons" style="text-align:center;width:500px; ">
			<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveczmm();">绑定</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="clearz();">取消</a>
		</div>
		<!-- 注销账户 -->
		<div id="edit_dlgzx" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttonszx" >
			<form id="edit_fmzx" method="post">
			 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
			   <tr>
	      			<td align="left"><label class="bitian">身份证号</label></td>
	      			<td align="left">
	      				<input type="hidden" id="bankCodezx" name="bankCode">
	      				<input type="hidden" id="idcard_tzx" name="card"/>
	      				<input type="hidden" id="flowIdzx" name="flowId">
	      				<input type="text" id="idcardzx" style="width:200px" readonly="readonly"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">持卡人</label></td>
	      			<td align="left">
	      				<input type="text" id="namezx" style="width:200px" name="name"  readonly="readonly"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">银行卡号</label></td>
	      			<td align="left">
	      				<input type="hidden" id="accountszx" style="width:200px"  name="accounts"  />
	      				<input type="text" id="accountzx" style="width:200px" name="account" readonly="readonly" />
	      			</td>
			   </tr>
			    <tr>
	      			<td align="left"><label class="bitian">所属银行</label></td>
	      			<td align="left">
	      				<input type="text" id="bankNamezx" style="width:200px" readonly="readonly" name="bankName"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">所属支行</label></td>
	      			<td align="left">
	      				<input type="text" id="bankBranchNamezx" style="width:200px" readonly="readonly" name="bankBranchName"/>
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">预留手机号</label></td>
	      			<td align="left">
	      				<input type="text" id="phonezx" style="width:200px" onkeyup="this.value=this.value.replace(/\D/g,'')" maxlength="11" class="easyui-validatebox" name="phone" required="true" />
	      			</td>
			   </tr>
			   <tr>
	      			<td align="left"><label class="bitian">验证码</label></td>
	      			<td align="left">
	      				<input type="text" id="verifyzx" style="width:200px" class="easyui-validatebox" onkeyup="this.value=this.value.replace(/\D/g,'')" name="verCode" required="true" />
	      				<input id="btnSendCodezx" class="easyui-linkbutton" type="button" value="发送验证码" onclick="fsVerifyzx()" />
	      			</td>
			   </tr>
			</table>
			</form>
		</div>
		<div id="${entityName}_dlg-buttonszx" style="text-align:center;width:500px; ">
			<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="savezxzh();">绑定</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:clearzx();">取消</a>
		</div>
</body>

