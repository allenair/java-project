<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="../../css/sinyd-all.css" rel="stylesheet" type="text/css" />
<script language="Javascript" src="../../js/sinyd-all.js"></script>
<title></title>
</head>

<body style="padding: 0px; overflow: hidden;">  
        <div class="l-loading" style="display:block" id="pageloading"></div> 
        
        <div class="l-panel-search">
          <div class="H-layout0" style="position:relative; min-width:800px;"><!--H-layout0是一行文本框，H-layout1是两行文本框，H-layout2是三行文本框-->
	        <div style="max-width:1134px;">
				<div class="l-panel-search-item">
					searchName：<input id="searchName" type="text" size="30" />
				</div>
	        </div>
			<div class="l-panel-search-btn">
				<input  id="searchBtnId" name="searchBtn" value=" " class="btn-search" onmouseover="this.className='btn-search-over'" onmouseout="this.className='btn-search'" onmousedown="this.className='btn-search-down'" onclick="search();" type="button" />
                <input  id="resetBtnId" name="resetBtn" value=" " class="btn-reset" onmouseover="this.className='btn-reset-over'" onmouseout="this.className='btn-reset'" onmousedown="this.className='btn-reset-down'" onclick="reset();" type="button" />
			</div>
          </div>
		</div>
        
		<div id="toptoolbar"></div> 
		<div id="maingrid" style="margin: 0; padding: 0"></div>
        
        <div id="openDialogId" style="display: none;">
            <form name="form" method="post" action="#" id="formId">
            	<input id="id" type="hidden" />
              <#list columnNameList as columnName>
            	<label>${columnName}：</label> <input id="${columnName}" name = "${columnName}" type="text" size="30" class="required"/><br/>
              </#list>
                <input type="submit" value="提交" id="submitBtnId"/> 
                <input type="button" value="取消" onclick="closeOpen();"/>
                <input type="reset"  style="display: none;" id="resetButnId"/>
            </form>
        </div>
        <div id="openDialogForViewId" style="display: none;">
          <#list columnNameList as columnName>
            <label>${columnName}：</label> <span id="${columnName}_view"></span><br/>
          </#list>
            <input type="button" value="关闭" onclick="closeOpenView();"/>
        </div>

	<div style="display: none;"></div>

	<script type="text/javascript">
		var grid = null;
        var dialogObj = null;
        var dialogObjForView = null;
        
		var parms = {};
		var search = function() {
			parms["searchName"] = $("#searchName").val();
			loadGridData(true);
		};

		var loadGridData = function(firstFlag) {
			grid.set('parms', this.parms);
			if(firstFlag){
				grid.changePage('first');
			}
			grid.loadData(true);
			$("#pageloading").hide();
		};

		var reset = function(){
			$("#searchName").val('');
		};
		
		var addNewRecord = function(){
			$('#id').val('0');
		  <#list columnNameList as columnName>
            $('#${columnName}').val('');
          </#list>
            openDialog('新增',1);
		};
		
		var modifyRecord = function(){
			var row = grid.getSelectedRow();
			if(row){
				$.getJSON(global_param.context_name + "/${modualPath}/${tableLittleName}info.do",{
							id: row.id
						}, function(json){
								$('#id').val(json.id);
							  <#list columnNameList as columnName>
					            $('#${columnName}').val(json.${columnName});
					          </#list>
			                	openDialog('修改',2);
							}
				);
			}else{
				$.ligerDialog.question('请先选择一条记录！');
			}
		};
		
		var deleteRecord = function(){
			var rows = grid.getSelectedRows();
			
			var idArray = '';
			$.each(rows, function(index, row){
				idArray += row.id+'#';
			});
			
			if(idArray){
				idArray +='0';
				$.post(global_param.context_name+"/${modualPath}/${tableLittleName}delete.do",
						{ "idArray": idArray },
					   function(data){
				    	  if(data==='success'){
				    		  loadGridData(false);
				    		  $.ligerDialog.success('删除成功！');
				    	  }else{
				    		  $.ligerDialog.error('删除失败！');
				    	  }
					   });
			}else{
				$.ligerDialog.question('请至少选择一条记录！');
			}
		};
        
        var viewRecord = function(){
			var row = grid.getSelectedRow();
			if(row){
				$.getJSON(global_param.context_name + "/${modualPath}/${tableLittleName}info.do",{
							id: row.id
						}, function(json){
							  <#list columnNameList as columnName>
					            $('#${columnName}_view').html(json.${columnName});
					          </#list>
			                 	openDialog('详情',3);
							}
				);
			}else{
				$.ligerDialog.question('请先选择一条记录！');
			}
		};

		var saveRecord = function(){
			$.post(global_param.context_name+"/${modualPath}/${tableLittleName}save.do",
					{ 
					<#list columnNameList as columnName>
					  "${columnName}": $('#${columnName}').val(),
					</#list>
					  "id": $('#id').val()
				    },
				
				   function(data){
			    	  if(data==='success'){
			    		  closeOpen();
			    		  loadGridData(false);
			    		  $.ligerDialog.success('保存成功！');
			    	  }else{
			    		  $.ligerDialog.error('保存失败！');
			    	  }
				   });
        };
        
        var openDialog = function(dialogTitle,type) {
            if(type===3){
               if(dialogObjForView){
                    dialogObjForView._setTitle(dialogTitle);
                    dialogObjForView.show();
                }else{
                    dialogObjForView = $.ligerDialog.open({
                        target: $("#openDialogForViewId"),
                        title: dialogTitle || '',
                        width: 600,
                        height: 500,
                        isResize: false
                    });
                }
            }else if (type===1){
                form.clearTextClass(1);
               
                if(dialogObj){
                    dialogObj._setTitle(dialogTitle);
                    dialogObj.show();
                }else{
                    dialogObj = $.ligerDialog.open({
                        target: $("#openDialogId"),
                        title: dialogTitle || '',
                        width: 600,
                        height: 500,
                        isResize: false
                    });
                }
            }else{
                form.clearTextClass(2);
               
                if(dialogObj){
                    dialogObj._setTitle(dialogTitle);
                    dialogObj.show();
                }else{
                    dialogObj = $.ligerDialog.open({
                        target: $("#openDialogId"),
                        title: dialogTitle || '',
                        width: 600,
                        height: 500,
                        isResize: false
                    });
                }
            }
            
        };
        
        var closeOpen = function(){
          dialogObj.hide();  
        };
    
        var closeOpenView = function(){
          dialogObjForView.hide();  
        };
    
        
        
        
		$(document).ready(function() {
			$("#toptoolbar").ligerToolBar({ 
				items : [ { text: '新增', click: addNewRecord }, { line: true }, 
                          { text: '修改', click: modifyRecord }, { line: true },
                          { text: '删除', click: deleteRecord }, { line: true },
                          { text: '详情', click: viewRecord }                                   
                        ]
			});
			
			// 设置表格属性
			grid = $("#maingrid").ligerGrid({
				// 设置属性
				columns : [ 
				          <#list columnNameList as columnName>
				          	{ display: '${columnName}', name: '${columnName}', width: '10%'} <#if columnName_has_next>,</#if>
						  </#list>
                          ],
				url : global_param.context_name+'/${modualPath}/${tableLittleName}list.do',
				sortName : 'id',
				sortOrder : 'asc', //default is asc 
				width : '100%',
				height : '100%',
				pageSize : 20, //default is 10 
				rownumbers : true,
				heightDiff: -6,
				// 设置是否包含多选框
				//checkbox: true,
				
				// 设置事件
				onDblClickRow : function(data, rowindex, rowobj) {
					viewRecord();
				}
			});

			// 表格加载后，隐藏loading动画
			$("#pageloading").hide();
            
			$("#submitBtnId").click(function (){
          	  var $form = $(formId);
          	   	 if (!$form.valid()) {
          	   	 	return false;
          	   	 }
          	   	saveRecord();
          	   	return false;
            });
        });
	</script>
</body>
</html>
