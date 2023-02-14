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


    @Operation(summary = "Clean, tokenize, and lemmatize the text"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    ])
    @PostMapping(path="/batch")
    def batch(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description='Body text; e.g<br>{"text": "Сьогодні y продажi «ХХІ століття»."}', 
            required = true)
        @RequestBody BatchRequest request) {
        
        validateRequest(request)

        try {
            batchService.batch(request.text)
        }
        catch(Exception e) {
            e.printStackTrace()
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)
        }

    }

    @Schema(description="Request to clean, tokenize, and lemmatize text.")
    @CompileStatic
    static class BatchRequest extends RequestBase {
    }

    @Schema(description="Response with tokenized and lemmatized text.")
    @CompileStatic
    static class BatchResponse {
        List<List<String>> tokenized
        List<List<String>> lemmatized
    }
}
