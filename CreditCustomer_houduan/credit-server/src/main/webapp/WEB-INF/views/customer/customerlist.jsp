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
		   // if($(target).datagrid('getRows')[0].id > 0){
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
		    //}else{
		    	//$("#customer_List").datagrid('load');
		    	//$('#customer_List').datagrid('loadData', { total: 0, rows: [] });
		    	//parent.location.reload();
		   // }
		}
	});
//--------------------------------------------初始化页面--------------------------------------------
$(function() {
		$('#searchBt').click(function() {
		var queryParams = $('#customer_List').datagrid('options').queryParams;
		queryParams.customerName = $('#name_s').val();
		queryParams.phone = $('#phone_s').val();
		queryParams.idCard = $('#idcard_s').val();
		queryParams.beginDate = $('#beginDate_s').combobox('getValue'); //设置值
		queryParams.endDate = $('#endDate_s').combobox('getValue'); //设置值
		$('#customer_List').datagrid('options').queryParams = queryParams;						
		$("#customer_List").datagrid('load');
	});
	$('#clearQry').click(function() {
			$('#queryForm').form('clear');
	});
	 
	$('input').keydown(function (e) {
        if (e.keyCode == 13) {
           return false;
        }
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
// 	新建账户
function addxjzh(){
	if (checkSubmit()) {
		$('#submit').linkbutton({  
		    disabled:true
		});
		var phone = $('#add_phone').val();
		if(phone == ''){
			$.messager.alert('温馨提示',"手机号不能为空！",'info');
			$('#submit').linkbutton({  
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		var phonepn = /^1\d{10}$/;
		if(!phonepn.test(phone)){
			$.messager.alert('温馨提示',"手机输入不正确！",'info');
			$('#submit').linkbutton({  
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		
		var name = $('#add_name').val();
		if(name == ''){
			$.messager.alert('温馨提示','姓名不能为空!','info');
			$('#submit').linkbutton({ 
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		
		var reg=/^·*[\u4e00-\u9fa5]+[·\u4e00-\u9fa5]*·*$/;
		if(!reg.test(name)){
			$.messager.alert('温馨提示','姓名只能输入汉字和“·”!','info');
			$('#submit').linkbutton({ 
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		
		var idcard = $('#add_idCard').val();
		if( idcard ==''){
			$.messager.alert('温馨提示','身份证号不能为空!','info');
			$('#submit').linkbutton({  
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		var pattern = /(^\d{18}$)|(^\d{17}(\d|X)$)/;
		
		if(!pattern.test(idcard)){
			$.messager.alert('温馨提示','身份证格式不正确!','info');
			$('#submit').linkbutton({  
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		$('#add_fm').form('submit', {
			url : '${ctx}/manage/customer/addCustomer',
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
				if (result.status) {		
					$('#add_dlg').dialog('close');
					$('#customer_List').datagrid('reload');
					$.messager.alert('温馨提示','新建账户成功！','info');
				} else {
					$.messager.alert('温馨提示',result.data,'info');
				}
				checkSubmitFlg = false;
			}
		})
	}
}
// 注销账户
function savezxzh(){
	var row = $('#customer_List').datagrid('getSelected');
	if (checkSubmit()) {
		$('#submit').linkbutton({  
		    disabled:true
		});
		var idcard = $('#idcardzx').val();
		if( idcard ==''){
			$.messager.alert('温馨提示','身份证号不能为空!','info');
			$('#submit').linkbutton({  
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		var pattern = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
		
		if(!pattern.test(idcard)){
			$.messager.alert('温馨提示','身份证格式不正确!','info');
			$('#submit').linkbutton({  
			    disabled:false
			});
			checkSubmitFlg = false;
			return false;
		}
		$('#edit_fmzx').form('submit', {
			url : '${ctx}/manage/customer/updateCancel?id='+row.id,
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
				if (result.status) {		
					$('#edit_dlgzx').dialog('close');
					$('#customer_List').datagrid('reload');
				} else {
					$.messager.alert('温馨提示',result.data,'info');
				}
				checkSubmitFlg = false;
			}
		})
	}
}
// 重置密码
function saveczmm(){
		var row = $('#customer_List').datagrid('getSelected');
		if (checkSubmit()) {
			$('#submit').linkbutton({  
			    disabled:true
			});
			if(row.flowStatus=='是'){
				var idcard = $('#idcard').val();
				if( idcard ==''){
					$.messager.alert('温馨提示','身份证号不能为空!','info');
					$('#submit').linkbutton({  
					    disabled:false
					});
					checkSubmitFlg = false;
					return false;
				}
				var pattern = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
				if(!pattern.test(idcard)){
					$.messager.alert('温馨提示','身份证格式不正确!','info');
					$('#submit').linkbutton({  
					    disabled:false
					});
					checkSubmitFlg = false;
					return false;
				}
			}
			$('#edit_fm').form('submit', {
				url : '${ctx}/manage/customer/updateReset?id='+row.id,
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
					if (result.status) {		
						$('#edit_dlg').dialog('close');
						$('#customer_List').datagrid('reload');
					} else {
						$.messager.alert('温馨提示',result.data,'info');
					}
					checkSubmitFlg = false;
				}
			})
	}
}

//--------------------------------------------显示新建公告对话框--------------------------------------------
function showAdd(){
	$('#add_dlg').dialog({
	 	title: '新建账户',
	    modal: true
	});
	$('#add_dlg').dialog('open');	
	
	$('#add_fm').form('clear');
}

function showCzmm(){
	var row = $('#customer_List').datagrid('getSelected');
	if(row){
		$('#bts').html("确定为客户("+row.customerName+")进行重置密码操作吗?");
		if(row.flowStatus=='否'){
			$('#idcard').attr("readonly", true);
		}else{
			$('#idcard').removeAttr("readonly");
		}
		$('#edit_dlg').dialog({
		 	title: '重置密码',
		    modal: true
		});
		$('#edit_dlg').dialog('open');	
		
		$('#edit_fm').form('clear');
	}else{
		$.messager.alert('温馨提示',"请选择一条数据",'info');
	}
}

function showZxzh(){
	var row = $('#customer_List').datagrid('getSelected');
	if(row){
		$('#btszx').html("确定为客户("+row.customerName+")注销账户吗?"); 
		
		$('#edit_dlgzx').dialog({
		 	title: '注销账户',
		    modal: true
		});
		$('#edit_dlgzx').dialog('open');
		
		$('#edit_fmzx').form('clear');
	}else{
		$.messager.alert('温馨提示',"请选择一条数据",'info');
	}
}
//--------------------------------------------格式化日期字段--------------------------------------------
function formatterdate(val, row) {
	if(val == null || val == '' || val == 'undefined'){
		return "";
	}else{
		var datetime = new Date(val);
		var year = datetime.getFullYear();
	    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
		return year + '.' + month + '.' + date;
	}
}

//重置
function doReset(){
	$('#querymessage').form('clear');
	//$('#customer_List').datagrid({queryParams: {}});
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
	<table id="customer_List" class="easyui-datagrid" striped="true"  toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true" fitColumns="true" fit="true" style="overflow-x:hidden"
	pageSize="15"  pageList=[15,30,45,60] 
	 data-options="url :'${ctx}/manage/customer/customerPage',
	 			view: myview,
		        emptyMsg: '暂无数据',
		onDblClickRow : function(rowIndex, rowData) {
		},
		onSelect : function() {
		}" >
		<thead>
			<tr>
				<th field ='ck'checkbox ="true"><input type="checkbox" id="ckAll" name="DataGridCheckbox" value="全选" /></th>
			    <th field="id" sortable="true" hidden="hidden" formatter : controllIfSelected sortable="true">ID</th>
			    <th field="customerName" width ="20" align="center">客户姓名</th>
				<th field="phone" width ="20" align="center">手机号</th>
				<th field="idCard"  width ="20" align="center">身份证号码</th>
				<th field="flowStatus"  width ="20" align="center">是否认证</th>
				<th field="createTime"  width ="20" formatter="formatterdate" align="center">注册时间</th>
			</tr>
		</thead>
	</table>
	<div id="tb" style="padding: 5px; height: auto">
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
			<div style="margin-left:10px;float: left;">
				<span class="datagrid-btn-separator"></span>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onClick="showAdd()">新建账户</a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-reload" plain="true" onClick="showCzmm()">重置密码</a>
			</div>
			<div style="margin-right:100px; text-align: right;">
				<span class="datagrid-btn-separator"></span>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" plain="true" onClick="showZxzh()">注销账户</a>
			</div>
		</div>
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
			<form id="querymessage" method="post">
				<table border="0" width="100%" style="margin-left: 20px;">
					<tr>
						<td ><label class="bitian">客户姓名：</label>
							<input id="name_s" name="customerName" type="text" style="margin-left: 12px;"/> 
						</td>
						<td>
							<label class="bitian">手机号码：</label>
							<input id="phone_s" name="phone" type="text" style="margin-left: 12px;"/> 					
						</td>
						<td>
							<label class="bitian">身份证号码：</label>
							<input id="idcard_s" name="idCard" type="text"/> 					
						</td>
					</tr>
					<tr>
						<td>
							<label class="bitian">注册时间起：</label>
							<input  class="easyui-datebox " data-options="editable:false" id="beginDate_s" name = "beginDate"/>			
						</td>
						<td>
							<label class="bitian">注册时间止：</label>
							<input  class="easyui-datebox " data-options="editable:false" readonly='readonly' id="endDate_s" name = "endDate"/>					
						</td>
						<td>
							<span class="datagrid-btn-separator"></span>	
							<a href="#" id="searchBt" class="easyui-linkbutton" iconCls="icon-search" plain="true">查询</a>
							<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true" onclick="doReset()">清空</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<!-- 新建账户 -->
	<div id="add_dlg" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttonsadd" >
		<form id="add_fm" method="post">
		 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
		     <tr>
      			<td align="left">
      				<label class="bitian">手机号码:</label>
      			</td>
      			<td align="left">
      				<input type="text" style="width:200px" id="add_phone" class="easyui-validatebox" size="11" maxlength="11" name="phone"  required="true"/>
      			</td>
		     </tr>
		     <tr>
      			<td align="left">
      				<label class="bitian">姓名:</label>
      			</td>
      			<td align="left">
      				<input type="text" style="width:200px" id="add_name" class="easyui-validatebox" size="20" maxlength="20" name="customerName"  required="true"/>
      			</td>
		     </tr>
		     <tr>
      			<td align="left">
      				<label class="bitian">身份证号:</label>
      			</td>
      			<td align="left">
      				<input type="text" style="width:200px" id="add_idCard" class="easyui-validatebox" maxlength="18" size="18" name="idCard"  required="true"/>
      			</td>
		   </tr>
		     <tr>
      			<td align="left">
      			</td>
      			<td align="left">
      				<label class="bitian" style="color: red;">密码将发送到用户手机号码!</label>
      			</td>
		   </tr>
		</table>
		</form>
	</div>
	<div id="${entityName}_dlg-buttonsadd" style="text-align:center;width:500px; ">
		<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="addxjzh();">确定</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#add_dlg').dialog('close')">取消</a>
	</div>
	<!-- 重置密码 -->
	<div id="edit_dlg" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttons" >
			<form id="edit_fm" method="post">
			 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
			   <tr>
	      			<td align="left"><label class="bitian" id="bts"></label></td>
			   </tr>
			     <tr>
	      			<td align="left">
	      				<label class="bitian">身份证号</label>
	      				<input id="idcard" style="width:200px" class="easyui-validatebox" size="18" name="idCard" />
	      				<label class="bitian" style="color: red;">已认证用户必填</label>
	      			</td>
			   </tr>
			     <tr>
	      			<td align="left"><label class="bitian" style="color: red;">新密码将发送到用户手机号码!</label></td>
			   </tr>
			</table>
			</form>
		</div>
		<div id="${entityName}_dlg-buttons" style="text-align:center;width:500px; ">
			<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="saveczmm();">确定</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#edit_dlg').dialog('close')">取消</a>
		</div>
		<!-- 注销账户 -->
		<div id="edit_dlgzx" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttonszx" >
			<form id="edit_fmzx" method="post">
			 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
			   <tr>
	      			<td align="left"><label class="bitian" id="btszx"></label></td>
			   </tr>
			     <tr>
	      			<td align="left">
	      				<label class="bitian">身份证号</label>
	      				<input type="text" id="idcardzx" style="width:200px" class="easyui-validatebox" size="18" name="idCard"  required="true"/>
	      			</td>
			   </tr>
			     <tr>
	      			<td align="left"><label class="bitian" style="color: red;">注销账户将无法恢复!</label></td>
			   </tr>
			</table>
			</form>
		</div>
		<div id="${entityName}_dlg-buttonszx" style="text-align:center;width:500px; ">
			<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="savezxzh();">确定</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#edit_dlgzx').dialog('close')">取消</a>
		</div>
</body>

