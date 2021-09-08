var cd=0;var isYear=false;var cat='expense';var forceLoad=false;
var chartType={asset:'bar',expense:'pie',liability:'bar',revenue:'bar'};
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
	case 76:cat='liability';forceLoad=true;generateQuery(cd,isYear);return;//l
	case 73:cat='revenue';forceLoad=true;generateQuery(cd,isYear);return;//i
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

function submitReport(query1, query2){
	$.post("report/monthly", {type:"monthly",cat:chartType[cat],sql:query1,sql2:query2,month:isYear?(cd/100)>>0:cd}, function(text) {
		$("#title").html(isYear?(cd/100)>>0:cd+"&nbsp&nbsp"+cat);
		$("#report").html(text);
		$("#ttable").tablesorter(); 
	},'text');
}

function generateQuery(date,isy){
	if(!forceLoad&&cd==date&&isYear==isy) return;cd=date;isYear=isy;forceLoad=false;
	var query1="select name as "+cat+",sum(amount) as amount from vf_tran3_m where cmonth like '"+(isYear?(cd/100)>>0:cd)+"%' and category like '%"+cat+"' group by "+cat+" order by amount desc";
	var query2="SELECT fto,famount,fcomments,tradeDate,ffrom,ftype FROM v_tran3 v where tradeDate like '"+(isYear?(cd/100)>>0:cd)+"%' and (fto like '%"+cat+"' or ffrom like '%"+cat+"') order by famount desc";
	submitReport(query1, query2);
}