<!doctype html>
<html lang="ko" class="no-js">
<head>
    %{--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <link href="//cdnjs.cloudflare.com/ajax/libs/summernote/0.8.2/summernote.css" rel="stylesheet"/>
    <script src="//cdnjs.cloudflare.com/ajax/libs/summernote/0.8.2/summernote.js"></script>

    <asset:stylesheet src="application.css"/>--}%

    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>ekkor - <g:layoutTitle default="All That Community"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'icon_57x57.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'icon_114x114.png')}">
    <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet">
    <meta property="og:image" content="${resource(dir: 'images', file: 'okky_logo_fb.png')}">

    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
    <script src="//code.jquery.com/jquery-3.2.1.min.js" integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4=" crossorigin="anonymous"></script>
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.js"></script>
    <link href="//cdnjs.cloudflare.com/ajax/libs/summernote/0.8.2/summernote.css" rel="stylesheet"/>
    <script src="//cdnjs.cloudflare.com/ajax/libs/summernote/0.8.2/summernote.js"></script>

    <asset:stylesheet src="application.css"/>

    <!--[if lt IE 9]>
    <asset:javascript src="libs/html5.js" />
    <![endif]-->

    <g:layoutHead/>
</head>
<body>

    <div class="navbar navbar-default navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/#">
		    <asset:image src="grails.svg" alt="Grails Logo"/>
                </a>
            </div>
            <div class="navbar-collapse collapse" aria-expanded="false" style="height: 0.8px;">
                <ul class="nav navbar-nav navbar-right">
                    <g:pageProperty name="page.nav" />
                </ul>
            </div>
        </div>
    </div>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <script>
        var contextPath = "${request.contextPath}", encodedURL = "${encodedURL()}";
    </script>
    <asset:javascript src="application.js" />
    %{--<asset:javascript src="apps/search.js" />
    --}%%{--<sec:ifLoggedIn>--}%%{--
        <asset:javascript src="apps/notification.js" />
    --}%%{--</sec:ifLoggedIn>--}%%{--
    <g:pageProperty name="page.script"/>--}%
    <asset:deferredScripts />

    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-6707625-5', 'auto');
        ga('send', 'pageview');

    </script>

</body>
</html>
