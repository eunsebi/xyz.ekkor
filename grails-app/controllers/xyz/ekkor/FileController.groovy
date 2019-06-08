package xyz.ekkor

import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3ObjectSummary
import grails.converters.JSON
import org.grails.web.json.JSONObject
import org.springframework.web.multipart.MultipartFile

class FileController {

    def amazonS3Service
    def amazonPath = "https://s3.ap-northeast-2.amazonaws.com/ekkor/img/"

    def index() {
        ObjectListing listObjects = amazonS3Service.listObjects('ekkor', 'img/')
        List<S3ObjectSummary> objectSummary = listObjects.getObjectSummaries()

        [objects: objectSummary]
    }

    def image() {
        MultipartFile imageFile = request.getFile("files")
        println "file image Upload"

        if(!imageFile.empty) {
            def ext = imageFile.originalFilename.substring(imageFile.originalFilename.lastIndexOf('.'))
            def mil = System.currentTimeMillis()
            imageFile.transferTo(new File("${grailsApplication.config.grails.filePath}/images/", "${mil}${ext}"))

            render "<script>parent.\$.imageUploaded('${grailsApplication.config.grails.fileURL}/images/${mil}${ext}', '${mil}${ext}');</script>"
            println "render : " + "<script>parent.\$.imageUploaded('${grailsApplication.config.grails.fileURL}/images/${mil}${ext}', '${mil}${ext}');</script>"
        }

    }

    def uploadFile() {
        println "File Upload"
        MultipartFile multipartFile = request.getFile('file')
        if(multipartFile && !multipartFile.empty) {
            amazonS3Service.storeMultipartFile('ekkor', 'img/'+multipartFile.originalFilename, multipartFile)

            flash.message = "File "+multipartFile.originalFilename+" uploaded successfully"
            redirect controller: "file"
        } else {
            flash.message = "Upload failed"
            redirect controller: "file"
        }
    }

    def uploadImg() {
        println "File Upload"
        println "file Name : " + params

        MultipartFile multipartFile = request.getFile('file')
        if(multipartFile && !multipartFile.empty) {
            amazonS3Service.storeMultipartFile('ekkor', 'img/'+multipartFile.originalFilename, multipartFile)
            def file = amazonPath+multipartFile.originalFilename

            flash.message = "File "+multipartFile.originalFilename+" uploaded successfully"
            //redirect controller: "uploader"
            JSONObject jsonObject = new JSONObject()
            jsonObject.put("url", file)
            response.setContentType("application/json")
            render jsonObject as JSON

        } else {
            flash.message = "Upload failed"
            redirect controller: "file"
        }

    }

    def deleteFile() {
        def found = amazonS3Service.exists('ekkor', params.filekey)
        if(found) {
            amazonS3Service.deleteFile('ekkor', params.filekey)
            flash.message = "File "+params.filekey+" deleted"
            redirect controller: "file"
        }
    }
}
