<meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>ETL数据抽取平台</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.7 -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/bootstrap/dist/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/font-awesome/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/Ionicons/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/dist/css/AdminLTE.min.css">
  <!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/dist/css/skins/_all-skins.min.css">
  <!-- Morris chart -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/morris.js/morris.css">
  <!-- jvectormap -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/jvectormap/jquery-jvectormap.css">
  <!-- Date Picker -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/bootstrap-datepicker/dist/css/bootstrap-datepicker.min.css">
  <!-- Daterange picker -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/bootstrap-daterangepicker/daterangepicker.css">
  <!-- bootstrap wysihtml5 - text editor -->
  <link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css">
<link rel="stylesheet" href="${ctx.contextPath}/resources/adminLTE/plugins/pace/pace.min.css">
  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
<!-- ./wrapper -->

<!-- jQuery 3 -->
<script src="${ctx.contextPath}/resources/adminLTE/jquery/dist/jquery.min.js"></script>
<!-- jQuery UI 1.11.4 -->
<script src="${ctx.contextPath}/resources/adminLTE/jquery-ui/jquery-ui.min.js"></script>
<!-- Resolve conflict in jQuery UI tooltip with Bootstrap tooltip -->
<script>
    $.widget.bridge('uibutton', $.ui.button);
</script>
<!-- Bootstrap 3.3.7 -->
<script src="${ctx.contextPath}/resources/adminLTE/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- Morris.js charts -->
<script src="${ctx.contextPath}/resources/adminLTE/raphael/raphael.min.js"></script>
<script src="${ctx.contextPath}/resources/adminLTE/morris.js/morris.min.js"></script>
<!-- Sparkline -->
<script src="${ctx.contextPath}/resources/adminLTE/jquery-sparkline/dist/jquery.sparkline.min.js"></script>
<!-- jvectormap -->
<script src="${ctx.contextPath}/resources/adminLTE/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script src="${ctx.contextPath}/resources/adminLTE/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<!-- jQuery Knob Chart -->
<script src="${ctx.contextPath}/resources/adminLTE/jquery-knob/dist/jquery.knob.min.js"></script>
<!-- daterangepicker -->
<script src="${ctx.contextPath}/resources/adminLTE/moment/min/moment.min.js"></script>
<script src="${ctx.contextPath}/resources/adminLTE/bootstrap-daterangepicker/daterangepicker.js"></script>
<!-- datepicker -->
<script src="${ctx.contextPath}/resources/adminLTE/bootstrap-datepicker/dist/js/bootstrap-datepicker.min.js"></script>
<!-- Bootstrap WYSIHTML5 -->
<script src="${ctx.contextPath}/resources/adminLTE/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"></script>
<!-- Slimscroll -->
<script src="${ctx.contextPath}/resources/adminLTE/jquery-slimscroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="${ctx.contextPath}/resources/adminLTE/fastclick/lib/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="${ctx.contextPath}/resources/adminLTE/dist/js/adminlte.min.js"></script>
<!-- AdminLTE dashboard demo (This is only for demo purposes) -->
<script src="${ctx.contextPath}/resources/adminLTE/dist/js/pages/dashboard.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="${ctx.contextPath}/resources/adminLTE/dist/js/demo.js"></script>
<script src="${ctx.contextPath}/resources/layer/layer.js"></script>
<script src="${ctx.contextPath}/resources/adminLTE/PACE/pace.min.js"></script>
<script type="text/javascript">
    $(document).ajaxStart(function () {
        Pace.restart();
    });
</script>