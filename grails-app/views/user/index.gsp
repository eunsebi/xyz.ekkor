<%@ page import="xyz.ekkor.User" %>
<%@ page import="xyz.ekkor.Activity" %>
<%@ page import="xyz.ekkor.Content" %>
<%@ page import="xyz.ekkor.ContentType" %>
<%@ page import="xyz.ekkor.ActivityType" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
    <g:sidebar/>
    <div id="user" class="content clearfix" role="main">
        <div class="panel panel-default">
            <div class="panel-body">
                <g:avatar size="big" avatar="${avatar}" pictureOnly="true" class="col-sm-3 text-center" />
                <div class="user-info col-sm-9">
                    <div class="clearfix">
                        <h2 class="pull-left">${avatar.nickname}</h2>
                        <button class="btn btn-success pull-right btn-wide disabled"><i class="fa fa-plus"></i> 팔로우</button>
                    </div>
                    <div class="user-points">
                        <div class="user-point">
                            <div class="user-point-label"><i class="fa fa-flash"></i> 활동점수</div>
                            <div class="user-point-num"><g:link uri="/user/info/${avatar.id}/activity">${avatar.activityPoint}</g:link></div>
                        </div>
                        <div class="user-point">
                            <div class="user-point-label"><i class="fa fa-user"></i> 팔로잉</div>
                            <div class="user-point-num"><a href="#">${counts.followingCount}</a></div>
                        </div>
                        <div class="user-point">
                            <div class="user-point-label"><i class="fa fa-users"></i> 팔로워</div>
                            <div class="user-point-num"><a href="#">${counts.followerCount}</a></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-2 user-info-nav pull-right">
            <ul class="nav">
                <li class="${!params.category || params.category == 'activity' ? 'active' : ''}"><g:link uri="/user/info/${avatar.id}/activity">최근 활동</g:link> </li>
                <li class="${params.category == 'articles' ? 'active' : ''}"><g:link uri="/user/info/${avatar.id}/articles">게시물 <g:if test="${counts.postedCount > 0}"><span class="badge">${counts.postedCount}</span></g:if></g:link></li>
                %{--<li class="${params.category == 'solved' ? 'active' : ''}"><g:link uri="/user/info/${avatar.id}/solved"><g:if test="${counts.solvedCount > 0}"><span class="badge">${counts.solvedCount}</span></g:if> 질문 해결</g:link></li>--}%
                <li class="${params.category == 'scrapped' ? 'active' : ''}"><g:link uri="/user/info/${avatar.id}/scrapped">스크랩 <g:if test="${counts.scrappedCount > 0}"><span class="badge">${counts.scrappedCount}</span></g:if></g:link></li>
            </ul>
        </div>
        <div class="col-sm-10 main-block-left pull-left">
            <ul class="list-group">

                <g:each in="${activities}" var="activity">

                    <g:set var="article" value="${activity.article}"/>

                    <g:set var="evaluateClass" value="no-note" />

                    <g:if test="${article.selectedNoteId}">
                        <g:set var="evaluateClass" value="success" />
                    </g:if>
                    <g:elseif test="${article.noteCount > 0}">
                        <g:set var="evaluateClass" value="has-note" />
                    </g:elseif>

                    <li class="list-group-item list-group-item-small ${category?.useEvaluate ? 'list-group-item-question':''} list-group-${evaluateClass} clearfix">
                        <div class="list-icon-wrapper pull-left">
                            <g:if test="${activity.type == ActivityType.POSTED}"><i class="fa fa-pencil"></i></g:if>
                            <g:if test="${activity.type == ActivityType.NOTED}"><i class="fa fa-comment-o"></i></g:if>
                            <g:if test="${activity.type == ActivityType.ASSENTED_ARTICLE}">
                                <g:if test="${article.category.useEvaluate}"><i class="fa fa-angle-up fa-lg"></i></g:if>
                                <g:else><i class="fa fa-thumbs-up"></i></g:else>
                            </g:if>
                            <g:if test="${activity.type == ActivityType.ASSENTED_NOTE}">
                                <g:if test="${article.category.useEvaluate}"><i class="fa fa-angle-up fa-lg"></i></g:if>
                                <g:else><i class="fa fa-thumbs-up"></i></span></g:else>
                            </g:if>
                            <g:if test="${activity.type == ActivityType.DISSENTED_ARTICLE}"><i class="fa fa-angle-down fa-lg"></i></g:if>
                            <g:if test="${activity.type == ActivityType.DISSENTED_NOTE}"><i class="fa fa-angle-down fa-lg"></i></g:if>
                            <g:if test="${activity.type == ActivityType.SCRAPED}"><i class="fa fa-bookmark"></i></g:if>
                        </div>
                        <div class="list-title-wrapper list-activity">
                            <div class="list-activity-desc">
                                <span class="list-activity-desc-text">
                                    <g:if test="${activity.type == ActivityType.POSTED}">
                                        <g:categoryLabel category="${article.category}" />
                                        <g:if test="${article.category.code == 'questions'}">에 #${article.id} 질문을 올렸습니다.</g:if>
                                        <g:else>에 #${article.id} 게시물을 작성하였습니다.</g:else>
                                    </g:if>
                                    <g:if test="${activity.type == ActivityType.NOTED}">
                                        #${article.id}
                                        <g:if test="${article.category.code == 'questions'}">게시물에 답변을 올렸습니다.</g:if>
                                        <g:else>게시물에 댓글을 남겼습니다.</g:else>
                                    </g:if>
                                    <g:if test="${activity.type == ActivityType.ASSENTED_ARTICLE}">#${article.id} 게시물을 추천 하였습니다.</g:if>
                                    <g:if test="${activity.type == ActivityType.ASSENTED_NOTE}">
                                        #${article.id}
                                        <g:if test="${article.category.useEvaluate}">질문의 <g:link uri="/user/info/${activity?.content?.author.id}" class="nickname">${activity?.content?.author?.nickname}</g:link>님의 답변을 추천 하였습니다.</g:if>
                                        <g:else>게시물의 <g:link uri="/user/info/${activity?.content?.author?.id}" class="nickname">${activity?.content?.author.nickname}</g:link>님의 댓글을 추천 하였습니다.</g:else>
                                    </g:if>
                                    <g:if test="${activity.type == ActivityType.DISSENTED_ARTICLE}">#${article.id} 질문을 반대 하였습니다.</g:if>
                                    <g:if test="${activity.type == ActivityType.DISSENTED_NOTE}">#${article.id} 질문의 <g:link uri="/user/info/${activity?.content?.author?.id}" class="nickname">${activity?.content?.author?.nickname}</g:link>님의 답변에 반대 하였습니다.</g:if>
                                    <g:if test="${activity.type == ActivityType.SCRAPED}">#${article.id} 게시물을 스크랩 하였습니다.</g:if>
                                </span>
                                <span class="timeago" title="${activity.dateCreated}">${activity.dateCreated}</span>
                            </div>
                            <h5 class="list-group-item-heading ${category?.useEvaluate ? 'list-group-item-evaluate' : ''}">
                                <g:set var="linkParams" />
                                <g:if test="${activity?.content?.type == ContentType.NOTE}">
                                    <g:set var="linkParams" value="[note:activity?.content?.id]" />
                                </g:if>
                                <g:link controller="article" action="show" id="${article.id}" params="${linkParams}">${fieldValue(bean: article, field: "title")}</g:link>
                                <div class="list-group-item-author pull-right clearfix">
                                    <g:avatar avatar="${article.displayAuthor}" size="x-small"/>
                                </div>
                            </h5>
                        </div>
                    </li>
                </g:each>
            </ul>
            <div class="text-center">
                <g:if test="${activitiesCount > 0}">
                    <g:paginate uri="/user/info/${avatar.id}/${params.category?: 'activity'}" controller="user" action="index" id="" class="pagination-sm" total="${activitiesCount ?: 0}" />
                </g:if>
            </div>
        </div>
    </div>

    %{-- --}%
        %{--<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-user" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${userList}" />

            <div class="pagination">
                <g:paginate total="${userCount ?: 0}" />
            </div>
        </div>--}%
    </body>
</html>