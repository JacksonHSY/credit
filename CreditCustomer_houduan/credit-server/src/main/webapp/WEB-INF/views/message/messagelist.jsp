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
		$('#searchBt').click(function() {
		var queryParams = $('#message_List').datagrid('options').queryParams;
		queryParams.customerName = $('#name_s').val();
		queryParams.phone = $('#phone_s').val();
		queryParams.beginDate = $('#beginDate_s').combobox('getValue'); //设置值
		queryParams.endDate = $('#endDate_s').combobox('getValue'); //设置值
		$('#message_List').datagrid('options').queryParams = queryParams;
		$("#message_List").datagrid('load');
	});
		$('#clearQry').click(function() {
			$('#queryForm').form('clear');
	});
		
	 $(".test").each(function () {
            $(this).click(function () {
                $(this).addClass("pad");
                $(this).siblings().removeClass("pad");
            })
        });
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
function savemessage(){
		if (checkSubmit()) {
			$('#submit').linkbutton({  
			    disabled:true
			});
			var content = $('#content').val();
			if( content ==''){
				$.messager.alert('温馨提示','消息内容为必填!','info');
				$('#submit').linkbutton({  
				    disabled:false
				});
				checkSubmitFlg = false;
				return false;
			}
			$('#edit_fm').form('submit', {
				url : '${ctx}/manage/message/savemessage',
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
						ids=[];				
						$('#edit_dlg').dialog('close');
						$('#message_List').datagrid('reload');
					} else {
						$.messager.show({
							title : '提示',
							msg : result.data
						});
					}
					checkSubmitFlg = false;
				}
			})
	}
}
//--------------------------------------------显示新建公告对话框--------------------------------------------
function showCreateAnnou(){
	$('#edit_dlg').dialog({
	 	title: '发送新消息',
	    modal: true
	});
   
	$(".test").each(function () {$(this).siblings().removeClass("pad");});
	$('#edit_dlg').dialog('open');	
	
	$('#edit_fm').form('clear');
}
//--------------------------------------------格式化日期字段--------------------------------------------
function formatterdate(val, row) {
	var date = new Date(val);
	return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
}

//--------------------------------------------内容模板--------------------------------------------
function textModel(val) {
	if(val==1){
		$("#content").val("尊敬的客户,您好!我司将于明日发行人民币理财产品1,退订回复T");
	}else if(val==2){
		$("#content").val("尊敬的客户,您好!我司将于明日发行人民币理财产品2,退订回复T");
	}else{
		$("#content").val("尊敬的客户,您好!我司将于明日发行人民币理财产品3,退订回复T");
	}
	
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
.pad{
         background-color: Red;
        }
</style>
<body>
	<table id="message_List" class="easyui-datagrid" striped="true"  toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true" fitColumns="true" fit="true" style="overflow-x:hidden"
	pageSize="15"  pageList=[15,30,45,60] 
	 data-options="url :'${ctx}/manage/message/showMessage',
	 			view: myview,
				singleSelect:true,
				rownumbers:true,
				fitColumns:true,
				selectOnCheck:true,
		      emptyMsg: '暂无数据',
		onDblClickRow : function(rowIndex, rowData) {
		},
		onSelect : function() {
		}" >
		<thead>
			<tr>
			    <th field="id" sortable="true" hidden="hidden" formatter : controllIfSelected sortable="true">ID</th>
			    <th field="customerName" width ="30" align="center">客户姓名</th>
				<th field="phone" width ="30" align="center">手机号</th>
				<th field="content"  width ="30" align="center">内容</th>
				<th field="createTimeStr"  width ="30" formatter="formatterdate" align="center">时间</th>
			
			</tr>
		</thead>
	</table>
	<div id="tb" style="padding: 5px; height: auto">
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
		<span class="datagrid-btn-separator"></span>
			<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onClick="showCreateAnnou()">发送新消息</a>
		</div>
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
			<form id="querymessage" method="post">
				<table border="0" width="100%" style="margin-left: 20px;">
					<tr>
						<td >
							客户姓名：<input id="name_s" name="customerName" type="text"/> 
						</td>
						<td>
							手机号码：<input id="phone_s" name="phone" type="text"/> 					
						</td>
						<td>
							发送时间起：
							<input  class="easyui-datebox " id="beginDate_s" name = "beginDate"/>					
						</td>
						<td>
							发送时间止：
							<input  class="easyui-datebox " id="endDate_s" name = "endDate"/>					
						</td>
						<td>
							<span class="datagrid-btn-separator"></span>	
								<a href="#" id="searchBt" class="easyui-linkbutton" iconCls="icon-search" plain="true">查询</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<!-- 新建公告会话框 -->
	<div  id="edit_dlg" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttons" >
			<form id="edit_fm" method="post">
			 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
			 	<input name="id" id="id" type="hidden" />
			   <tr>
	      			<td><label class="bitian">客户姓名</label></td>
	      			<td  align="left">
						<input type="text" id="customerName" style="width:200px" class="easyui-validatebox" size="49" name="customerName"  required="false"/>
					</td>
			   </tr>
			     <tr>
	      			<td><label class="bitian">手机号</label></td>
	      			<td  align="left">
						<input type="text" id="phone" style="width:200px" class="easyui-validatebox" size="49" name="phone"  required="true"/>
					</td>
			   </tr>
			     <tr>
	      			<td><label class="bitian">选择模板</label></td>
	      			<td  align="left">
						<span class = "test"  onclick ="textModel(1)">模板1</span> 
						<span class = "test"  onclick ="textModel(2)">模板2</span>  
						<span class = "test"  onclick ="textModel(3)">模板3</span>
					</td>
			   </tr>
			   <tr>
	      			<td><label class="bitian">短信内容</label></td>
	      			<td align="left" >
						<textarea name="content" id="content" maxlength="200" style="width: 70%" rows="5" ></textarea>	
					</td>	
			   </tr> 
			</table>
			</form>
		</div>
		<div id="${entityName}_dlg-buttons" style="text-align:center;width:500px; ">
			<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="savemessage();">提交</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#edit_dlg').dialog('close')">取消</a>
		</div>
</body>

