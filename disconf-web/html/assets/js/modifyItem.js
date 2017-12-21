(function ($) {

    getSession();

    var configId = Util.param.getConfigId();

    fetchItem();

    //
    // 获取配置项
    //
    function fetchItem() {

        //
        // 获取此配置项的数据
        //
        $.ajax({
            type: "GET",
            url: "/api/web/config/" + configId
        }).done(
            function (data) {
                if (data.success === "true") {
                    var result = data.result;
                    $("#app").text(
                        result.appName + ' (appid=' + result.appId
                        + ')');
                    $("#version").text(result.version);
                    $("#env").text(result.envName);
                    $("#key").text(result.key);
                    $("#value").val(result.value);
                    $("#currentData").html(
                        result.appName + "<b style='color:greenyellow'>*</b> " + result.version + " <b style='color:greenyellow'>*</b> "
                        + result.envName);

                    // 展示java配置信息
                    $("#javaClient").text(result.javaClient);
                    $("#autoReload").text(result.autoReload);


                    // 判断是否是json格式，用于启动界面化配置
                    if (isJsonString(result.value)) {
                        $("#value").attr("readonly", "readonly")
                        $("#value").css("cursor","pointer")
                        // 绑定事件
                        $("#value").bind("click", function(e){
                            // value，
                            app.reload();
                            var jsons = JSON.parse($(this).val());
                            // render
                            app.load(jsons);
                            app.resize();
                            //弹出即全屏
                            var index = layer.open({
                                title:"编辑配置项:"+result.appName + "<b style='color:greenyellow'>*</b> " + result.version + " <b style='color:greenyellow'>*</b> "
                                + result.envName,
                                type: 1,
                                content: $("#je-container"),
                                area: ['320px', '195px'],
                                maxmin: true,
                                cancel: function () {
                                    if($("#forButtonSave").val()){
                                        $("#value").val($("#forButtonSave").val())
                                    }
                                }
                            });
                            layer.full(index);
                            // $.each(jsons,function(n,config){
                            //     console.log(n);
                            //     for(var key in config){
                            //         console.log(key+"=================="+config[key]);
                            //     }
                            // })
                        })
                    }


                    // 获取APP下的配置数据
                    // 获取配置项/配置文件列表
                    fetchItems(result.appId, result.envId, result.version,
                        configId);
                }
            });
    }

    // 提交
    $("#submit").on("click", function (e) {
        console.log("click click item")
        $("#error").addClass("hide");
        var me = this;
        var value = $("#value").val();
        // 验证
        if (!value) {
            $("#error").removeClass("hide");
            $("#error").html("表单不能为空或填写格式错误！");
            return;
        }
        $.ajax({
            type: "PUT",
            url: "/api/web/config/item/" + configId,
            data: {
                "value": value
            }
        }).done(function (data) {
            $("#error").removeClass("hide");
            if (data.success === "true") {
                $("#error").html(data.result);
            } else {
                Util.input.whiteError($("#error"), data);
            }
        });
    });

})(jQuery);
