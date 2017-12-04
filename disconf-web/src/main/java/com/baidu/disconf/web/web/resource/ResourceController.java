package com.baidu.disconf.web.web.resource;

import com.baidu.disconf.web.service.config.form.RoleResourceForm;
import com.baidu.disconf.web.service.resource.ResourceMapping;
import com.baidu.disconf.web.service.resource.ResourceMappingVO;
import com.baidu.disconf.web.service.roleres.bo.RoleResource;
import com.baidu.disconf.web.service.roleres.service.RoleResourceMgr;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author: hama
 * @date: created in  2017/11/19
 * @description: 要求在定义接口RequestMapping注解时，需详尽
 * <p>
 * 与权限配置有关的接口
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/resource")
public class ResourceController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);


    private List<ResourceMapping> requestToMethodItemList = new ArrayList<ResourceMapping>();


    @Autowired
    private RoleResourceMgr roleResourceMgr;

    @NoAuth
    @RequestMapping(value = "/list/role/{roleId}")
    @ResponseBody
    public JsonObjectBase getRoleResource(HttpServletRequest request, @PathVariable("roleId") int roleId) {
        // 获取某个角色所有的role_resource
        List<RoleResource> list = roleResourceMgr.getRoleResource(roleId);
        if (requestToMethodItemList.isEmpty()) {
            fillIn(request);
        }
        return buildSuccess("resource", tranfterVO(list));
    }


    /**
     * 用于获取spring所有支持的请求，也就是{@link RequestMapping}的url
     * 要求在定义接口RequestMapping注解时，需详尽
     *
     * @return
     */
    @NoAuth
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "于获取spring所有支持的请求，也就是")
    @ResponseBody
    public JsonObjectBase list(HttpServletRequest request) {
        fillIn(request);
        return buildSuccess(ImmutableMap.of("size", requestToMethodItemList.size(), "resource", requestToMethodItemList));
    }

    /**
     * 填充requestToMethodItemList
     */

    public void fillIn(HttpServletRequest request) {

        if (!requestToMethodItemList.isEmpty()) {
            return;
        }
        ServletContext servletContext = request.getSession().getServletContext();
        if (servletContext == null) {
            return;
        }
        WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext,
                HandlerMapping.class, true, false);


        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
            //本项目只需要RequestMappingHandlerMapping中的URL映射
            if (handlerMapping instanceof RequestMappingHandlerMapping) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
                Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : handlerMethods.entrySet()) {


                    RequestMappingInfo requestMappingInfo = requestMappingInfoHandlerMethodEntry.getKey();
                    HandlerMethod mappingInfoValue = requestMappingInfoHandlerMethodEntry.getValue();

                    // 尚需排除@oAuth注解修饰的接口
                    NoAuth noAuth = mappingInfoValue.getMethodAnnotation(NoAuth.class);
                    if (null != noAuth) {
                        continue;
                    }

                    /**
                     * 开始获取需要利用的信息
                     */

                    // 接口定义的请求方式 GET POST PUT DELETE...
                    RequestMethodsRequestCondition methodCondition = requestMappingInfo.getMethodsCondition();
                    String srcMethod = "";
                    Set<RequestMethod> methodSet = methodCondition.getMethods();
                    for (Iterator iter = methodSet.iterator(); iter.hasNext(); ) {
                        RequestMethod str = (RequestMethod) iter.next();
                        srcMethod += str.name();
                    }

                    // 获取mapping地址
                    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
                    String srcUrl = patternsCondition.getPatterns().iterator().next();

                    // 接口所在的类名
                    String srcClassName = mappingInfoValue.getBeanType().toString();

                    // 接口定义的方法名
                    String srcMethodName = mappingInfoValue.getMethod().getName();

                    // 接口参数类型
                    Class<?>[] srcMethodParamType = mappingInfoValue.getMethod().getParameterTypes();

                    // 接口用途备注，资源介绍
                    String srcDescription = requestMappingInfo.getName();

                    ResourceMapping item = new ResourceMapping(srcUrl, srcMethod, srcClassName, srcMethodName,
                            srcMethodParamType, srcDescription);

                    requestToMethodItemList.add(item);
                }
                break;
            }
        }

    }

    // 转换
    public List<ResourceMappingVO> tranfterVO(List<RoleResource> roleResourceList) {
        List<ResourceMappingVO> result = new ArrayList<ResourceMappingVO>();
        ResourceMappingVO rmVO = null;
        for (ResourceMapping resourceMapping : requestToMethodItemList
                ) {
            rmVO = new ResourceMappingVO(resourceMapping);
            for (RoleResource roleResource : roleResourceList
                    ) {
                if (resourceMapping.getSrcUrl().equals(roleResource.getUrlPattern())) {
                    // enrich
                    rmVO.setAccess(true);
                    rmVO.setRoleResId(roleResource.getId());
                    rmVO.setUpdateTime(roleResource.getUpdateTime());
                }
            }
            result.add(rmVO);
        }

        return result;
    }


    @NoAuth
    @RequestMapping(value = "/auth", method = RequestMethod.POST, name = "授予权限项")
    @ResponseBody
    public JsonObjectBase auth(RoleResourceForm roleResourceForm) {
        // 获取某个角色所有的role_resource
        roleResourceMgr.createOne(roleResourceForm);
        // 清除缓存
        roleResourceMgr.evictCache();
        return buildSuccess("auth", true);
    }

    @NoAuth
    @RequestMapping(value = "/cancel/{roleResId}", method = RequestMethod.DELETE, name = "取消权限项")
    @ResponseBody
    public JsonObjectBase cancel(@PathVariable("roleResId") int roleResId) {
        // 获取某个角色所有的role_resource
        roleResourceMgr.delete(roleResId);
        // 清除缓存
        roleResourceMgr.evictCache();
        return buildSuccess("cancel", true);
    }

    @NoAuth
    @RequestMapping(value = "/test", method = RequestMethod.GET, name = "测试测试测试")
    @ResponseBody
    public JsonObjectBase test() {
        // 获取某个角色所有的role_resource
        roleResourceMgr.evictCache();
        return buildSuccess("resource", "");
    }

}
