<%@ page import="xyz.ekkor.Article" %>
<%@ page import="xyz.ekkor.Content" %>
<%@ page import="xyz.ekkor.ContentTextType" %>

%{--
  Created by IntelliJ IDEA.
  User: eunse
  Date: 2019-05-17
  Time: 오후 5:15
--}%

<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
</head>

<body>
 form body


 <g:if test="${category?.anonymity}">
 %{--<div class="form-group ${hasErrors(bean: article, field: 'title', 'error')} has-feedback">
     <div class="alert alert-info">
         <ul>
             <li><b>블라블라</b> 블라블라</li>
         </ul>
     </div>
 </div>--}%
 </g:if>

<g:if test="${!article.id || !article.category?.anonymity}">
    <sec:ifAllGranted roles="ROLE_ADMIN">

        <div class="form-group ${hasErrors(bean: article, field: 'choice', 'has-error')} has-feedback">
            <div class="checkbox">
                <label>
                    <g:checkBox name="choice" value="${article?.choice}"/>
                    <g:message code="article.choice.label" default="Editor`s Choice"/>
                </label>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <label>
                    <g:checkBox name="disabled" value="${!article?.enabled}"/>
                    <g:message code="article.disabled.label" default="게시물 비공개 (관리자만 접근가능)"/>
                </label>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <label>
                    <g:checkBox name="ignore" value="${article?.ignoreBest}"/>
                    <g:message code="article.ignore.label" default="Weekly Best 제외"/>
                </label>
            </div>
        </div>

        <div class="form-group ${hasErrors(bean: article, field: 'choice', 'has-error')} has-feedback">
            <div class="checkbox">
                <label>
                    <g:checkBox name="notice" value="${notices?.size() > 0}"/>
                    <g:message code="article.notice.label" default="카테고리 공지"/>
                </label>
            </div>

            <div class="alert alert-info" id="noticeCategoryList"
                 style="display: ${notices?.size() > 0 ? "block" : "none"}">
                <g:each in="${categories}" var="category">
                    <label>
                        <input type="checkbox" name="notices" value="${category.code}"
                               <g:if test="${notices*.categoryId.contains(category.code)}">checked="checked"</g:if>> ${message(code: category.labelCode, default: category.defaultLabel)}
                    </label>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                </g:each>
            </div>
        </div>

        <div class="form-group ${hasErrors(bean: article, field: 'category', 'has-error')} has-feedback">
            <div>
                <select id="category" name="categoryCode" class="form-control">
                    <option value="">게시판을 선택해 주세요.</option>
                    <g:each in="${writableCategories}" var="category">
                        <option value="${category.id}"
                                <g:if test="${category.code == article?.category?.code}">selected="selected"</g:if>>${message(code: category.labelCode, default: category.defaultLabel)}</option>
                    </g:each>
                </select>
            </div>
        </div>
    </sec:ifAllGranted>

    <sec:ifNotGranted roles="ROLE_ADMIN">
        <g:if test="${writableCategories.size() > 1}">
            <div class="form-group ${hasErrors(bean: article, field: 'category', 'has-error')} has-feedback">
                <div>
                    <select id="category" name="categoryCode" class="form-control">
                        <option value="">게시판을 선택해 주세요.</option>
                        <g:each in="${writableCategories}" var="category">
                            <option value="${category.code}"
                                    <g:if test="${category.code == article?.category?.code}">selected="selected"</g:if>
                                    data-external="${category.writeByExternalLink}"
                                    data-anonymity="${category.anonymity}">
                                ${message(code: category.labelCode, default: category.defaultLabel)}
                            </option>
                        </g:each>
                    </select>
                </div>
            </div>
        </g:if>
        <g:else>
            <g:hiddenField name="categoryCode" value="${writableCategories?.getAt(0).code}"/>
        </g:else>
    </sec:ifNotGranted>
</g:if>

category

<div class="form-group ${hasErrors(bean: article, field: 'title', 'has-error')} has-feedback">
    <div>
        <g:textField name="title" required="" value="${article?.title}" placeholder="제목을 입력해 주세요." class="form-control"/>
    </div>
</div>

<div class="form-group ${hasErrors(bean: article, field: 'tagString', 'has-error')} has-feedback">
    <div>
        <g:textField name="tagString" value="${article?.tagString}" placeholder="Tags," data-role="tagsinput" class="form-control"/>
    </div>
</div>

content
<div class="form-group ${hasErrors(bean: article.content, field: 'text', 'has-error')} has-feedback">
    <g:if test="${article?.content?.textType == ContentTextType.MD}">MD
        <g:textArea name="content.text" id="summernote" value="${markdown.renderHtml([text: article?.content?.text])}" rows="20" class="form-control input-block-level" />
    </g:if>
    <g:elseif test="${article?.content?.textType == ContentTextType.HTML}">HTML
        <g:textArea name="content.text" id="summernote" value="${filterHtml([text: article?.content?.text])}" rows="20" class="form-control input-block-level" />
    </g:elseif>
    <g:else>text
        <g:textArea name="content.text" id="summernote" value="${lineToBr([text: article?.content?.text])}" rows="20" class="form-control input-block-level" />
    </g:else>
</div>

<g:hiddenField name="content.textType" value="HTML"/>
<asset:script type="text/javascript">
    $(document).ready(function() {
        $('#summernote').summernote({
            height: 300,
            minHeight: null,
            maxHeight: null,
            lang : 'ko-KR',
            callbacks: {
                onImageUpload : function(files, editor, welEditable) {
                    console.log('image upload:', files);
                    sendFile(files[0], editor, welEditable);
                }
            }
        });

        // summernote 에 값을 기록(set)
        // var markupStr = 'hello world';
        // $('.summernote').summernote('code', markupStr);

        // summernote 에디터에 이미지 업로드
        function sendFile(file,editor,welEditable) {
            data = new FormData();
            data.append("file", file);
            $.ajax({
                url: "/file/uploadImg", // image 저장 경로
                data: data,
                cache: false,
                contentType: false,
                enctype: 'multipart/form-data',
                processData: false,
                type: 'POST',
                success: function(data){
                    $('#summernote').summernote('insertImage', data.url); // summernote 에디터에 img 태그를 보여줌
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(textStatus+" "+errorThrown);
                }
            });
        }
    });

     function postForm() {
         $('textarea[name="content.text"]').val($('#summernote').code());
     }

     $('#notice').click(function() {
       if($(this).is(':checked')) {
         $('#noticeCategoryList').show();
       } else {
         $('#noticeCategoryList').hide();
         $('input[name="notices"]').prop('checked', false);
       }
     });
</asset:script>
%{--<fieldset class="form">
    <f:all bean="article"/>
</fieldset>--}%

</body>
</html>