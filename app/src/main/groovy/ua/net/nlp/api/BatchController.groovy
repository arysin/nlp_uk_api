package ua.net.nlp.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Value
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

    @Value('${ua.net.nlp.api.maxBatchSize:1000}')
    int BATCH_SIZE_LIMIT = 1000

    @Autowired
    BatchService batchService

    def validateRequest(BatchRequest request) {
        if( ! request.texts ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Texts are empty.")
        }

        if( request.texts.size() > BATCH_SIZE_LIMIT ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many texts.")
        }

        request.texts.each { 
            if( ! it ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text is empty.")
            }

            if( it.size() > TEXT_LIMIT ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text size is too big.")
            }
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
            return request.texts.parallelStream().collect { String text ->
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
        String cleanText
        List<List<String>> tokens
        List<List<String>> lemmas
        List<String> sentences
    }
}
