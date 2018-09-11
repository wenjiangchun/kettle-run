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
                    <form class="form-inline">
                        <div class="form-group">
                            <label for="inputEmail3">项目名称:</label>
                            <p class="form-control-static">${preProjectName!}</p>
                        </div>
                        <div class="form-group">
                            <label for="inputPassword3">预售证号:</label>
                            <p class="form-control-static">${permissionNum!}</p>
                        </div>
                        <div class="form-group">
                            <label for="inputPassword3">开发商:</label>
                            <p class="form-control-static">${mainBodyName!}</p>
                        </div>
                    </form>
                </div>
                <!-- /.box-header -->
                <!-- form start -->
                <table id="example1" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>业务号</th>
                        <th>合同号</th>
                        <th>合同坐落</th>
                        <th>合同金额</th>
                        <th>合同面积</th>
                        <th>单价</th>
                        <th>房屋用途</th>
                        <th>购买人</th>
                        <th>购买人户籍</th>
                        <th>购买人证件号</th>
                        <th>备案日期</th>
                    </tr>
                    </thead>
                    <tbody>
                                <#list results as rs>
                                <tr>
                                    <td>${rs['TRANSACTION_NUM_']!''}</td>
                                    <td>${rs['CONTRACT_NUM_']!''}</td>
                                    <td>${rs['CONTRACT_POSITION_']!''}</td>
                                    <td>${rs['CONTRACT_PRICE_']!''}</td>
                                    <td>${rs['CONTRACT_AREA_']!''}</td>
                                    <td>${rs['AVG_PRICE_']!''}</td>
                                    <td>${rs['HOUSE_USED_NAME_']!''}</td>
                                    <td>${rs['BUYER_']!''}</td>
                                    <td>${rs['BUYER_REGISTER_']!''}</td>
                                    <td>${rs['BUYER_ID_CARD_']!''}</td>
                                    <td>${rs['RECORD_TIME_']!''}</td>
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
            }
        })
    });
</script>
</body>
</html>
