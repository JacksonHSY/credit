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
		var queryParams = $('#banner_List').datagrid('options').queryParams;
		queryParams.codekey = $('#codekey').val();
		queryParams.num = $('#num').val();
 		queryParams.startDate = $('#startDate').combobox('getValue'); //设置值
 		queryParams.endDate = $('#endDate').combobox('getValue'); //设置值
		$('#banner_List').datagrid('options').queryParams = queryParams;
		$("#banner_List").datagrid('load');
	});
});
function formatOper(val,row,index){  
	return '<a href="#" onclick="show('+index+')">查看</a> / <a href="#" onclick="del('+index+')">删除</a> ';  
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
	//---------提交表单
		function savemessage(){
			var form = new FormData($('#edit_fm')[0]);
			//校验参数
			var codekey = $("#codekeys").val();
			var url = $("#url").val();
			var num = $("#number").val();
			var numfromsevice = $('#numfromsevice').val();
			var status = $("input[name='status']:checked").val();
			var picture = $("#img1").attr('src');
			
			if(codekey==''){
				$.messager.alert('温馨提示',"关键字不能为空！",'info');
				return
			}
			if(num==''){
				$.messager.alert('温馨提示',"序号不能为空！",'info');
				return
			}
			if(numfromsevice!=0){
				if(num-numfromsevice>0){
				$.messager.alert('温馨提示',"只允许输入1到"+numfromsevice+"之间的序号！",'info');
				return
				}
			}
			if(status=='undefined'){
				$.messager.alert('温馨提示',"请选择是否启用！",'info');
				return
			}
			if(picture==''){
				$.messager.alert('温馨提示',"请选择图片！",'info');
				return
			}
			
			var codetest = new RegExp("^[A-Za-z0-9\u4e00-\u9fa5]+$");
			if(!codetest.test(codekey)){
				
				$.messager.alert('温馨提示',"只允许输入中文英文和数字！",'info');
				return
			}
			var strRegex = '^((https|http|ftp|rtsp|mms)?://)'
				+ '?(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@ 
				+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184 
				+ '|' // 允许IP和DOMAIN（域名） 
				+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www. 
				+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名 
				+ '[a-z]{2,6})' // first level domain- .com or .museum 
				+ '(:[0-9]{1,4})?' // 端口- :80 
				+ '((/?)|' // a slash isn't required if there is no file name 
				+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$'; 
				var urltest=new RegExp(strRegex); 
			if(url!=''){
				debugger
				if(url.indexOf("TTBankCardVC")==-1&&url.indexOf("BankCardAdminActivity")==-1){
					if(!urltest.test(url)){
						$.messager.alert('温馨提示',"url格式不对！",'info');
						return
					}
				}
			}
			
			var numtest = new RegExp("^[0-9]+$");
			if(!numtest.test(num)){
				$.messager.alert('温馨提示',"只允许输入数字！",'info');
				return
			}
 			 $.ajax({
 		        type: "post",
 		        url: "${ctx}/manage/banner/creatPicture",
 		        data: form,
                 processData:false,
                 contentType:false,
 		        success: function(result){
 					if (result.status) {
 						ids=[];
 						$('#img1').attr('src','');
 						$('#edit_fm').form('clear');
 						$('#edit_dlg').dialog('close');
 						$('#banner_List').datagrid('reload');
 					} else {
 						$('#img1').attr('src','');
 						$('#edit_fm').form('clear');
 						$('#edit_dlg').dialog('close');
 						$('#banner_List').datagrid('reload');
 						$.messager.show({
 							title : '提示',
 							msg : result.data
 						});
						
 					}
 		        }
 		    });
					
		}
//-------------------取消提交
function clearqx(){
	$('#edit_fm').form('clear');
	$('#img1').attr('src','');
	$('#edit_dlg').dialog('close');
}

//-------------------图片预览
		function imgPreview(fileDom){
		    //判断是否支持FileReader
		    if (window.FileReader) {
		        var reader = new FileReader();
		    } else {
		        alert("您的设备不支持图片预览功能，如需该功能请升级您的设备！");
		    }
		
		    //获取文件
		    var file = fileDom.files[0];
		    var imageType = /^image\//;
		    //是否是图片
		    if (!imageType.test(file.type)) {
		        alert("请选择图片！");
		        return;
		    }
		    //读取完成
		    reader.onload = function(e) {
		        //获取图片dom
		        var img = document.getElementById("img1");
		        //图片路径设置为读取的图片
		        img.src = e.target.result;
		    };
		    reader.readAsDataURL(file);
		}
 //-------------清空查询条件
 function clearConditon(){
	 $("#querybanner").form('clear');
 }
//--------------------------------新增图片
function Createpicture(){
	$('#edit_dlg').dialog({
	 	title: '新增图片',
	    modal: true
	});
	$("#yes").attr("checked","checked");
	$('#img1').attr('src','');
	$("#yes").attr("checked","checked");
	//ajax获取最大序号
	$.ajax({
 		        type: "post",
 		        url: "${ctx}/manage/banner/selectNumber",
 		        data: {
 		        	
 		        },
 		       dataType: "json",
 		        success: function(result){
 					if (result.status) {
 						$("#yes").attr("checked","checked");
 						$('#number').val(parseInt(result.rows[0].num)+1);
 						$('#numfromsevice').val(parseInt(result.rows[0].num)+1);
 					} else {
 						$.messager.show({
 							title : '提示',
 							msg : result.data
 						});
						
 					}
 		        }
 		    });
	$('#edit_dlg').dialog('open');	
	$('#edit_fm').form('clear');
}
//--------------------------修改图片
function Deletepicture(){
	//ajax获取最大序号
	$.ajax({
 		        type: "post",
 		        url: "${ctx}/manage/banner/selectNumber",
 		        data: {
 		        	
 		        },
 		       dataType: "json",
 		        success: function(result){
 					if (result.status) {
 						$('#numfromsevice').val(parseInt(result.rows[0].num));
 					} else {
 						
 						$.messager.show({
 							title : '提示',
 							msg : result.data
 						});
						
 					}
 		        }
 		    });
	var row = $('#banner_List').datagrid('getSelected');
	if (row){
		$("#id").val(row.id);
		$("#codekeys").val(row.codekey);
		$("#url").val(row.url);
		$("#number").val(row.num);
		if(row.status==1){
			$("#yes").attr("checked","checked");
		}else{
			$("#no").attr("checked","checked");
		}
		$('#img1').attr('src',row.picture);
		
		$('#edit_dlg').dialog({
		 	title: '新增图片',
		    modal: true
		});
		$('#edit_dlg').dialog('open');	
     }else{
    	 $.messager.alert('温馨提示',"请选择一条数据",'info');
     }	
	
}
//--------------------------------------------格式化日期字段--------------------------------------------
function formatterdate(val, row) {
	if(val==null||val==''||val==undefined){
		return "";
	}else{
		var date = new Date(val);
		return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
	}
}

function formatterStatue(val, row){
	if(row.status==1){
		return "是";
	}else{
		return "否";
	}
}
//-------------表格图片预览
function imgFormatter(val,row){
	if(val){
		return '<img id="imgs" src='+val+' style=width:98px;height:58px;>';
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
	<table id="banner_List" class="easyui-datagrid" striped="true"  toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true" fitColumns="true" fit="true" style="overflow-x:hidden"
	pageSize="15"  pageList=[15,30,45,60] 
	 data-options="url :'${ctx}/manage/banner/bannerPage',
	 			view: myview,
		        emptyMsg: '暂无数据',
		onDblClickRow : function(rowIndex, rowData) {
		},
		onSelect : function() {
		}" >
		<thead>
			<tr>
			    
			    <th field="id" sortable="true" hidden="hidden" formatter : controllIfSelected sortable="true">ID</th>
			    <th field="picture"  align="center" data-options="formatter:imgFormatter">缩略图</th>
				<th field="codekey" width ="30" align="center">关键字</th>
				<th field="url"  width ="30" align="center">URL链接地址</th>
				<th field="creatTime"  width ="30" formatter="formatterdate" align="center">上传时间</th>
				<th field="num"  width ="30" align="center">序号</th>
				<th field="status"  width ="30" formatter="formatterStatue" align="center">是否启用</th>
			</tr>
		</thead>
	</table>
	<div id="tb" style="padding: 5px; height: auto">
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
		<span class="datagrid-btn-separator"></span>
			<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onClick="Createpicture()">新增图片</a>
		<span class="datagrid-btn-separator"></span>
			<a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true" onClick="Deletepicture()">修改图片</a>
		</div>
		<div style="padding: 5px; border-bottom:1px solid #f2f3f5">
			<form id="querybanner" method="post">
				<table border="0" width="100%" style="margin-left: 20px;">
					<tr>
						<td><label class="bitian">关键字     ：</label>
							<input id="codekey"  type="text" style="margin-left: 19.3px;"/> 
						</td>
						<td><label class="bitian">序          号：</label>
							<input id="num"   type="text" onkeyup="value=value.replace(/[^\d]/g,'')" style="margin-left: 29px;"/> 					
						</td>
					</tr>
					<tr>
						<td><label class="bitian">上传时间起：</label>
							<input  class="easyui-datebox " data-options="editable:false" readonly='readonly' id="startDate" name = "startDate" />					
						</td>
						<td><label class="bitian">上传时间止：</label>
							<input  class="easyui-datebox " data-options="editable:false" readonly='readonly' id="endDate" name = "endDate"/>					
						</td>
						<td>
							<span class="datagrid-btn-separator"></span>	
							<a href="#" id="searchBt" class="easyui-linkbutton" iconCls="icon-search" plain="true">查询</a>
							<a href="#" id="searchBtReload" class="easyui-linkbutton" iconCls="icon-reload" plain="true" onclick="clearConditon();">清空</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<!-- 新建图片 -->
	<div  id="edit_dlg" class="easyui-dialog" style="text-align:center;width:500px; " closed="true" buttons="#${entityName}_dlg-buttons" >
			<form id="edit_fm" method="post" enctype="multipart/form-data">
			 <table  style="width:100% ; border-spacing:0px 10px;" class="m_table">
			 	<input name="id" id="id" type="hidden" />
			 	<input id="numfromsevice" type="hidden">
			   <tr>
	      			<td><label class="bitian">关键字：</label></td>
	      			<td  align="left">
						<input type="text" id="codekeys" name="codekey" style="width:200px" class="easyui-validatebox" size="49"   required="true"/>
					</td>
			   </tr>
			     <tr>
	      			<td><label class="bitian">URL地址连接：</label></td>
	      			<td  align="left">
						<input type="text" id="url" name="url" style="width:200px"  size="49"   required="true"/>
					</td>
			   </tr>
			     <tr>
	      			<td>
	      				<label class="bitian">序号：</label>
	      			</td>
	      			<td  align="left">
						<input type="text" id="number" name="num" style="width:200px" class="easyui-validatebox" size="49"   required="true"/>
					</td>
			   </tr>
			   <tr>
	      			<td><label class="bitian">上传图片：</label></td>
	      			<td align="left" >
					<input type="file" id="picture" name="imgfile" style="width:200px"   size="49"  onchange="imgPreview(this)" accept="image/png,image/jpeg,image/gif"  required="required"/>	 
					</td>
			   </tr>
			   <tr>
       				<td id="imgtd"><img id="img1" src="" style="width:180px"/></td>
       		   </tr> 
			   <tr>
	      			<td>
	      				<label class="bitian">是否启用：</label>
	      			</td>
	      			<td  align="left">
						<input  id="yes" name="status" type="radio"  value="1" checked/>是
						<input  id="no"  name="status" type="radio"  value="0"/>否
					</td>
			   </tr>
			</table>
			</form>
		</div>
		<div id="${entityName}_dlg-buttons" style="text-align:center;width:500px; ">
			<a href="#" id="submit" class="easyui-linkbutton" iconCls="icon-ok" onclick="savemessage();">确定</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="clearqx();">取消</a>
		</div>
</body>

