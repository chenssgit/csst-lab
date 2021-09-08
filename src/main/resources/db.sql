

--v_tran2:
select t.id,t.ffrom,t.fto,s.famount,t.cur,t.issueDate,s.tradeDate,t.fcomments,if(ftype is null,"T",concat('T:',ftype)) as ftype
 from transaction t,subtran s where t.id=s.id
union all

select t.id,t.ffrom,t.fto,t.famount,t.cur,t.issueDate,t.tradeDate,t.fcomments,if(ftype is null,"T",concat('T:',ftype)) as ftype
 from transaction t where t.issueDate!=t.tradeDate
union all
select t.id,t.ffrom,t.fto,t.famount,t.cur,t.issueDate,t.tradeDate,t.fcomments,if(ftype is null,"",t.ftype) as ftype
from transaction t

--v_tran3:
select t.id,concat(f.name,'-',f.category) as ffrom,concat(f2.name,'-',f2.category) as fto,t.famount,t.cur,t.issueDate,t.tradeDate,t.fcomments,t.ftype from v_tran2 t,funds f,funds f2 where t.ffrom=f.id and t.fto=f2.id;


--vf_tran
select t.id,ffrom,fto,
if(s.id is null,t.tradeDate,s.tradeDate) as tradeDate,
if(s.id is null,t.famount,s.famount)*if(t.cur is null,1,t.cur) as famount
 from transaction t left join subtran s on t.id=s.id


--vf_tran2:
select id,ffrom as fid,tradeDate,-famount as famount from vf_tran
union all
select id,fto as fid,tradeDate,famount from vf_tran


--vf_tran3:
select t.id,
t.fid,f.name,f.category,
tradeDate,famount
 from vf_tran2 t,funds f
where t.fid=f.id


--vf_balance:
select f.name,f.category,sum(if(t.famount is null,0,t.famount))+f.init as famount
 from funds f left join vf_tran2 t on f.id=t.fid
group by 1,2

--vf_tran3-m:
CREATE OR REPLACE VIEW `finance`.`vf_tran3-m` AS  
select t.fid,f.category,f.name,substr(tradeDate,1,6) as cmonth,sum(famount) as amount from
vf_tran2 t, funds f where t.fid=f.id 
group by fid,cmonth 

--vf_balance_t
SELECT if(category like '%asset',famount,0) as asset,
if(category like '%expense',famount,0) as expense,
if(category like '%liability',famount,0) as liability,
if(category like '%-SE',famount,0) as SE,
if(category like '%revenue',famount,0) as revenue,
tradeDate FROM vf_tran3 v 
