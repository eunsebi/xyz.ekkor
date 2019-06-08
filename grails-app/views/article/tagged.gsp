%{--
  Created by IntelliJ IDEA.
  User: eunse
  Date: 2019-02-07
  Time: 오후 10:46
--}%

<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="xyz.ekkor.Article" %>
<html>
<head>
    <meta name="layout" content="main">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <g:set var="entityName" value="${message(code: 'article.label', default: 'Article')}" />
    <title>Tagged ${params.tag}</title>
</head>
<body>
<g:sidebar />
<div id="list-article" class="content scaffold-list" role="main">
    <div class="nav" role="navigation">
        %{--<g:link class="create btn btn-success btn-wide pull-right" uri="/articles/${params.code}/create"><i class="fa fa-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link>--}%

        <h4>Tagged <span class="item-tag label label-gray">${params.tag}</span></h4>
        <div class="category-filter-wrapper">
            <g:form name="category-filter-form" method="get" uri="/articles/tagged/${params.tag}">
                <div class="category-filter-query pull-right">
                    <div class="input-group input-group-sm">
                        <input type="search" name="query" class="form-control" placeholder="검색어" value="${params.query}" />
                        <span class="input-group-btn">
                            <button type="submit" class="btn btn-default"><i class="fa fa-search"></i></button>
                            <g:if test="${params.query}">
                                <g:link uri="/articles/tagged/${params.tag}" class="btn btn-warning"><i class="fa fa-times-circle"></i> clear</g:link>
                            </g:if>
                        </span>
                    </div>
                </div>
                <ul class="list-sort pull-left">
                    <li><g:link uri="/articles/tagged/${params.tag}" params="[sort:'id', order:'desc']" data-sort="id" data-order="desc" class="category-sort-link ${params.sort == 'id' ? 'active':''}">최신순</g:link></li>
                    <li><g:link uri="/articles/tagged/${params.tag}" params="[sort:'voteCount', order:'desc']" data-sort="voteCount" data-order="desc" class="category-sort-link ${params.sort == 'voteCount' ? 'active':''}">추천순</g:link></li>
                    <li><g:link uri="/articles/tagged/${params.tag}" params="[sort:'noteCount', order:'desc']" data-sort="noteCount" data-order="desc" class="category-sort-link ${params.sort == 'noteCount' ? 'active':''}">댓글순</g:link></li>
                    <li><g:link uri="/articles/tagged/${params.tag}" params="[sort:'scrapCount', order:'desc']" data-sort="scrapCount" data-order="desc" class="category-sort-link ${params.sort == 'scrapCount' ? 'active':''}">스크랩순</g:link></li>
                    <li><g:link uri="/articles/tagged/${params.tag}" params="[sort:'viewCount', order:'desc']" data-sort="viewCount" data-order="desc" class="category-sort-link ${params.sort == 'viewCount' ? 'active':''}">조회순</g:link></li>
                </ul>
                <input type="hidden" name="sort" id="category-sort-input" value="${params.sort}"/>
                <input type="hidden" name="order" id="category-order-input" value="${params.order}"/>
            </g:form>
        </div>

    </div>
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

                <g:set var="evaluateClass" value="no-note" />

                <g:if test="${article.selectedNote}">
                    <g:set var="evaluateClass" value="success" />
                </g:if>
                <g:elseif test="${article.noteCount > 0}">
                    <g:set var="evaluateClass" value="has-note" />
                </g:elseif>

                <li class="list-group-item ${category?.useEvaluate ? 'list-group-item-question':''} list-group-${evaluateClass} clearfix">

                    <div class="list-title-wrapper clearfix">
                        <div class="list-tag clearfix">
                            <span class="list-group-item-text article-id">#${article.id}</span>
                            <g:categoryLabel category="${article.category}" />
                            <g:tags tags="${article.tagString}" />
                        </div>

                        <h5 class="list-group-item-heading ${category?.useEvaluate ? 'list-group-item-evaluate' : ''}">
                            <g:link controller="article" action="show" id="${article.id}">${fieldValue(bean: article, field: "title")}</g:link>
                        </h5>
                    </div>

                    <div class="list-summary-wrapper clearfix">
                        <g:if test="${article.category?.useEvaluate}">
                            <div class="item-evaluate-wrapper pull-right clearfix">
                                <div class="item-evaluate">
                                    <div class="item-evaluate-icon">
                                        <i class="item-icon fa fa-thumbs-o-up"></i>
                                    </div>
                                    <div class="item-evaluate-count">
                                        <span><g:shorten number="${article.voteCount}" />
                                    </div>
                                </div>
                                <div class="item-evaluate item-evaluate-${evaluateClass}">
                                    <div class="item-evaluate-icon">
                                        <g:if test="${evaluateClass == 'no-note'}">
                                            <i class="item-icon fa fa-question-circle"></i>
                                        </g:if>
                                        <g:elseif test="${evaluateClass == 'has-note'}">
                                            <i class="item-icon fa fa-exclamation-circle"></i>
                                        </g:elseif>
                                        <g:elseif test="${evaluateClass == 'success'}">
                                            <i class="item-icon fa fa-check-circle"></i>
                                        </g:elseif>
                                    </div>
                                    <div class="item-evaluate-count">
                                        <g:shorten number="${article.noteCount}" />
                                    </div>
                                </div>
                            </div>
                        </g:if>
                        <g:else>
                            <div class="list-group-item-summary clearfix">
                                <ul>
                                    <li><i class="item-icon fa fa-comment"></i> <g:shorten number="${article.noteCount}" /></li>
                                    <li><i class="item-icon fa fa-thumbs-up"></i> <g:shorten number="${article.voteCount}" /></li>
                                    <li><i class="item-icon fa fa-eye"></i> <g:shorten number="${article.viewCount}" /></li>
                                </ul>
                            </div>
                        </g:else>
                    </div>

                    <div class="list-author-wrapper clearfix">
                        <div class="list-group-item-author clearfix">
                            <g:avatar avatar="${article.displayAuthor}" size="list" dateCreated="${article.dateCreated}" />
                        </div>
                    </div>
                </li>
            </g:each>
        </ul>
    </div>
    <div class="text-center">
        <g:if test="${articlesCount > 0}">
            <g:paginate uri="/articles/tagged/${params.tag}" class="pagination-sm" total="${articlesCount ?: 0}" />
        </g:if>
    </div>
</div>
<content tag="script">
    <script>
        $(function() {
            $('.category-sort-link').click(function(e) {
                $('#category-sort-input').val($(this).data('sort'));
                $('#category-order-input').val($(this).data('order'));
                e.preventDefault();
                $('#category-filter-form')[0].submit();
            });
        });
    </script>
</content>
</body>
</html>
