<%@page import="com.util.IncConRecord"%>
<%@page import="com.service.Impl.IncidentServiceImpl"%>
<%@page language="java" contentType="text/html; charset=utf-8"%>
<%
IncConRecord incident = null;
String basePath = request.getContextPath();
try {
	incident = (IncConRecord)request.getAttribute("IncidentDetail");
} catch (Exception e) {
	e.printStackTrace();
}
if (incident != null) {
%>
<html>
<head>
<title>Incident detail</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="initial-scale=1.0">
<link rel="stylesheet" type="text/css"
	href="http://www.jeasyui.com/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="http://www.jeasyui.com/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css"
	href="http://www.jeasyui.com/easyui/themes/color.css">
<link rel="stylesheet" type="text/css"
	href="http://www.jeasyui.com/easyui/demo/demo.css">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.6.min.js"></script>
<script type="text/javascript"
	src="http://www.jeasyui.com/easyui/jquery.easyui.min.js"></script>
<style>
/* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
#infoArea {
/* 	height: 20% */
}
/* Optional: Makes the sample page fill the window. */
html,body {
	height: 100%;
	margin: 0;
	padding: 0;
    overflow-x : hidden;
    overflow-y : hidden;
}

#map{height: 80%;width:100%}
</style>
</head>
<body>
	<div id="infoArea">
		<table class="contentTableNoBoard">
			<tr>
				<td><a href="#" id="main_search" class="easyui-linkbutton"><b>Main</b></a></td>
				<td><input type="text" style="width: 180px" id="main_st"
					value="${IncidentDetail.mainSt != null ? IncidentDetail.mainSt : ''}"></td>
				<td><input type="text" style="width: 30px" id="main_dir"
					value="<%=incident.getMainDir()%>"></td>
				<td><a href="#" id="from_search" class="easyui-linkbutton"><b>From</b></a></td>
				<td><input type="text" style="width: 180px" id="cross_from"
					value="<%=incident.getFromSt()%>"></td>
				<td><a href="#" id="to_search" class="easyui-linkbutton"><b>To</b></a></td>
				<td><input type="text" style="width: 180px" id="cross_to"
					value="<%=incident.getToSt()%>"></td>
				<td><a href="#" id="geocodingBtn" class="easyui-linkbutton">Geocoding</a>
			</tr>
		</table>
	</div>


<%-- 	<iframe src="<%=basePath%>/GoogleApiTest1.html" width="100%" height="80%"></iframe>	 --%>
	<div id="map"></div>
	<div id="rclickMenu" class="easyui-menu">
		<div onclick="javascript:findLinksByLatLng();">Find nearby link</div>
		<div onclick="javascript:$('#boundaryDialog').dialog('open');">Market Boundary</div>
	</div>
	<div id="boundaryDialog" class="easyui-dialog" title="Market Boundary" style="width:600px;height:320px;padding:10px" data-options="closed:true">
	</div>	 
	<script type="text/javascript">
	    var appPath = '/EasyUIProject';
		var map;
		var contextmenuLatLong;
		
		function initMap() {
			var latitude = 25.76553;			
		    var longitude = -80.22663;

			var latlng = new google.maps.LatLng(latitude, longitude);
			var mapOptions = {
				zoom : 14,
				center : latlng,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById('map'), mapOptions);

			/*Start line*/
			var lineSymbol = {
				path : google.maps.SymbolPath.FORWARD_OPEN_ARROW,
				strokeColor : "#000033",
				strokeOpacity : 0.7,
				strokeWeight : 4,
				scale : 6
			};
			var lineCoordinatesStart = [
					new google.maps.LatLng(25.76548, -80.22813),
					new google.maps.LatLng(25.76553, -80.22663) ];
			var linePathStart = new google.maps.Polyline({
				path : lineCoordinatesStart,
				strokeColor : "#ff0000",
				strokeOpacity : 0.8,
				strokeWeight : 6,
				icons : [ {
					icon : lineSymbol,
					offset : '100%'
				} ]
			});
			linePathStart.setMap(map);
			google.maps.event.addListener(linePathStart, 'click' , function(e) {
				findRoadInfoByLink(e);
			});
			/*Start marker*/
			var startLatLng = new google.maps.LatLng(25.76553, -80.22663);
			var startMarker = new google.maps.Marker({
				position : startLatLng,
				map : map,
				icon : appPath + "/image/markerS.png",
				title : "Main Position"
			});
			startMarker.setMap(map);
			
			/*End line*/
			var lineCoordinatesEnd = [
					new google.maps.LatLng(25.76556, -80.22546),
					new google.maps.LatLng(25.76558, -80.22457) ];
			var linePathEnd = new google.maps.Polyline({
				path : lineCoordinatesEnd,
				strokeColor : "#000000",
				strokeOpacity : 0.8,
				strokeWeight : 6,
				icons : [ {
					icon : lineSymbol,
					offset : '100%'
				} ]
			});
			linePathEnd.setMap(map);
			google.maps.event.addListener(linePathEnd, 'click' , function(e) {
				findRoadInfoByLink(e);
			});
			/*End marker*/
			var endLatLng = new google.maps.LatLng(25.76558, -80.22457)
			var endMarker = new google.maps.Marker({
				position : endLatLng,
				map : map,
				icon : appPath + "/image/markerE.png",
				title : "Main Position"
			});
			endMarker.setMap(map);
			
			/*Right click*/			
			var infoHeight = document.getElementById('infoArea').clientHeight;
			console.log(infoHeight);
			google.maps.event.addListener(map, 'rightclick', function(e) {
		  		$('#rclickMenu').menu('show', {
		  	                left: e.pixel.x + "px",
		  	                top: e.pixel.y + infoHeight + "px"
		  	        });
		  	    contextmenuLatLong=e.latLng;
		  	});	
			
		}

		/*click on the polyline*/
		function findRoadInfoByLink(e){
			var latitude = e.latLng.lat();
			var longitude = e.latLng.lng();
			var content = '<div class="infoWidow">' + '<table><thead><tr><td>name</td><td>type</td></tr></thead>' +
			                   '<tbody><tr><td>name</td><td>type</td></tr>' + 
			                   '<tr><td>name</td><td>type</td></tr></tbody></table></div>';
			console.log(latitude);
			console.log(longitude);
			var position = new google.maps.LatLng(latitude, longitude);
			var infoWindow = new google.maps.InfoWindow();
			infoWindow.setContent(content);
			infoWindow.setPosition(position);
			infoWindow.open(map);			
		}
		
		/*Right click menu request and paint line*/
		function findLinksByLatLng(){
			$.post();
		}
	</script>  	
	<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBSzpT5VDEA9eZSyvzTaa-KaLxBjyugulA&callback=initMap"
    async defer></script> 
	
	<script type="text/javascript">
	     
	
	
	
	</script>
</body>
</html>
<%	
} else {
	out.print("Error to request the incident.");
}
%>
