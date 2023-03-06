package ua.net.nlp.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

import groovy.transform.CompileStatic
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import ua.net.nlp.api.BaseController.RequestBase
import ua.net.nlp.api.services.BatchService


@Tag(name = "Batch text processing services",
    description = "Batch text processing services for Ukrainian language"
)
@RestController
@RequestMapping('')
@CompileStatic
class BatchController extends BaseController {

    @Autowired
    BatchService batchService

    def validateRequest(BatchRequest request) {
        // To Andriy. I think we should allow empty strings, but we clearly should disallow long ones.
        // How I can assert that all the strings in the list are shorter than TEXT_LIMIT?
        if( ! request.texts ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Texts are empty.")
        }

        if( request.texts.size() > 100 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many texts.")
        }
    }

    @Operation(summary = "Clean, tokenize, and lemmatize the texts"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    ])
    @PostMapping(path="/batch")
    def batch(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description='Body text; e.g<br>{"texts": ["Сьогодні y продажi «ХХІ століття»."]}', 
            required = true)
        @RequestBody BatchRequest request) {
        
        validateRequest(request)

        try {
            // To Andriy. Can I use parallel here? groovy gave me the error when compiling.
            // Maybe it is related to the old version of it?
            // Groovy Version: 4.0.5 JVM: 17.0.4.1 Vendor: Homebrew OS: Mac OS X
            // It's not a mandatory option, so if it's hard to implement, I'd rather skip this one
            // return request.texts.parallel().collect { text ->
            return request.texts.collect { text ->
                batchService.batch(text)
            }
        }
        catch(Exception e) {
            e.printStackTrace()
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)
        }

    }

    @Schema(description="Request to clean, tokenize, and lemmatize text.")
    @CompileStatic
    static class BatchRequest {
        @Schema(description="Texts to process.")
        List<String> texts
    }


    @Schema(description="Response with tokenized and lemmatized text.")
    @CompileStatic
    static class BatchResponse {
        String clean
        List<List<String>> tokenized
        List<List<String>> lemmatized
        List<String> segmented
    }
}
