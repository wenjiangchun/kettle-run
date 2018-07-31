<!DOCTYPE html>
<html>
<head>
  <#include "../../common/head.ftl"/>
</head>
<body class="hold-transition skin-blue sidebar-mini">
        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                        <!-- /.box-header -->
                            <div class="box box-info">
                                <div class="box-header with-border">
                                </div>
                                <!-- /.box-header -->
                                <!-- form start -->
                                <form class="form-horizontal">
                                    <div class="box-body">
                                        <div class="form-group">
                                            <label for="inputEmail3" class="col-sm-2 control-label">转换路径</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" id="inputEmail3" placeholder="ObjectId" type="text" value="${transMeta.pathAndName!}" readonly>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="inputPassword3" class="col-sm-2 control-label">创建人</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" id="inputPassword3" placeholder="Password" type="text" value="${transMeta.createdUser!}" readonly>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="inputPassword3" class="col-sm-2 control-label">创建时间</label>
                                            <div class="col-sm-10">
                                                <input class="form-control" id="inputPassword3" placeholder="Password" type="text" value="${transMeta.createdDate!}" readonly>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="inputPassword3" class="col-sm-2 control-label">参数列表</label>
                                            <div class="col-sm-10">
                                                <#list transMeta.listParameters() as parameter>
                                                    <div class="form-group">
                                                        <label for="inputPassword3" class="col-sm-2 control-label">${parameter!}</label>
                                                        <div class="col-sm-10">
                                                            <input class="form-control query" name="${parameter!}"  placeholder="请输入" type="text" value="${transMeta.getParameterDefault(parameter)!}">
                                                            <span class="help-block">${transMeta.getParameterDescription(parameter)!}</span>
                                                        </div>
                                                    </div>
                                                </#list>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- /.box-body -->
                                    <div class="box-footer">
                                        <button type="button" class="btn btn-default">取消</button>
                                        <button type="button" class="btn btn-info pull-right" id="runBtn">执行</button>
                                    </div>
                                    <!-- /.box-footer -->
                                </form>
                            </div>
                        <!-- /.box-body -->
                    <!-- /.box -->
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
        </section>
        <!-- /.content -->
    <!-- /.content-wrapper -->
<script>
    $(function () {
        $("#runBtn").click(function(){
            var isSubmit = true;
            var queryVariables = "objectId="+${transMeta.objectId};
            $("input.query").each(function() {
                var name = $(this).attr("name");
                var value = $(this).val().trim();
                if (value === "") {
                    alert(name + "不能为空");
                    isSubmit = false;
                    return false;
                } else {
                    queryVariables +="&kettleParams['"+name+"']="+value;
                }
             });
            if (isSubmit) {
                layer.load();
                console.log(queryVariables)

                $.ajax({
                    "type": "post",
                    "url": "${ctx.contextPath}/kettle/runTrans",
                    "data": queryVariables,
                    "success": function (data) {
                        alert(data);
                        layer.closeAll('loading');
                    }
                });
            }
        });
    });
    function runTran(transObjectId) {
        layer.open({
            type: 2,
            title: '转换信息',
            shadeClose: true,
            shade: 0.8,
            area: ['380px', '90%'],
            content: '${ctx.contextPath}/kettle/getTransMeta/'+ transObjectId //iframe的url
        });
    }
</script>
</body>
</html>
