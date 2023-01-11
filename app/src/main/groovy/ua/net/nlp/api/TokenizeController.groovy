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
import ua.net.nlp.api.BaseController.ResponseBase
import ua.net.nlp.api.services.TokenizeService


@Tag(name = "Tokenization services",
    description = "Tokenization services for Ukrainian language"
)
@RestController
@RequestMapping('')
@CompileStatic
class TokenizeController extends BaseController {

    @Autowired
    TokenizeService tokenizeService

    @Operation(summary = "Tokenize the text"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    ])
    @PostMapping(path="/tokenize")
    def tokenize(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description='Body text; e.g<br>{"text": "Сьогодні у продажі. 12-те зібрання творів 1969 р. І. П. Котляревського."}', 
            required = true)
        @RequestBody TokenizeRequest request) {
        
        validateRequest(request)

        try {
            def tokens = tokenizeService.tokenize(request.text, request.wordsOnly)

            def response = new TokenizeResponse(tokens: tokens)

            updateNotes(request, response)

            return response
        }
        catch(Exception e) {
            e.printStackTrace()
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)
        }

    }

    @Schema(description="Request to tokenize text.")
    @CompileStatic
    static class TokenizeResponse extends ResponseBase {
        @Schema(description="List of tokenized tokens inside the list of sentences.")
        List<List<String>> tokens
    }

    @Schema(description="Request to tokenize text.")
    @CompileStatic
    static class TokenizeRequest extends RequestBase {
        @Schema(description="If true only words will be returned, otherwise other tokens will be returned as well.", defaultValue = "true")
        boolean wordsOnly = true
    }
}
