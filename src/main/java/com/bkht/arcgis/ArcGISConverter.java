package com.bkht.arcgis;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.localserver.DynamicWorkspace;
import com.esri.arcgisruntime.localserver.EnterpriseGeodatabaseWorkspace;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ArcGISConverter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
   public void convert() {
       ArcGISRuntimeEnvironment.setInstallDirectory("D:\\arcgis-runtime-sdk-java-100.2.0");
       //ArcGISRuntimeEnvironment.setLicense();
       LocalServer.INSTANCE.startAsync().addDoneListener(new Runnable() {
           @Override
           public void run() {
               //首先将空间数据库中数据在arcMap中配置成地图打包成mpk格式文件
               LocalMapService myLocalMapService = new LocalMapService("d:\\xm.mpk");
               //创建企业数据库工作区间 主要第二个参数目前不太明确具体格式?
               EnterpriseGeodatabaseWorkspace workspace = EnterpriseGeodatabaseWorkspace.createFromConnectionString("test","PASSWORD=fbtest;SERVER=ORACLE;INSTANCE=xmbdcdg;DBCLIENT=bdcdj;DB_CONNECTION_PROPERTIES=connectionProp;DATABASE=SDE;USER=SDE;VERSION=DBversion;AUTHENTICATION_MODE=DBMS");
               List<DynamicWorkspace> dw = new ArrayList<>();
               dw.add(workspace);
               myLocalMapService.setDynamicWorkspaces(dw);
               myLocalMapService.startAsync().addDoneListener(() -> {
                   ArcGISMapImageLayer layer = new ArcGISMapImageLayer(myLocalMapService.getUrl());
                   layer.setName("Enterprise");
                   System.out.println(layer.getGeodatabaseVersion());
                   String url = myLocalMapService.getUrl() + "/0";
                   // create a feature layer using the url
                   ServiceFeatureTable featureTable = new ServiceFeatureTable(url);
                   featureTable.loadAsync();
                   QueryParameters parameters = new QueryParameters();
                   parameters.setReturnGeometry(true);
                   parameters.setWhereClause("OBJECTID >=40000 and OBJECTID<50000");
                   parameters.setMaxFeatures(50000);
                   System.out.println(featureTable.getTotalFeatureCount());
                   ListenableFuture<FeatureQueryResult> results = featureTable.queryFeaturesAsync(parameters,ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                   AtomicInteger i = new AtomicInteger(1);
                   try {
                       results.get().forEach(feature -> {
                           Map<String, Object> attributes =  feature.getAttributes();
                           Object NBUILDING_ID = attributes.get("NBUILDING_ID");
                           //System.out.println(feature.getGeometry().getExtent().getCenter().toJson());
                           String sql = "update D_UNIT_NBUILDING set x_=?,y_=? where NBUILDING_ID_=?";
                           if (NBUILDING_ID != null && feature.getGeometry().getExtent().getCenter() != null) {
                               jdbcTemplate.update(sql,String.valueOf(feature.getGeometry().getExtent().getCenter().getX()),String.valueOf(feature.getGeometry().getExtent().getCenter().getY()), NBUILDING_ID );
                               System.out.println(NBUILDING_ID + ",X=" +feature.getGeometry().getExtent().getCenter().getX() + ",Y="+feature.getGeometry().getExtent().getCenter().getY());
                           } else {
                               System.out.println("跳过第" + i.addAndGet(1) + "NBUILDING_ID=" + NBUILDING_ID);
                           }
                           i.addAndGet(1);
                           System.out.println("执行第"+i.get());
                       });
                       System.out.println("执行更新完毕");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }); }
       });
   }
}
