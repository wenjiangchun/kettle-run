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
    <#--<script type="text/javascript" src="https://js.arcgis.com/4.8"></script>-->
    <script>
        require([
                    "esri/Map",
                    "esri/views/SceneView",
                    "esri/views/MapView",
                    "esri/layers/MapImageLayer",
                    "esri/layers/ImageryLayer",
                    "esri/layers/TileLayer",
                    "esri/layers/Layer",
                    "esri/geometry/Extent",
                    "esri/layers/FeatureLayer",
                    "esri/widgets/LayerList",
                    "esri/layers/VectorTileLayer",
                    "esri/core/urlUtils",
                    "esri/config",
                    "esri/widgets/Legend",
                    "esri/layers/GraphicsLayer",
                    "esri/Graphic",
                    "esri/geometry/Circle",
                    "esri/layers/support/LabelClass",
                    "esri/geometry/Point",
                    "esri/core/watchUtils",
                    "esri/request",
                    "dojo/_base/array",
                    "dojo/on",
                    "dojo/dom",
                    "dojo/domReady!map"
                ],
                function(
                        Map, SceneView, MapView, MapImageLayer,ImageryLayer,TileLayer,Layer,Extent,FeatureLayer,LayerList
                        ,VectorTileLayer,urlUtils,esriConfig,Legend,GraphicsLayer,Graphic,Circle,LabelClass,Point, watchUtils,esriRequest,arrayUtils, on, dom) {


                    var permitsLyr1 = new TileLayer({
                        url:"http://gisserver.gov.xm:8080/RemoteRest/services/MAP_VEC/MapServer"
                    });
                    /*****************************************************************
                     * Add the layer to a map
                     *****************************************************************/
                    var map = new Map({
                        //basemap: "streets",
                        layers: [permitsLyr1],
                        ground: "world-elevation"
                    });
                    //map.add(permitsLyr1);
                    var view = new MapView({
                        container: "map",
                        map: map
                    });

                    watchUtils.whenTrue(view, "stationary", function() {
                        if (view.extent) {

                        }
                    });

                    var fields = [
                        {
                            name: "ObjectID",
                            alias: "ObjectID",
                            type: "oid"
                        }, {
                            name: "x",
                            alias: "x",
                            type: "string"
                        }, {
                            name: "y",
                            alias: "y",
                            type: "string"
                        }, {
                            name: "address",
                            alias: "address",
                            type: "string"
                        }, {
                            name: "projectName",
                            alias: "projectName",
                            type: "string"
                        }, {
                            name: "projectNum",
                            alias: "projectNum",
                            type: "string"
                        }, {
                            name: "totalCount",
                            alias: "totalCount",
                            type: "string"
                        }, {
                            name: "avgPrice",
                            alias: "avgPrice",
                            type: "string"
                        }];

                    var pTemplate = {
                        title: "{projectName}",
                        content:"<ul><li>项目名称: {projectName}</li><li>预先售项目编号: {projectNum}</li><li>坐落: {address}</li><li>销售套数: {totalCount}</li><li>均价: {avgPrice}</li></ul>"
                    };


                    var quakesRenderer = {
                        type: "simple", // autocasts as new SimpleRenderer()
                        label: "",
                        symbol: {
                            type: "simple-marker", // autocasts as new SimpleMarkerSymbol()
                            style: "circle",
                            size: 5,
                            color: [211, 255, 0, 0],
                            outline: {
                                width: 5,
                                color: "#FF0055",
                                style: "solid"
                            }
                        }
                    };

                    view.when(function() {
                        getData()
                                .then(createGraphics) // then send it to the createGraphics() method
                                .then(createLayer) // when graphics are created, create the layer
                                .otherwise(errback);
                    });

                    function getData() {
                        var url = "${ctx.contextPath}/getProjectInfo";
                        return esriRequest(url, {
                            responseType: "json"
                        });
                    }

                    function createGraphics(response) {
                        var geoJson = response.data;
                        return geoJson.map(function(feature, i) {
                            return {
                                geometry: new Point({
                                    x: feature.x,
                                    y: feature.y,
                                    spatialReference:permitsLyr1.spatialReference
                                }),
                                // select only the attributes you care about
                                attributes: {
                                    ObjectID: feature.preProjectId,
                                    address: feature.projectAddress,
                                    x: feature.x,
                                    y: feature.y,
                                    projectName: feature.projectName,
                                    projectNum: feature.projectNum,
                                    totalCount:feature.totalCount,
                                    avgPrice:feature.avgPrice
                                }
                            };
                        });
                    }

                    function createLayer(graphics) {
                        const statesLabelClass = new LabelClass({
                            labelExpressionInfo: { expression: "$feature.projectName" },
                            symbol: {
                                type: "text",  // autocasts as new TextSymbol()
                                color: "red",
                                haloSize: 2,
                                haloColor: "white"
                            }
                        });
                        var lyr = new FeatureLayer({
                            source: graphics, // autocast as an array of esri/Graphic
                            // create an instance of esri/layers/support/Field for each field object
                            fields: fields, // This is required when creating a layer from Graphics
                            objectIdField: "ObjectID", // This must be defined when creating a layer from Graphics
                            renderer: quakesRenderer, // set the visualization on the layer
                            spatialReference: permitsLyr1.spatialReference,
                            geometryType: "point", // Must be set when creating a layer from Graphics
                            popupTemplate: pTemplate
                        });
                        map.add(lyr);
                        alert(map.layers.length);
                        lyr.visible=true;




                        var point = new Point({
                            x: 59041.86650000047,
                            y: 19162.87450000085
                            //spatialReference:permitsLyr1.spatialReference
                        });
                        var markerSymbol = {
                            type: "simple-marker", // autocasts as new SimpleMarkerSymbol()
                            color: "#FF4000",
                            style:"circle",
                            outline: { // autocasts as new SimpleLineSymbol()
                                color: "green",
                                width: 5
                            }
                        };
                        var pointGraphic = new Graphic({
                            geometry: point,
                            symbol: markerSymbol
                        });
                        var areaGraphicLayer = new GraphicsLayer({
                            visible : true
                        });
                        /*for (var i = 0; i< graphics.length; i++) {
                            var pointGraphic = new Graphic({
                                geometry: graphics[i].geometry,
                                symbol: markerSymbol
                            });
                            areaGraphicLayer.graphics.add(pointGraphic);
                        }*/
                        areaGraphicLayer.graphics.add(pointGraphic);
                        map.add(areaGraphicLayer);
                        return lyr;
                    }




                    /*

                                    function createLegend(layer) {
                                        // if the legend already exists, then update it with the new layer
                                        if (legend) {
                                            legend.layerInfos = [{
                                                layer: layer,
                                                title: "Magnitude"
                                            }];
                                        } else {
                                            legend = new Legend({
                                                view: view,
                                                layerInfos: [
                                                    {
                                                        layer: layer,
                                                        title: "Earthquake"
                                                    }]
                                            }, "infoDiv");
                                        }
                                    }
                    */

                    // Executes if data retrieval was unsuccessful.
                    function errback(error) {
                        alert(77);
                        console.error("Creating legend failed. ", error);
                    }
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
