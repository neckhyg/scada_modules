<%--
--%><%@page import="com.serotonin.m2m2.Constants"%><%--
--%><%@page import="com.serotonin.m2m2.Common"%><%--
--%><%@page import="com.serotonin.m2m2.view.ShareUser"%><%--
--%><%@page pageEncoding="UTF-8"%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><%@ taglib prefix="main" tagdir="/WEB-INF/tags/main" %>

<main:eazyPage dwr="HistoryDwr" js="${modulePath}/web/js/view.js">
  <jsp:attribute name="styles">
    <style>
body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;}
#l-map{height:100%;width:78%;float:left;border-right:2px solid #bcbcbc;}
#r-result{height:100%;width:20%;float:left;}
    </style>
  </jsp:attribute>
  

  <jsp:body>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=1.4"></script>
    <script type="text/javascript">

var updateValues = function(){
  getLastestValues();
setTimeout("updateValues()",60000);
};

var startPoll = function(){
  getLastestValues();
setTimeout("updateValues()",1000);
};

dojo.addOnLoad(startPoll);

var jingxi = "荆溪北路<br>";
var jingyi = "荆邑北路<br>";
var jinlan = "金兰泵站<br>";

function getLastestValues(){
  var list = new Array("DP_JX0001","DP_JX0002","DP_JX0003",
      "DP_JY0001","DP_JY0002","DP_JY0003",
      "DP_JL0001","DP_JL0002"); 
  HistoryDwr.getHistoryValues(list,1,function(response){
      var data = response.data.histories;
      jingxi = "<a href=\"monitoring_data.shtm\" style=\"text-decoration:none\">荆溪北路<br>";
      jingyi = "<a href=\"monitoring_data.shtm\" style=\"text-decoration:none\">荆邑北路<br>";
      jinlan = "<a href=\"monitoring_data.shtm\" style=\"text-decoration:none\">金兰泵站<br>";
      if(data && data.length !=0){
        jingyi += "pH: " +  data["DP_JY0001"][0].value + "<br>";
        jingyi += "COD: " +  data["DP_JY0002"][0].value + "<br>";
        jingyi += "氨氮: " +  data["DP_JY0003"][0].value +"</a>";
        jingxi += "pH: " +  data["DP_JX0001"][0].value + "<br>";
        jingxi += "COD: " +  data["DP_JX0002"][0].value + "<br>";
        jingxi += "氨氮: " +  data["DP_JX0003"][0].value +"</a>";
        jinlan += "pH: " +  data["DP_JL0001"][0].value + "<br>";
        jinlan += "COD: " +  data["DP_JL0002"][0].value + "</a>";
      }
      labelA.setContent(jingyi);
      labelB.setContent(jingxi);
      labelC.setContent(jinlan);

  });

}
</script>

<div id="allmap"></div>

  </jsp:body>
</main:eazyPage>

<script type="text/javascript">

var tileLayer = new BMap.TileLayer();
tileLayer.getTilesUrl = function(tileCoord, zoom) {
    var x = tileCoord.x;
    var y = tileCoord.y;
    return '${modulePath}/web/images/tiles/' + zoom + '/tile' + x + '_' + y + '.png';
}

var map = new BMap.Map("allmap");            // 创建Map实例
map.addTileLayer(tileLayer);
map.centerAndZoom("宜兴", 13);
map.setMinZoom(12);
map.setMaxZoom(14);
map.addControl(new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL}));

var pointJinSanJiao = new BMap.Point(119.81557,31.390758);
var pointWangPoQiao = new BMap.Point(119.886379,31.372392);
var pointTaiHuGuangChang = new BMap.Point(119.910571,31.362633);
var pointCangPu = new BMap.Point(119.853591,31.35423);
var pointChengDong = new BMap.Point(119.849396,31.378797);
var pointJiaoYuXiHu = new BMap.Point(119.825645,31.35662);
var pointJingXiQiao = new BMap.Point(119.832203,31.363042);
var pointHuanKeYuan = new BMap.Point(119.814766,31.362232);
var pointFuFeng = new BMap.Point(119.947527,31.444856);
var pointLongYan = new BMap.Point(119.941526,31.420145);
var pointGaoYao        = new BMap.Point(119.832036,31.449685);
var pointWenZhuangQiao = new BMap.Point(119.856264,31.416077); 
var pointMeiJiaDu      = new BMap.Point(119.785899,31.460759);
var pointFanDao        = new BMap.Point(119.763136,31.458048);
var pointXinJie        = new BMap.Point(119.764384,31.37309);
var pointKaiFaQu = new BMap.Point(119.876353,31.419408);
var pointXuDu = new BMap.Point(119.996255,31.411681);
var pointPengGan = new BMap.Point(119.986598,31.426742);

var markerJinSanJiao = new BMap.Marker(pointJinSanJiao);
var markerWangPoQiao = new BMap.Marker(pointWangPoQiao);
var markerTaiHuGuangChang = new BMap.Marker(pointTaiHuGuangChang);
var markerCangPu  = new BMap.Marker(pointCangPu);
var markerChengDong  = new BMap.Marker(pointChengDong);
var markerJiaoYuXiHu = new BMap.Marker(pointJiaoYuXiHu);
var markerJingXiQiao = new BMap.Marker(pointJingXiQiao);
var markerHuanKeYuan = new BMap.Marker(pointHuanKeYuan);
var markerFuFeng  = new BMap.Marker(pointFuFeng);
var markerLongYan  = new BMap.Marker(pointLongYan);
var markerGaoYao = new BMap.Marker(pointGaoYao);
var markerWenZhuangQiao= new BMap.Marker(pointWenZhuangQiao);
var markerMeiJiaDu = new BMap.Marker(pointMeiJiaDu);
var markerFanDao = new BMap.Marker(pointFanDao);
var markerXinJie = new BMap.Marker(pointXinJie);
var markerKaiFaQu = new BMap.Marker(pointKaiFaQu);

var labelJinSanJiao = new BMap.Label("金三角泵站",{offset:new BMap.Size(-65,10)});
var labelWangPoQiao = new BMap.Label("王婆桥泵站",{offset:new BMap.Size(20,10)});
var labelTaiHuGuangChang = new BMap.Label("太湖广场泵站",{offset:new BMap.Size(20,10)});
var labelCangPu  = new BMap.Label("沧浦泵站",{offset:new BMap.Size(20,10)});
var labelChengDong  = new BMap.Label("城东泵站",{offset:new BMap.Size(20,10)});
var labelJiaoYuXiHu = new BMap.Label("教育西路泵站",{offset:new BMap.Size(20,10)});
var labelJingXiQiao = new BMap.Label("荆溪桥泵站",{offset:new BMap.Size(20,10)});
var labelHuanKeYuan = new BMap.Label("环科园泵站",{offset:new BMap.Size(-65,10)});
var labelFuFeng  = new BMap.Label("扶风泵站",{offset:new BMap.Size(20,10)});
var labelLongYan  = new BMap.Label("龙眼泵站",{offset:new BMap.Size(20,10)});
var labelGaoYao = new BMap.Label("高遥泵站",{offset: new BMap.Size(20,10)});
var labelWenZhuangQiao = new BMap.Label("文庄桥泵站",{offset: new BMap.Size(20,10)});
var labelMeiJiaDu = new BMap.Label("梅家渎泵站",{offset: new BMap.Size(20,10)});
var labelFanDao = new BMap.Label("范道泵站",{offset: new BMap.Size(20,10)});
var labelXinJie = new BMap.Label("新街泵站",{offset: new BMap.Size(20,10)});
var labelKaiFaQu = new BMap.Label("开发区临时泵站",{offset: new BMap.Size(20,10)});

markerJinSanJiao.setLabel(labelJinSanJiao); 
markerWangPoQiao.setLabel(labelWangPoQiao);
markerTaiHuGuangChang.setLabel(labelTaiHuGuangChang);
markerCangPu.setLabel(labelCangPu);
markerChengDong.setLabel(labelChengDong);
markerJiaoYuXiHu.setLabel(labelJiaoYuXiHu);
markerJingXiQiao.setLabel(labelJingXiQiao);
markerHuanKeYuan.setLabel(labelHuanKeYuan);
markerFuFeng.setLabel(labelFuFeng);
markerLongYan.setLabel(labelLongYan);
markerGaoYao.setLabel(labelGaoYao);
markerWenZhuangQiao.setLabel(labelWenZhuangQiao);
markerMeiJiaDu.setLabel(labelMeiJiaDu);
markerFanDao.setLabel(labelFanDao);
markerXinJie.setLabel(labelXinJie);
markerKaiFaQu.setLabel(labelKaiFaQu);

var pointA = new BMap.Point(119.859393,31.391947); //荆邑北路
var markerA = new BMap.Marker(pointA);  // 创建标注
map.addOverlay(markerA);              // 将标注添加到地图中
var labelA = new BMap.Label("<a href=\"monitoring_data.shtm\" style=\"text-decoration:none\">荆邑北路</a>",{offset:new BMap.Size(20,0)});
markerA.setLabel(labelA);
markerA.addEventListener("dblclick",function(e){
    map.centerAndZoom(e.point,map.getZoom()+2);
});


var pointB = new BMap.Point(119.849997,31.398513); //荆溪北路
var markerB = new BMap.Marker(pointB);  // 创建标注
map.addOverlay(markerB);              // 将标注添加到地图中
var labelB = new BMap.Label("<a href=\"monitoring_data.shtm\" style=\"text-decoration:none\">荆溪北路</a>",{offset:new BMap.Size(-101,0)});
markerB.setLabel(labelB);
markerB.addEventListener("dblclick",function(e){
    map.centerAndZoom(e.point,map.getZoom()+2);
});

var pointC = new BMap.Point(119.927422,31.427147); //金兰泵站
var markerC = new BMap.Marker(pointC);  // 创建标注
map.addOverlay(markerC);              // 将标注添加到地图中
var labelC = new BMap.Label("<a href=\"monitoring_data.shtm\" style=\"text-decoration:none\">金兰泵站</a>",{offset:new BMap.Size(20,-10)});
markerC.setLabel(labelC);
markerC.addEventListener("dblclick",function(e){
    map.centerAndZoom(e.point,map.getZoom()+2);
});

map.addOverlay(markerJinSanJiao);
map.addOverlay(markerWangPoQiao);
map.addOverlay(markerTaiHuGuangChang);
map.addOverlay(markerCangPu);
map.addOverlay(markerChengDong);
map.addOverlay(markerJiaoYuXiHu);
map.addOverlay(markerJingXiQiao);
map.addOverlay(markerHuanKeYuan);
map.addOverlay(markerFuFeng);
map.addOverlay(markerLongYan);
map.addOverlay(markerGaoYao);
map.addOverlay(markerWenZhuangQiao);
map.addOverlay(markerMeiJiaDu);
map.addOverlay(markerFanDao);
map.addOverlay(markerXinJie);
map.addOverlay(markerKaiFaQu);

map.enableScrollWheelZoom();    //启用滚轮放大缩小，默认禁用
map.enableContinuousZoom();    //启用地图惯性拖拽，默认禁用
</script>
<style>
.BMap_cpyCtrl,.anchorBL{
display:none;
}
</style>





