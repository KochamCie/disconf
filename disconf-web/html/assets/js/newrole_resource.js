/**
 * Created by hama on 2017/11/20.
 */

var roleId = -1;
function auth(obj) {
    //e.unbind('click');
    var roleResId = $(obj).attr("data-role-res-id")
    var urlPattern = $(obj).attr("data-url-pattern")
    var methodMask = $(obj).attr("data-method-mask")
    var urlDescription = $(obj).attr("data-url-description")
    if (0 == roleResId) {
        // auth
        $.ajax({
            type: "POST",
            url: "/api/resource/auth",
            data: {
                "roleId": roleId,
                "urlPattern": urlPattern,
                "methodMask": methodMask,
                "urlDescription": urlDescription
            }
        }).done(
            function (data) {
                if (data.success === "true") {
                    var html = "";
                    alert("授予访问权限成功")
                    window.location.reload()
                }
            });
    } else {
        // cancel
        $.ajax({
            type: "DELETE",
            url: "/api/resource/cancel/"+roleResId
        }).done(
            function (data) {
                if (data.success === "true") {
                    var html = "";
                    alert("取消访问权限成功")
                    window.location.reload()
                }
            });
    }
};

$(function () {


    getSession();

    var version = "#";


    //
    // 获取Role信息
    //
    $.ajax({
        type: "GET",
        url: "/api/role/list"
    }).done(
        function (data) {
            if (data.success === "true") {
                var html = "";
                var result = data.page.result;
                $.each(result, function (index, item) {
                    html += '<li><a rel=' + item.id + ' href="#">'
                        + item.roleName + ' </a></li>';
                });
                $("#roleChoice").html(html);
            }
        });
    $("#roleChoice").on('click', 'li a', function () {
        roleId = $(this).attr('rel');
        $("#env_info").html($(this).text());
        $("#roleChoice li").removeClass("active");
        $(this).parent().addClass("active");
        version = "#";
        fetchMainList(roleId);
    });


    function fetchRoleResource(roleId) {
        // 请求
        $.ajax({
            type: "GET",
            url: "/api/resource/list/role/" + roleId
        }).done(
            function (data) {
                if (data.success === "true") {
                    var html = "";
                    var resources = data.result.resource;
                    var roleResources = data.result.roleResource;

                    // 展示所有的resource

                    $.each(resources, function (index, item) {
                        html += '<li><a rel=' + item.id + ' href="#">'
                            + item.roleName + ' </a></li>';
                    });
                    $("#roleChoice").html(html);
                }
            });
    }


    fetchMainList();

    //
    // 渲染主列表
    //
    function fetchMainList() {

        // 参数不正确，清空列表
        if (roleId == -1) {
            $("#mainlist_error").text("请选择" + getTips()).show();
            $("#accountBody").html("");
            $("#mainlist").hide();
            $("#zk_deploy").hide();
            return;
        }
        $("#mainlist_error").hide();
        if (version == "#") {
        }

        var parameter = ""


        $.ajax({
            type: "GET",
            url: "/api/resource/list/role/" + roleId
        }).done(function (data) {
            if (data.success === "true") {
                var html = "";
                //var result = data.page.result;
                var resources = data.result.resource;

                $.each(resources, function (index, item) {
                    html += renderItem(item, index);
                });
                if (html != "") {
                    $("#mainlist").show();
                    $("#accountBody").html(html);
                } else {
                    $("#accountBody").html("");
                }

            } else {
                $("#accountBody").html("");
                $("#mainlist").hide();
            }

        });
        var mainTpl = $("#tbodyTpl").html();
        //console.log(mainTpl)
        // 渲染主列表
        function renderItem(item, i) {

            var test_url = '<a href="javascript:void(0);" onclick="auth(this)" class="auth" data-url-pattern="' + item.srcUrl + '"' +
                ' data-method-mask="' + item.srcMethod + '" data-url-description="' + item.srcDescription + '"' +
                ' data-role-res-id="' + item.roleResId + '">' +
                '<i title="' + (item.access ? "取消访问权限" : "授予访问权限") + '" class="' + (item.access ? "icon-ban-circle" : "icon-ok-circle") + '"></i></a>';

            return Util.string.format(mainTpl, i + 1, item.srcUrl, item.srcDescription, item.srcMethod, item.updateTime, test_url);
        }
    }

    //
    function getTips() {
        if (roleId == -1) {
            return "角色";
        }
        return "参数";
    }
})
