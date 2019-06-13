
import xyz.ekkor.CustomUserDetailService
import xyz.ekkor.encoding.OldPasswordEncoder
import xyz.ekkor.listeners.CustomSecurityEventListener
import xyz.ekkor.encoding.UserPasswordEncoderListener

// Place your Spring DSL code here
beans = {
    userDetailsService(CustomUserDetailService)
    securityEventListener(CustomSecurityEventListener)
    //passwordEncoder(OldPasswordEncoder)
    userPasswordEncoderListener(UserPasswordEncoderListener, ref('hibernateDatastore'))
    multipartResolver(org.springframework.web.multipart.commons.CommonsMultipartResolver){
        maxInMemorySize=1000000
        maxUploadSize=100000000
        //uploadTempDir="/tmp"
    }
}
