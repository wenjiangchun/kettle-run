<!DOCTYPE html>
<html>
<head>
  <#include "../common/head.ftl"/>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="${ctx.contextPath}/adminLTE/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${ctx.contextPath}/adminLTE/Ionicons/css/ionicons.min.css">
    <link rel="stylesheet" href="http://188.9.25.151:8080/arcgis_js_api/library/4.8/esri/css/main.css">
    <#--<link rel="stylesheet" href="https://js.arcgis.com/4.8/esri/css/main.css">-->
    <style>
        #map {
            padding: 0;
            margin: 0;
            height: 700px;
            width: 100%;
        }
    </style>
    <script type="text/javascript" src="http://188.9.25.151:8080/arcgis_js_api/library/4.8/init.js"></script>
    <#--<script src="https://js.arcgis.com/4.8/"></script>-->
    <script>
        require([
            "esri/Map",
            "esri/views/MapView",
            "esri/layers/MapImageLayer",
            "esri/config",
            "dojo/domReady!"
        ], function(Map, MapView,MapImageLayer,esriConfig) {
            esriConfig.request.proxyUrl="http://188.9.25.151:8080";
            var layer = new MapImageLayer({
                url: "http://gisserver.gov.xm:8080/RemoteRest/services/MAP_VEC/MapServer"
            });
            var map = new Map({
                /*basemap: "streets"*/
                layers: [layer]
            });
            var view = new MapView({
                container: "map",  // Reference to the scene div created in step 5
                map: map,  // Reference to the map object created before the scene
                zoom: 4
            });
        });
    </script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
<#include "../common/nav.ftl"/>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                地图展示
                <small>转换列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 主页</a></li>
                <li><a href="#">ETL管理</a></li>
                <li class="active">地图展示</li>
            </ol>
        </section>
        <section class="content">
            <div id="map">

            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
<#include "../common/foot.ftl"/>
<#include "../common/left.ftl"/>
</div>

</body>

</html>
