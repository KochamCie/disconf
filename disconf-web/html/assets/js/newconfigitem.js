var appId = -1;
var envId = -1;
var version = "";
var javaClient = -1;
var autoReload = -1;
getSession();

// 提交
$("#item_submit").on("click", function (e) {
    $("#error").addClass("hide");

    if (version == '自定义版本') {
        version = $('#selfversion_value').val();
    }

    var key = $("#key").val();
    var value = $("#value").val();

    console.log(javaClient+","+autoReload+","+envId)
    // 验证
    if (appId < 1 || envId < 1 || javaClient < 0 || autoReload < 0 || version == "" || !value || !key) {
        $("#error").removeClass("hide");
        $("#error").html("表单不能为空或填写格式错误！");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/api/web/config/item",
        data: {
            "appId": appId,
            "version": version,
            "key": key,
            "envId": envId,
            "value": value,
            "javaClient": javaClient,
            "autoReload": autoReload
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
