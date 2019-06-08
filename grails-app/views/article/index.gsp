<%@ page import="xyz.ekkor.Article" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'article.label', default: 'Article')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <g:sidebar category="${category}"/>
        %{--<a href="#list-article" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link uri="/articles/${params.code}/create" class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>--}%
    %{--<g:link class="create btn btn-success btn-wide pull-right" uri="/articles/${params.code}/create"><i class="fa fa-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link>--}%
        <div id="list-article" class="content scaffold-list" role="main">
            <div class="nav" role="navigation">
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <g:set var="isAdmin" value="${true}"/>
                </sec:ifAllGranted>
                <g:link class="create btn btn-success btn-wide pull-right" uri="/articles/${params.code}/create" action="create"><i class="fa fa-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link>

                <h4><g:message code="${category.labelCode}" default="${category.defaultLabel}" /></h4>
                <g:form name="category-filter-form" method="get" uri="/articles/${category.code}">
                    <div class="category-filter-wrapper">
                        <div class="category-filter-query pull-right">
                            <div class="input-group input-group-sm">
                                <input type="search" name="query" id="search-field" class="form-control" placeholder="검색어" value="${params.query}" />
                                <span class="input-group-btn">
                                    <button type="submit" class="btn btn-default"><i class="fa fa-search"></i></button>
                                    <g:if test="${params.query}">
                                        <g:link uri="/articles/${category.code}" class="btn btn-warning"><i class="fa fa-times-circle"></i> clear</g:link>
                                    </g:if>
                                </span>
                            </div>
                        </div>
                        <ul class="list-sort pull-left">
                            <li><g:link uri="/articles/${category.code}" params="[sort:'id', order:'desc']" data-sort="id" data-order="desc" class="category-sort-link ${params.sort == 'id' ? 'active':''}">최신순</g:link></li>
                            <li><g:link uri="/articles/${category.code}" params="[sort:'voteCount', order:'desc']" data-sort="voteCount" data-order="desc" class="category-sort-link ${params.sort == 'voteCount' ? 'active':''}">추천순</g:link></li>
                            <li><g:link uri="/articles/${category.code}" params="[sort:'noteCount', order:'desc']" data-sort="noteCount" data-order="desc" class="category-sort-link ${params.sort == 'noteCount' ? 'active':''}">댓글순</g:link></li>
                            <li><g:link uri="/articles/${category.code}" params="[sort:'scrapCount', order:'desc']" data-sort="scrapCount" data-order="desc" class="category-sort-link ${params.sort == 'scrapCount' ? 'active':''}">스크랩순</g:link></li>
                            <li><g:link uri="/articles/${category.code}" params="[sort:'viewCount', order:'desc']" data-sort="viewCount" data-order="desc" class="category-sort-link ${params.sort == 'viewCount' ? 'active':''}">조회순</g:link></li>
                        </ul>
                        <input type="hidden" name="sort" id="category-sort-input" value="${params.sort}"/>
                        <input type="hidden" name="order" id="category-order-input" value="${params.order}"/>
                    </div>
                </g:form>
            </div>
            <g:if test="${notices && notices?.size() > 0}">
            <div class="okkys-choice">
                <div class="panel panel-default">
                    <!-- Table -->
                    <ul class="list-group">
                        <g:each in="${notices}" status="i" var="article">
                            <g:render template="article" model="[article : article]"/>
                        </g:each>
                    </ul>
                </div>
            </div>
            </g:if>
            <g:if test="${choiceJobs && choiceJobs?.size() > 0}">
            <div class="okkys-choice">
                <div class="panel panel-default">
                    <!-- Table -->
                    <ul class="list-group">
                        <g:each in="${choiceJobs}" status="i" var="article">
                            <g:render template="recruit" model="[article : article]"/>
                        </g:each>
                    </ul>
                </div>
            </div>
            </g:if>
            <g:if test="${category?.code == 'jobs' || category?.parent?.code == 'jobs'}">
            <g:banner type="JOBS_TOP" />
            </g:if>
            <div class="panel panel-default">
                <!-- Table -->
                <ul class="list-group">

                    <g:if test="${articlesCount == 0}">
                        <li class="list-group-item clearfix">
                            <div class="panel-body text-center">
                                글이 없습니다.
                            </div>
                        </li>
                    </g:if>

                   <g:each in="${articleList}" status="i" var="article">
                   %{-- <g:if test="${article.isRecruit}">
                       <g:render template="recruit" model="[article : article]"/>
                   </g:if>
                   <g:else>--}%
                        <g:render template="article" model="[article : article]"/>
                    %{--</g:else>--}%
                    </g:each>
                </ul>
            </div>
            <div class="text-center">
                <g:if test="${articlesCount > 0}">
                    %{--<g:paginate uri="/articles/${category.code}" class="pagination-sm" total="${articlesCount ?: 0}" />--}%
                    <g:paginate total="${articleCount ?: 0}" />
                </g:if>
            </div>
        </div>
        %{--<content tag="script">--}%
        <script>
            $(function() {
                $('.category-sort-link').click(function(e) {
                    $('#category-sort-input').val($(this).data('sort'));
                    $('#category-order-input').val($(this).data('order'));
                    e.preventDefault();
                    $('#category-filter-form')[0].submit();
                });

                $('#search-field').focus(function() {
                    $('#job-filter').show();
                });

                $('#job-filter-close').click(function() {
                    $('#job-filter').hide();
                });

                $('#filter-job-group li').click(function() {
                    $('#filter-job-group li').removeClass('active');
                    $(this).addClass('active');

                    var id = $(this).data('id');

                    $('.filter-duty').hide();
//                  $('.filter-duty input').prop('checked', false);
                    $('#filter-duty-'+id).show();
                    $('#filter-group-input').val(id);
                });
            });
        </script>
        %{--</content>--}%
        %{--<div id="list-article" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${articleList}" />

            <div class="pagination">
                <g:paginate total="${articleCount ?: 0}" />
            </div>
        </div>--}%
    </body>
</html>