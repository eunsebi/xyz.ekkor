<%@ page import="xyz.ekkor.Category" %>
<g:set var="isSub" value="${!!category}"/>
<g:set var="parentCategory" value="${category?.parent ?: category}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'article.label', default: 'Article')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-article" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                %{--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
                <g:each in="${Category.getTopCategories()}" var="category">
                    <li
                        <g:if test="${category.code == parentCategory?.code}">class="active"</g:if>
                        <g:if test="${isSub}">data-toggle="tooltip" data-placement="right" data-container="body" title="<g:message code="${category.labelCode}" default="${category.defaultLabel}" />"</g:if>
                    >
                        <g:link uri="/articles/${category.id}" class="link"><i class="nav-icon ${category.iconCssNames}"></i> <span class="nav-sidebar-label nav-sidebar-category-label"><g:message code="${category.labelCode}" default="${category.defaultLabel}" /></span></g:link>
                    </li>
                </g:each>
            </ul>
        </div>
    <!-- 임시 -->
    <g:link class="create btn btn-success btn-wide pull-right" uri="/articles/${params.code}/create"><i class="fa fa-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link>

    <h4><g:message code="${category.labelCode}" default="${category.defaultLabel}" /></h4>
    <!-- 임시-->
        <div id="list-article" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${articleList}" />

            <div class="pagination">
                <g:paginate total="${articleCount ?: 0}" />
            </div>
        </div>
    </body>
</html>