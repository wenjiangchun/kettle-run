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
            height: 800px;
            width: 100%;
        }
        #infoDiv {
            padding: 8px;
        }
    </style>
    <script>
        var dojoConfig = {
            has: {
                "esri-featurelayer-webgl": 1
            }
        };
    </script>
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
                    "esri/widgets/BasemapToggle",
                    "esri/core/watchUtils",
                    "esri/request",
                    "dojo/_base/array",
                    "dojo/on",
                    "dojo/dom",
                    "dojo/domReady!map"
                ],
                function(
                        Map, SceneView, MapView, MapImageLayer,ImageryLayer,TileLayer,Layer,Extent,FeatureLayer,LayerList
                        ,VectorTileLayer,urlUtils,esriConfig,Legend,GraphicsLayer,Graphic,Circle,LabelClass,Point,BasemapToggle, watchUtils,esriRequest,arrayUtils, on, dom) {


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
                    view.ui.add("infoDiv", "top-right");
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
                            type: "int"
                        }, {
                            name: "totalArea",
                            alias: "totalArea",
                            type: "float"
                        }, {
                            name: "avgPrice",
                            alias: "avgPrice",
                            type: "float"
                        }];



                    var pTemplate = {
                        title: "{projectName}",
                        content:"<ul><li><strong>项目名称</strong>: {projectName}</li><li><strong>预现售项目编号</strong>: {projectNum}</li><li><strong>坐落</strong>: {address}</li><li><strong>销售套数</strong>: {totalCount}(套)</li><li><strong>销售面积</strong>: {totalArea}(㎡)</li><li><strong>均价</strong>: {avgPrice}(元)</li><li><a href='javascript:void(0)' onclick='showInfo({ObjectID})'><strong>查看详情</strong></a></li></ul>"
                    };

                    view.when(function() {
                        getData()
                                .then(createGraphics) // then send it to the createGraphics() method
                                .then(createLayer) // when graphics are created, create the layer
                                .otherwise(errback);
                    });

                    function getData() {
                        var startDate = $("#start-select").val();
                        var endDate = $("#end-select").val();
                        var orderIndex = $("#type-select").val();
                        var url = "${ctx.contextPath}/getProjectInfo?startDate=" + startDate + "&endDate=" + endDate + "&orderIndex=" + orderIndex;
                        return esriRequest(url, {
                            responseType: "json"
                        });
                    }
                    var legend, lyr, maxVal=0, defaultHouseSym, houseRender, seg1, seg2, seg3,seg4, seg5;
                    function createGraphics(response) {
                        var geoJson = response.data;
                        return geoJson.map(function(feature, i) {
                            if (i == 0) {
                                var mark = $("#type-select").val();
                                var houseColor = "red";
                                var labelTitle= "销售单价示例(单位:元)";
                                var labelField = "avgPrice";
                                console.log(feature.avgPrice);
                                console.log(feature.totalCount);
                                console.log(feature.totalArea);
                                if (mark == "0") {
                                    maxVal = feature.avgPrice;
                                } else if (mark == "1") {
                                    houseColor = "seagreen";
                                    maxVal = feature.totalCount;
                                    labelTitle= "销售套数示例(单位:套)";
                                    labelField = "totalCount";
                                }else if (mark == "2") {
                                    houseColor = "blue";
                                    maxVal = feature.totalArea;
                                    labelTitle= "销售面积示例(单位:㎡)";
                                    labelField = "totalArea";
                                }
                                seg1 = Math.ceil(maxVal /10);
                                seg2 = Math.ceil(maxVal * 3 /10);
                                seg3 = Math.ceil(maxVal * 7 /10);
                                seg4 = Math.ceil(maxVal * 9 /10);
                                seg5 = Math.ceil(maxVal * 9 /10) + 1;
                                defaultHouseSym = {
                                    type: "simple-marker", // autocasts as new SimpleMarkerSymbol()
                                    color: houseColor,
                                    outline: { // autocasts as new SimpleLineSymbol()
                                        color: "white",
                                        width: 1,
                                        style: "solid"
                                    }
                                };
                                houseRender = {
                                    type: "simple", // autocasts as new SimpleRenderer()
                                    symbol: defaultHouseSym,
                                    visualVariables: [{
                                        type: "size",
                                        field: labelField,
                                        //normalizationField: "avgPrice",
                                        legendOptions: {
                                            title: labelTitle
                                        },
                                        stops: [
                                            {
                                                value: seg1,
                                                size: 8,
                                                label: "<" + seg1
                                            },
                                            {
                                                value: seg2,
                                                size: 16,
                                                label: seg1 + "-" + seg2
                                            },
                                            {
                                                value: seg3,
                                                size: 24,
                                                label: seg2 + "-" + seg3
                                            },
                                            {
                                                value: seg4,
                                                size: 28,
                                                label: seg3 + "-" + seg4
                                            },
                                            {
                                                value: seg5,
                                                size: 36,
                                                label: ">" + seg4
                                            }]
                                    }]
                                };
                            }
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
                                    totalArea:feature.totalArea,
                                    avgPrice:feature.avgPrice
                                }
                            };
                        });
                    }


                    function createLayer(graphics) {
                        const statesLabelClass = new LabelClass({
                            labelExpressionInfo: { expression: "$feature.address" },
                            symbol: {
                                type: "text",  // autocasts as new TextSymbol()
                                color: "red",
                                haloSize: 2,
                                haloColor: "white"
                            }
                        });
                        lyr = new FeatureLayer({
                            source: graphics, // autocast as an array of esri/Graphic
                            // create an instance of esri/layers/support/Field for each field object
                            fields: fields, // This is required when creating a layer from Graphics
                            objectIdField: "ObjectID", // This must be defined when creating a layer from Graphics
                            renderer: houseRender, // set the visualization on the layer
                            spatialReference: permitsLyr1.spatialReference,
                            geometryType: "point", // Must be set when creating a layer from Graphics
                            popupTemplate: pTemplate,
                            labelsVisible: true,
                            labelingInfo : [statesLabelClass]
                        });
                        map.add(lyr);

                        var title = "项目住宅销售" + $("#type-select").find("option:selected").text() + "分析";
                        legend = new Legend({
                            view: view,
                            layerInfos: [
                                {
                                    layer: lyr,
                                    title: title
                                }]
                        });

                        view.ui.add(legend, "bottom-left");
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
                        console.error("Creating legend failed. ", error);
                    }

                    $("#search").click(function() {
                        view.ui.remove(legend);
                        map.remove(lyr);
                        getData()
                                .then(createGraphics) // then send it to the createGraphics() method
                                .then(createLayer) // when graphics are created, create the layer
                                .otherwise(errback);
                    });

                    $('.datepicker').datepicker({format:"yyyy-mm-dd",autoclose:true,language:"zh-cn"});
                });

        var showInfo = function(objectId) {
            layer.open({
                type: 2,
                title: "项目销售详情(销售日期" + $("#start-select").val() + "至" + $("#end-select").val() + ")",
                shadeClose: true,
                shade: 0.8,
                area: ['99%', '90%'],
                content: '${ctx.contextPath}/getSaleInfo/' + objectId +"?startDate="+$("#start-select").val()+"&endDate=" + $("#end-select").val()
            });
        };
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
                <small>住宅销售分析</small>
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
            <div id="infoDiv" class="esri-widget">
                销售日期:
                <input id="start-select" type="text" class="esri-widget datepicker" value="${startDate!}">
                 -至-
                <input id="end-select" type="text" class="esri-widget datepicker" value="${endDate!}">
                分析要素:
                <select id="type-select" class="esri-widget">
                    <option value="0" selected>单价</option>
                    <option value="1">套数</option>
                    <option value="2">面积</option>
                </select> <button class="btn btn-danger" id="search">查询</button>
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
