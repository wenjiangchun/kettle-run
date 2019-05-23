<!DOCTYPE html>
<html>
<head>
  <#include "../../common/head.ftl"/>
    <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="{ctx.contextPath}/resources/adminLTE/Ionicons/css/ionicons.min.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
<#include "../../common/nav.ftl"/>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                转换管理
                <small>转换列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 主页</a></li>
                <li><a href="#">ETL管理</a></li>
                <li class="active">转换管理</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">转换列表</h3>
                            <div class="box-tools">
                                <a href="#" id="refreshRepository" class="btn btn-default"><i class="fa fa-repeat"></i>  刷新</a>
                            </div>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="example1" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>资源ID</th>
                                    <th>资源名称</th>
                                    <th>资源类型</th>
                                    <th>存储路径</th>
                                    <th>资源描述</th>
                                    <th>修改时间</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list jobList as trans>
                                <tr>
                                    <td>${trans.objectId!}</td>
                                    <td>${trans.name!}</td>
                                    <td>${trans.objectType.name()!}</td>
                                    <td>${trans.repositoryDirectory.path!}</td>
                                    <td>${trans.description!}</td>
                                    <td>${trans.modifiedDate!}</td>
                                    <td>
                                        <a href="javascript:void(0)"
                                           onclick="showElementInfo(${trans.objectId}, '${trans.name!}', '${trans.objectType.name()!}')"
                                           class="btn btn-primary btn-sm"><i class="fa fa-info"></i>  查看详细</a>
                                        <a href="javascript:void(0)"
                                           onclick="showElementLog(${trans.objectId}, '${trans.name!}', '${trans.objectType.name()!}')"
                                           class="btn btn-primary btn-sm"><i class="fa fa-file"></i>  查看日志</a>
                                        <a href="javascript:void(0)"
                                           onclick="previewTrans(${trans.objectId}, '${trans.name!}', '${trans.objectType.name()!}')"
                                           class="btn btn-primary btn-sm"><i class="fa fa-file"></i>  转换预览</a>
                                    </td>
                                </tr>
                                </#list>
                                </tbody>
                            </table>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
<#include "../../common/foot.ftl"/>
<#include "../../common/left.ftl"/>
</div>
<script src="${ctx.contextPath}/resources/adminLTE/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${ctx.contextPath}/resources/adminLTE/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- SlimScroll -->
<script src="${ctx.contextPath}/resources/adminLTE/jquery-slimscroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="${ctx.contextPath}/resources/adminLTE/fastclick/lib/fastclick.js"></script>
<script>
    $(function () {
        initMenu();
        $('#example1').DataTable({
            'paging': true,
            'lengthChange': true,
            'searching': true,
            'ordering': false,
            'info': true,
            'autoWidth': true,
            "oLanguage": {
                "sLengthMenu": "每页显示 _MENU_条记录",
                "sZeroRecords": "没有检索到数据",
                "sInfo": "显示第 _START_ - _END_ 条记录；共 _TOTAL_ 条记录",
                "sInfoEmpty": "",
                "sProcessing": "正在加载数据...",
                "sSearch": "检索：",
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "上一页",
                    "sNext": "下一页",
                    "sLast": '尾页'
                }
            },
            "columnDefs": [
                     { "width": "20%", "targets": [ 4 ] },
                     { "ordering": "true", "targets": [ 2 ] }
                   ]
            })

        $("#refreshRepository").click(function() {
            window.location.href="${ctx.contextPath}/kettle/refreshRepository";
        });
    });


    function showElementInfo(objectId, title, type) {
        layer.open({
            type: 2,
            title: title,
            shadeClose: true,
            shade: 0.8,
            area: ['800px', '60%'],
            content: '${ctx.contextPath}/kettle/job/getElementInfo/' + objectId + '/' + type //iframe的url
        });
    }

    function showElementLog(objectId, title, type) {
        layer.open({
            type: 2,
            title: title,
            shadeClose: true,
            shade: 0.8,
            area: ['80%', '60%'],
            content: '${ctx.contextPath}/kettle/getElementLog/' + objectId + '/' + type //iframe的url
        });
    }

    function initMenu() {
        var parent = $("#transMenu").parent().parent().parent();
        $(".sidebar-menu").find(".active").removeClass("active");
        parent.addClass("active");
        $("#transMenu").parent().addClass("active");
    }
</script>
</body>

</html>
