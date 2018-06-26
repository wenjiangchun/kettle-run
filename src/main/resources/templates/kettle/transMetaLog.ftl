<!DOCTYPE html>
<html>
<head>
  <#include "../common/head.ftl"/>
    <link rel="stylesheet" href="${ctx.contextPath}/adminLTE/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="{ctx.contextPath}/adminLTE/Ionicons/css/ionicons.min.css">
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
                                <table id="example1" class="table table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th>日志ID</th>
                                        <th>转换名称</th>
                                        <th>是否执行成功</th>
                                        <th>错误数</th>
                                        <th>当前状态</th>
                                        <th>开始执行时间</th>
                                        <th>结束执行时间</th>
                                        <th>执行日志</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                <#list logs as log>
                                <tr>
                                    <td>${log.id!}</td>
                                    <td>${log.name!}</td>
                                    <td>${log.success?string('是', '否')!}</td>
                                    <td>${log.errors!}</td>
                                    <td>${log.status!}</td>
                                    <td>${log.startDate?string("yyyy-MM-dd hh:mm:ss")!}</td>
                                    <td>${log.endDate?string("yyyy-MM-dd hh:mm:ss")!}</td>
                                    <td><a href="javascript:void(0)" title="${log.content!}">详细</a> </td>
                                </tr>
                                </#list>
                                    </tbody>
                                </table>
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
        <script src="${ctx.contextPath}/adminLTE/datatables.net/js/jquery.dataTables.min.js"></script>
        <script src="${ctx.contextPath}/adminLTE/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script>
    $(function () {
        $('#example1').DataTable({
            'paging': true,
            'lengthChange': true,
            'searching': true,
            'ordering': true,
            'info': true,
            'autoWidth': true
        })
    });
</script>
</body>
</html>
