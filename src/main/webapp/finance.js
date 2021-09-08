$(document).ready(function() {
	loadDropdown();
	initvars();
 $(document).keyup(function(event) {
	var key=event.which; if(event.ctrlKey)key+=1000;if(event.altKey)key+=2000; $("#debug").html(key);
	if(mode=="input"){
		switch(key){
		case 27:setMode("cmd");$("#bi").focus();break;
		case 13:inputFocus(1);break;
		case 1013:submitInsert();break;
		case 1008:inputFocus(0);break;
		}
	}else if(mode=="edit"){
		switch(key){
		case 27:self.html(val);setMode("cmd");break;
		case 13:change();break;
		}
	}else{
		switch(key){
		case 82: location.replace('Report.html');
		case 13:inputFocus(1);setMode("input");break;
		case 1013: submitUpdate();break;
		case 66:submitQuery('balance');break;
		//case 82:$("#debug").html($("#debug").html()+"R");submitQuery('report');break;
		case 73: submitQuery('tran');break;
		case 48,49,50,51,52,53,54,55,56,57,187,189:
			submitMonth(key);
		}
	}
 });
});

function submitMonth(key){
	var m=key==187?12:key==189?11:key==48?10:key-48;
	var d=new Date();var y=d.getYear();var dd=d.getDay();
	var d1=new Date(y,m,dd); var d2=d1.getTime()-86400000*40;
	var where=' ' + cond + ' between \''+d2.format('yyyymmdd')+'\' and \'' + d1.format('yyyymmdd')+'\'';
	$("#where").html(where);
	
}

curDate=new Date().format('yyyymmdd');cond='issueDate';
function initvars(){
	var d1=new Date(new Date().getTime()-60*86400*1000).format('yyyymmdd');
	$("#where").html(" issueDate between '"+d1+"' and '"+curDate+"'");
	$("#inputIssueDate").val(curDate);
	$("#inputTradeDate").val(curDate);
}

var fundsdd=[];
function loadDropdown(){
	$.getJSON('finance/query?type=funds', function(data) {
		fundsdd=data;
		setAutoComplete($("#iaa"));setAutoComplete($("#ibb"));
		$("input[type!='hidden']",$("#inputf")).focus(function(){
			$(this).select();
		});
		});	
}

var val;
var self;
var sql="";
var mode="cmd";
var order='issueDate';var desc=true;
function setClickable(){
		$("#fdata").click(function(event){
			if($(event.target).is("th")) {//closest("tr").index()==0
				order=$(event.target).html();order=order=="famount"?"famount*cur":order;desc=!desc;
				submitQuery('tran');
				return;
			}
			if(!$(event.target).is("td")&&!$(event.target).parent().is("td")) return;
			var target=$(event.target).closest("td");
			if(mode=="edit"&&target.index()==self.index()&&target.parent().index()==self.parent().index()) return;
			if(mode=="edit") self.html(val);
			
			self=$(event.target);
			val=self.html();
			setMode("edit");
			self.html('<input style="width:'+self.width()+';height:'+self.height()+'" value="'+val+'" />');
			if(self.index()==1||self.index()==2) setAutoComplete2(self);
			$("input",self).focus();
		});
		$("#inputf input").focus(function(){
			setMode("input");
		});
		$("#inputf input").blur(function(){
			setMode("cmd");
		});
}

function change(){
	if(self.index()==1||self.index()==2) {return;self.html($("input",self).val());return;}
	var head=$("th",$("#fdata"))[self.index()].innerHTML;//[self.index()].html();
	var chval=$("input",self).val();
//	sql=sql+head+":"+chval+":"+$("td",self.parent())[0].innerHTML+";";
	var id=head=="id"?chval:$("td",self.parent())[0].innerHTML;
	if(head=="id"){//click on id:delete
		sql=sql+"delete from transaction where id="+chval+";";
	}else if(head=="ftype"){//click on ftype:installment
		sql=sql+"update transaction set "+head+"='"+chval+"' where id="+id+";";
		sql=sql+"ftype:"+chval+":"+id+":"+$("td",self.parent())[6].innerHTML+":"+$("td",self.parent())[3].innerHTML+";";
	}else
		sql=sql+"update transaction set "+head+"='"+chval+"' where id="+id+";";
	self.html(chval);
	setMode("cmd");
}

function submitUpdate(){
	if(sql=="")return;
	$.post("finance/update", {sql:sql}, function(text) {alert('update completed');
       $("#debug").html(text);
       submitQuery('tran');
     },"text");
}

function submitInsert(){
	var sqlinsert="insert into transaction(ffrom,fto,famount,fcomments,issueDate,tradeDate,cur) values(";
	var c=$("input.i",$("#inputf"));
	for(var i=0;i<4;i++){
		sqlinsert=sqlinsert+"'"+c[i].value+"',";
	};
	sqlinsert=sqlinsert+"'"+(c[4].value==""?curDate:c[4].value)+"',";
	sqlinsert=sqlinsert+"'"+(c[5].value==""?curDate:c[5].value)+"',";
	sqlinsert=sqlinsert+"'"+(c[6].value==""?"1":c[6].value)+"')";
//	$("#debug").html(sqlinsert);if(true) return;
	$.post("finance/insert", {sql:sqlinsert}, function(text) {alert('insert completed');
       $("#debug").html(text);
//       if(text=="insert ok"){inputn=-1;inputFocus(0);}
       submitQuery('tran');
     },"text");
}

var inputn=-1;
function inputFocus(key){
	switch(key){
	case 0:inputn--;break;case 1:inputn++;break;
	}
	inputn=inputn%7;
	$("input[type!='hidden']",$("#inputf"))[inputn].focus();
}

function setAutoComplete(obj){//the td obj that contains two inputs
	var idobj=$("input[type='hidden']",obj);
	var lobj=$("input[type!='hidden']",obj);
	lobj.autocomplete({
		source: fundsdd,delay:0,autoFocus:true,
		select: function(event, ui) {
			var selectedObj = ui.item;
			lobj.val(selectedObj.label);
			idobj.val(selectedObj.value);
			return false;
		}
	});	
}

function setAutoComplete2(obj){//the td obj
	var lobj=$("input",obj);
	lobj.autocomplete({
		source: fundsdd,delay:0,autoFocus:true,
		select: function(event, ui) {
			var selectedObj = ui.item;
			lobj.val(selectedObj.label);
			
			var head=$("th",$("#fdata"))[self.index()].innerHTML;
//			sql=sql+head+":"+selectedObj.value+":"+$("td",self.parent())[0].innerHTML+";";
			sql=sql+"update transaction set "+head+"='"+selectedObj.value+"' where id="+$("td",self.parent())[0].innerHTML+";";
			self.html(selectedObj.label);
			setMode("cmd");
			
			return false;
		}
	});	
}

function submitQuery(type){//tran, report,

	var orderclause=' order by '+order+(desc?' desc':'');
	var where=$("#where").val();
	$.post("finance/query", {type:type,time:new Date().getTime(),sql:" where "+where+" "+orderclause}, function(text) {
		sql="";
		setMode("cmd");
		if(type=='balance'){
			$("#mf").html('<table><td><img src="chart/balance" alt="balance report" height="400" width="400" /></td>' + 
					'<td><div style="height:480px;overflow:auto">'+text+'</div></td></table>');
			$("#mf td").attr('align','right');
		}else{
			$("#mf").html(text);/*
			var head=$("#fdata tr:first-child").html();
			$("#fdata tr:first-child").remove();
			$("#fdata").html('<tr><td><table><tr>'+head+
					'</tr></table></td></tr><tr><td><div style="height:470px;overflow:auto"><table>'+
					$("#fdata").html()+'</table></div></td></tr>');*/
//			var table1=$("#fdata table:nth-child(1)");alert(table1.html());
//			var table2=$("#fdata table:nth-child(2)");alert(table2.html());
//			for(var i=1;i<9;i++){
//				var width1=$("th:nth-child("+i+")",table1).width();
//				var width2=$("td:nth-child("+i+")",table2).width();
//				if(width1>width2)
//					$("td:nth-child("+i+")",table2).attr('style','width:'+width1);
//				elseKTV with collegues(ouge)
//					$("th:nth-child("+i+")",table1).attr('style','width:'+width2);
//			}
		}
       if(type=='tran'){
	       $("td:last:contains('T')",$("#fdata tr")).parent().attr('style','color:grey');
	       if(order!='') $("th:contains("+order+")",$("#fdata tr:first")).attr('style',desc?'background:#00FF00':'background:#009900');
	       setClickable();
       }
     },"text");
};

function setMode(m){
	mode=m;$("#mode").html(m);
};
