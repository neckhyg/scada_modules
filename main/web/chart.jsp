<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@page import="javax.servlet.http.HttpServletRequest"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>图表控件</title>

<script type="text/javascript" src="/modules/main/web/js/jquery.min.js"></script>
<script type="text/javascript" src="/modules/main/web/js/highcharts.js"></script>
<script type="text/javascript" src="/modules/main/web/js/exporting.js"></script>
<script type='text/javascript' src='/dwr/interface/WatchListDwr.js'></script>
<script type='text/javascript' src='/dwr/engine.js'></script>
<script type='text/javascript' src='/dwr/util.js'></script>
<script type="text/javascript">
var pointlist="<%=request.getParameter("list")==null?"":request.getParameter("list")%>"
var form="<%=request.getParameter("from")==null?0:request.getParameter("from")%>";
var to="<%=request.getParameter("to")==null?0:request.getParameter("to")%>";

//var timearray=new Array();
		
function getWatchListData(list)//根据用户选择的站点列表， 获取监测点。根据监测点获取当天所有数据信息。
{  
	var dataarray=new Array();
	  WatchListDwr.getPointByWatchList(function(data){
        for (var i=0; i<data.length; i++) {
      		var id=data[i].id;//点位id;
      		var name=data[i].deviceName+"-"+data[i].name;
      		var xid=data[i].xid;
      		var type=xid.substr(8,1);
      		var y="";
      		if(type==1)
      			y="";
      		else if(type==2)
      			y=1;
      		else
      			y=2;
      		if(list!="" && list.indexOf(id)!=-1)
      			{ 
      				DWREngine.setAsync(false);  
	                WatchListDwr.getPointValueByPointId(id,form,to,function(respons){
	                	var temparray=new Array();
	                	var obj=eval(respons.value);
	                	if(obj!=null)
	                		{
			                    var num=obj.length
			                	for(var j=0;j<num;j++)
			                		{
			                        	var sitevalue=obj[j].value;
			                			if(sitevalue.indexOf("mg/L")!=-1)
			                			{
			                				sitevalue=sitevalue.substr(0,sitevalue.length-4);
			                			}
				                		var timespan=obj[j].time;
			                        	temparray.push("["+timespan,parseFloat(sitevalue)+"]");
			                		}
			                	if(y=="")
			                		{
			                		datas="{name:'"+name+"',type:'spline',data:["+temparray+"]}";
			                		}
			                	else
			                		{
			                		datas="{name:'"+name+"',type:'spline',yAxis:"+y+",data:["+temparray+"]}";
			                		}
			                	dataarray.push(datas);
			                	DWREngine.setAsync(true); 
	                		}
	                	else
	                		{
	                		alert("无数据");
	                		return ;
	                		}
	                })
	              
      			}
        }
        showChart("["+dataarray+"]")
	  }); 
}
	
getWatchListData(pointlist);

function isInArray(str)
{
	for(var i=0;i<timearray.length;i++)
		{
		if(str==timearray[i])
			return true;
		}
	return false;
}


//图形控件
var chart;
function showChart(datalist)
{
	var obj=eval(datalist);
	var array=new Array();
	for(var i=0;i<obj.length;i++)
		{
			array.push(obj[i]);
		}
	
	
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});
chart = new Highcharts.Chart({
 		chart: {
 			renderTo: 'container',
 			zoomType: 'xy'
 		},
 		
 		title: {
 			text: '监测点数据'
 		},
 		subtitle: {
 			//text: '22'
 		},
 		xAxis: {
 		    type:'datetime',
			dateTimeLabelFormats: {
				second:'%H:%M:%S',
				minute:'%H:%M',
				hour:'%H:%M'
			}
 		},
 		yAxis: [{ // Primary yAxis
 			labels: {
 				formatter: function() {
 					return this.value+'';
 				},
 				style: {
 					color: '#89A54E'
 				}
 			},
 			title: {
 				text: 'PH',
 				style: {
 					color: '#89A54E'
 				}
 			},
 			lineColor: '#89A54E',
            lineWidth: 1
 			
 		}, { // Secondary yAxis
 			gridLineWidth: 0,
 			title: {
 				text: 'COD',
 				style: {
 					color: '#4572A7'
 				}
 			},
 			opposite: true,
 			labels: {
 				formatter: function() {
 					return this.value+'mg/L';
 				},
 				style: {
 					color: '#4572A7'
 				}
 			},
 			lineColor: '#4572A7',
            lineWidth: 1
 			
 		}, { // Tertiary yAxis
 			gridLineWidth: 0,
 			title: {
 				text: '氨氮',
 				style: {
 					color: '#AA4643'
 				}
 			},
 			labels: {
 				formatter: function() {
 					return this.value+'mg/L';
 				},
 				style: {
 					color: '#AA4643'
 				}
 			},
 			opposite: true,
 			lineColor: '#AA4643',
            lineWidth: 1
 		}],
 		tooltip: {
 			formatter: function() {
 				var unit = {
 					'PH': '',
 					'COD': 'mg/L',
 					'氨氮': 'mg/L'
 				};
 				
 				return [this.series.name]+'    '+
 				Highcharts.dateFormat('时间 %H:%M',this.x) +' 数值 '+ this.y ;
 			}
 		},
 		legend: {
 			layout: 'vertical',
 			align: 'left',
 			x: 120,
 			verticalAlign: 'top',
 			y: 80,
 			floating: true,
 			backgroundColor: '#FFFFFF'
 		},
 		series:array	
 	});

}
 	
</script>
</head>
<body>
	<div id="container" style="width: 100%; height: 400px; margin: 0 auto"></div>
	<div id='data' style="display:none"></div>
</body>
</html>
