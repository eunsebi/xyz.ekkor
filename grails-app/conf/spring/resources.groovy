
import xyz.ekkor.UserPasswordEncoderListener
import xyz.ekkor.CustomUserDetailService

// Place your Spring DSL code here
beans = {
    userDetailsService(CustomUserDetailService)
    userPasswordEncoderListener(UserPasswordEncoderListener, ref('hibernateDatastore'))
    multipartResolver(org.springframework.web.multipart.commons.CommonsMultipartResolver){
        maxInMemorySize=1000000
        maxUploadSize=100000000
        //uploadTempDir="/tmp"
    }
}
