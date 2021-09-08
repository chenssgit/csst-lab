var cd=0;var isYear=false;var cat='expense';var forceLoad=false;

$(document).ready(function() {
	generateQuery(100*new Date().getYear()+190001+new Date().getMonth(),isYear);
	$(document).keyup(keyHandler);
});

function keyHandler(event) {
	var key=event.which; $("#status").html(key);var month=cd%100;var y=(cd/100)>>0;
	switch(key){
	case 89:generateQuery(cd,!isYear);return;//y
	case 65:cat='asset';forceLoad=true;generateQuery(cd,isYear);return;//a
	case 69:cat='expense';forceLoad=true;generateQuery(cd,isYear);return;//e
	case 13:submitReport();return;//enter
	case 49:;case 50:;case 51:;case 52:;case 53:;case 54:;
	case 55:;case 56:;case 57:month=key-48;break;
	case 48:month=10;break;
	case 189:month=11;break;
	case 187:month=12;break;
	case 37:month=month-1<1?(y--,12):month-1;break;
	case 39:month=month+1>12?(y++,1):month+1;break;
	case 38:y=y-1;break;
	case 40:y=y+1;break;
	case 82:location.replace('finance.html');//r
	}
	generateQuery(y*100+month,isYear);
 }

function submitReport(){
	$.post("report/monthly", {type:"monthly",cat:cat=="asset"?"bar":"pie",sql:$("#query").val(),sql2:$("#query2").val(),month:isYear?(cd/100)>>0:cd}, function(text) {
		$("#title").html(isYear?(cd/100)>>0:cd);
		$("#report").html(text);
	},'text');
}

function generateQuery(date,isy){
	if(!forceLoad&&cd==date&&isYear==isy) return;cd=date;isYear=isy;forceLoad=false;var query="";var query2="";
	if(cat=="expense"){
		query="select name,sum(amount) a from vf_tran3_m where cmonth like '"+(isYear?(cd/100)>>0:cd)+"%' and category like '%expense' group by name order by a desc";
		query2="SELECT left(fto,char_length(fto)-16) as fto,famount,fcomments,tradeDate,ffrom,ftype FROM v_tran3 v where tradeDate like '"+(isYear?(cd/100)>>0:cd)+"%' and (fto like '%expense' or ffrom like '%expense') order by famount desc";
	}else if(cat=="asset"){
		query="select name as asset,sum(amount) as amount from vf_tran3_m where cmonth like '"+(isYear?(cd/100)>>0:cd)+"%' and category like '%asset' group by asset order by amount desc";
		query2="SELECT left(fto,char_length(fto)-16) as fto,famount,fcomments,tradeDate,ffrom,ftype FROM v_tran3 v where tradeDate like '"+(isYear?(cd/100)>>0:cd)+"%' and (fto like '%asset' or ffrom like '%asset') order by famount desc";
	}
	$("#query").html(query);$("#query2").html(query2);
	submitReport();
}