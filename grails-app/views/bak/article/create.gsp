<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'article.label', default: 'Article')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-article" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                %{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
                <li><g:link uri="/articles/${category.id}" class="link"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-article" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:hasErrors bean="${this.article}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.article}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:hasErrors bean="${article.content}">
                <div class="alert alert-danger alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <ul>
                        <g:eachError bean="${article.content}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </div>
            </g:hasErrors>
form
            %{--<div class="panel-heading clearfix">
                <g:if test="${category?.anonymity}">
                    <g:avatar avatar="${article.displayAuthor}" size="medium" class="pull-left" />
                </g:if>
                <g:else>
                    <g:avatar avatar="${article.displayAuthor}" size="medium" class="pull-left" />
                </g:else>
            </div>--}%

            %{--<g:form resource="${this.article}" method="POST">--}%
            <g:form id="article-form" url="[resource:article, uri: '/articles/'+params.code+'/save']" useToken="true" class="article-form"
                    role="form" onsubmit="return postForm()" enctype="multipart/form-data">
                <fieldset class="form">
                    <g:render template="form"/>
                    %{--<f:all bean="article"/>--}%
                </fieldset>
                <fieldset class="buttons">
                    <g:link uri="/articles/${params.code}" class="btn btn-default btn-wide" onclick="return confirm('정말로 취소하시겠습니까?')"><g:message code="default.button.cancel.label" default="Cancel"/></g:link>
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>

    <asset:script type="text/javascript">
        $('#category').change(function() {
            if(this.value && confirm('게시판 변경시 수정된 내용은 초기화 됩니다. 변경 하시겠습니까?')) {
              /*if(this.value == 'recruit') {
                location.href=contextPath+'/recruit/create';
              } else {*/
                location.href=contextPath+'/articles/'+this.value+'/create';
              // }
            }
        });
    </asset:script>

    </body>
</html>
