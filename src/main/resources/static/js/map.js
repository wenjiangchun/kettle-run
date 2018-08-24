var myMap = {};
var areaGraphicLayer,buildingGraphicLayer;
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
        "esri/core/watchUtils",
        "dojo/_base/array",
        "dojo/on",
        "dojo/dom",
        "dojo/domReady!map"
    ],
    function(
        Map, SceneView, MapView, MapImageLayer,ImageryLayer,TileLayer,Layer,Extent,FeatureLayer,LayerList
        ,VectorTileLayer,urlUtils,esriConfig,Legend,GraphicsLayer,Graphic,Circle,watchUtils,arrayUtils, on, dom) {
        //初始化图层数组
        myMap.initLayers = function() {
            this.layers = [];
        };
        myMap.initLayers();
        myMap.setMap = function(map, view) {
            this.map = map;
            this.view = view;
        };

        myMap.removeLayerByType = function(type) {
            var $this = this.map;
            this.map.allLayers.forEach(function(item, i) {
                if (item != undefined && item.type1 === type) {
                    $this.remove(item);
                }
            });
        };

        myMap.addLayer = function(date, type) {
            this.removeLayerByType(type);
            var $this = this;
            for (var i = 0; i < this.layers.length; i++) {
                var layer = this.layers[i];
                if (layer.type1 === type && layer.date === date) {
                    this.map.add(layer);
                    layer.then(function() {
                        $this.goTo(layer, 4326, $this.view);
                    });
                }
            }
        };

        myMap.removeLayer = function(id) {
            var foundLayer = this.map.allLayers.find(function(layer) {
                return layer.id === id;
            });
            this.map.remove(foundLayer);
        };



        myMap.clearGraphic = function() {
            myMap.view.graphics.removeAll();
        };

        areaGraphicLayer = new GraphicsLayer({
            visible : true
        });
        buildingGraphicLayer = new GraphicsLayer({
            visible : false
        });
        /**
         * 添加图表对象
         * @param datas 对象数组
         * @param type 图表类型  可以为point, polyline, polygon
         * @param isClear 是否清除之前添加对象
         */
        myMap.addGraphic = function(datas, type) {
          //遍历数据添加数据
          $(datas).each(function(i,data) {
              var point = {
                  type: "point", // autocasts as new Point()
                  x: data.x,
                  y: data.y,
                  z:1000,
                  spatialReference:permitsLyr1.spatialReference
              };

              // Create a symbol for drawing the point
              var style = type === "building"? "circle" : "circle";
              var pointWidth = type === "building"? 50 : 60;
              var fontSize = type === "building"? 8 : 12;
              var color = type === "building"? "green" : [226, 119, 40];
              var markerSymbol = {
                  type: "simple-marker", // autocasts as new SimpleMarkerSymbol()
                  color: "#FF4000",
                  style:style,
                  outline: { // autocasts as new SimpleLineSymbol()
                      color: color,
                      width: pointWidth
                  }
              };
              var textSymbol = {
                  type: "text",  // autocasts as new TextSymbol()
                  color: "white",
                  backgroundColor:"red",
                  text: data.name,
                  xoffset: 3,
                  yoffset: 10,
                  font: {  // autocast as new Font()
                      size: fontSize,
                      weight: "bolder"
                  }
              };

              var textSymbol1 = {
                  type: "text",  // autocasts as new TextSymbol()
                  color: "white",
                  text: data.totalCount + "套," + data.avgPrice + "元/㎡",
                  backgroundColor:"red",
                  xoffset: 3,
                  yoffset: -5,
                  font: {  // autocast as new Font()
                      size: fontSize - 2,
                      weight: "bolder"
                  }
              };

              var pointGraphic = new Graphic({
                  geometry: point,
                  symbol: markerSymbol
              });
              var titleGraphic = new Graphic({
                  geometry: point,
                  symbol: textSymbol
              });
              var titleGraphic1 = new Graphic({
                  geometry: point,
                  symbol: textSymbol1
              });
              if (type === "building") {
                  pointGraphic.popupTemplate = {
                      title: data.name,
                      content:"销量" + data.totalCount
                  };
                  buildingGraphicLayer.graphics.add(pointGraphic);
                  buildingGraphicLayer.graphics.add(titleGraphic);
                  buildingGraphicLayer.graphics.add(titleGraphic1);
              } else {
                  areaGraphicLayer.graphics.add(pointGraphic);
                  areaGraphicLayer.graphics.add(titleGraphic);
                  areaGraphicLayer.graphics.add(titleGraphic1);
              }
          });
        };


        myMap.goTo = function(layer, targetWkid, view) {
            jQuery.post("http://121.42.151.97:6080/arcgis/rest/services/Utilities/Geometry/GeometryServer/project", {
                inSR:layer.spatialReference.wkid,
                outSR:targetWkid,
                geometries:"["+layer.fullExtent.xmin+","+layer.fullExtent.ymin+","+layer.fullExtent.xmax+","+layer.fullExtent.ymax+"]",
                f:"pjson"
            },function(result) {
                var geos = result.geometries;
                if (geos.length > 0) {
                    var extent = new Extent({
                        xmin: geos[0].x,
                        ymin: geos[0].y,
                        xmax: geos[1].x,
                        ymax: geos[1].y,
                        spatialReference: {
                            wkid: targetWkid
                        }
                    });
                    view.goTo(extent,{
                        duration: 8000,
                        easing: "in-expo"
                    });
                }
            },"json");
        };

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
        myMap.setMap(map, view);

        watchUtils.whenTrue(view, "stationary", function() {
            if (view.extent) {
                if (view.zoom >= 4) {
                    if (areaGraphicLayer.visible) {
                        areaGraphicLayer.visible = false;
                    }
                    if (!buildingGraphicLayer.visible) {
                        buildingGraphicLayer.visible = true;
                    }
                } else {
                    if (buildingGraphicLayer.visible) {
                        buildingGraphicLayer.visible = false;
                    }
                    if (!areaGraphicLayer.visible) {
                        areaGraphicLayer.visible = true;
                    }
                }
            }
        });
        view.when(function() {
            getJson();
        });

        function getJson() {
            jQuery.getJSON("js/area.json", function(datas) {
                myMap.addGraphic(datas, "area");
                myMap.map.add(areaGraphicLayer);
            });
            jQuery.getJSON("js/building.json", function(datas) {
                myMap.addGraphic(datas, "building");
                myMap.map.add(buildingGraphicLayer);
            });
        }
    });

