import javax.persistence.*

grails.gorm.default.mapping = {
	'*'(accessType: AccessType.PROPERTY)
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'xyz.ekkor.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'xyz.ekkor.UserRole'
grails.plugin.springsecurity.authority.className = 'xyz.ekkor.Role'

//2019. 02. 12 추가
grails.plugin.springsecurity.password.algorithm = 'bcrypt'
grails.plugin.springsecurity.logout.postOnly = false

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
		[pattern: '/',               access: ['permitAll']],
		[pattern: '/error',          access: ['permitAll']],
		[pattern: '/index',          access: ['permitAll']],
		[pattern: '/index.gsp',      access: ['permitAll']],
		[pattern: '/shutdown',       access: ['permitAll']],
		[pattern: '/assets/**',      access: ['permitAll']],
		[pattern: '/**/js/**',       access: ['permitAll']],
		[pattern: '/**/css/**',      access: ['permitAll']],
		[pattern: '/**/images/**',   access: ['permitAll']],
		[pattern: '/**/favicon.ico', access: ['permitAll']],
		[pattern: '/admin/dbconsole/**', access: ['permitAll']],

		// 추가
		[pattern: '/main/index',          access: ['permitAll']],
		[pattern: '/category/**', 	 access: ['ROLE_ADMIN', 'ROLE_USER']],
		[pattern: '/article/**', 	 access: ['ROLE_ADMIN', 'ROLE_USER']],
		[pattern: '/articles/**', 	 access: ['ROLE_ADMIN', 'ROLE_USER']],
		[pattern: '/money/**', 	 access: ['ROLE_ADMIN', 'ROLE_USER']],

		[pattern: '/user/**', 	 access: ['permitAll']],
		[pattern: '/user/*/**', 	 access: ['permitAll']],
		[pattern: '/user/info/*', 	 access: ['permitAll']],
		[pattern: '/user/edit', 	 access: ['ROLE_USER']],
		[pattern: '/user/update', 	 access: ['ROLE_USER']],
		[pattern: '/user/withdraw', 	 access: ['ROLE_USER']],
		[pattern: '/user/withdrawConfirm', 	 access: ['ROLE_USER']],
		[pattern: '/user/passwordChange', 	 access: ['ROLE_USER']],
		[pattern: '/user/updatePasswordChange', 	 access: ['ROLE_USER']],
		[pattern: '/find/user/**', 	 access: ['permitAll']],
		[pattern: '/find/user/index', 	 access: ['permitAll']],
		[pattern: '/file/**', 	 access: ['ROLE_USER']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
		[pattern: '/assets/**',      filters: 'none'],
		[pattern: '/**/js/**',       filters: 'none'],
		[pattern: '/**/css/**',      filters: 'none'],
		[pattern: '/**/images/**',   filters: 'none'],
		[pattern: '/**/favicon.ico', filters: 'none'],
		[pattern: '/**',             filters: 'JOINED_FILTERS']

]

/*
groovyenvironments {
	production {
		grails.serverURL = "http://ekkor.xyz"
		grails.dbconsole.enabled = true
		grails.dbconsole.urlRoot = '/admin/dbconsole'
	}
	development {
		grails.serverURL = "http://localhost:8080/${appName}"
		grails.dbconsole.enabled = true
		grails.dbconsole.urlRoot = '/admin/dbconsole'
	}
	test {
		grails.serverURL = "http://localhost:8080/${appName}"
	}
}*/
