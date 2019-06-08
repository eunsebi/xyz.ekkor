<%--
  Created by IntelliJ IDEA.
  User: eunse
  Date: 2019-06-03
  Time: 오후 11:52
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <META http-equiv="Expires" content="-1">
    <META http-equiv="Pragma" content="no-cache">
    <META http-equiv="Cache-Control" content="No-Cache">
    <meta name="layout" content="main"/>
    <title>All That Community</title>
</head>

<body>
<g:sidebar/>
%{--<g:banner type="MAIN" />--}%

<div id="index" class="content scaffold-list clearfix" role="main">
    <div class="col-sm-6 main-block-left">
        <div class="main-block">
            <h4 class="main-header"><i class="fa fa-flag"></i> Editor's Choice</h4>
            <g:render template="article_block" model="[articles:choiceArticles]" />
        </div>
    </div>
    <div class="col-sm-6 main-block-left">
        <div class="main-block">
            <h4 class="main-header"><i class="fa fa-comment"></i> 커뮤니티 <a href="${request.contextPath}/articles/community" class="main-more-btn pull-right"><i class="fa fa-ellipsis-h"></i></a></h4>
            <g:render template="article_block" model="[articles:communityArticles]" />
        </div>
    </div>
    <div class="col-sm-6 main-block-right">
        <div class="main-block">
            <h4 class="main-header"><i class="fa fa-database"></i> Q&A <a href="${request.contextPath}/articles/questions" class="main-more-btn pull-right"><i class="fa fa-ellipsis-h"></i></a></h4>
            <g:render template="article_block" model="[articles:questionsArticles]" />
        </div>
    </div>
    <div class="col-sm-8 main-block-left">
        <div class="main-block">
            <h4 class="main-header"><i class="fa fa-code"></i> Tech <a href="${request.contextPath}/articles/tech" class="main-more-btn pull-right"><i class="fa fa-ellipsis-h"></i></a></h4>
            <g:if test="${techArticles}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <g:each in="${techArticles}" var="techArticle">
                            <div class="article-middle-block clearfix">
                                <div class="list-tag clearfix" style="">
                                    <g:categoryLabel category="${techArticle.category}" />
                                    <g:tags tags="${techArticle.tagString}" />
                                </div>
                                <h5><g:link controller="article" action="show" id="${techArticle.id}">${fieldValue(bean: techArticle, field: "title")}</g:link></h5>
                                <div class="list-group-item-author clearfix">
                                    <g:avatar avatar="${techArticle.displayAuthor}" class="pull-right" size="x-small" dateCreated="${techArticle.dateCreated}" />
                                </div>
                            </div>
                        </g:each>
                    </div>
                </div>
            </g:if>
        </div>
    </div>
    %{--<g:banner type="MAIN_BLOCK" />--}%
</div>
<content tag="script">
    <script>
        $(function () {
            $('.timeago').timeago();
        });
    </script>
</content>
</body>
</html>