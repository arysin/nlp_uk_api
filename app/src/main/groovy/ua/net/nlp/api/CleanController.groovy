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
import ua.net.nlp.api.services.CleanService
import ua.net.nlp.other.clean.CleanOptions


@Tag(name = "Text cleanup services",
    description = "Text cleanup services for Ukrainian language"
)
@RestController
@RequestMapping('')
@CompileStatic
class CleanController extends BaseController {

    @Autowired
    CleanService cleanService

    @Operation(summary = "Clean the text"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    ])
    @PostMapping(path="/clean")
    def clean(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description='Body text; e.g<br>{"text": "Сьогодні y продажi «ХХІ століття»."}', 
            required = true)
        @RequestBody CleanRequest request) {
        
        validateRequest(request)

        try {
            def response = cleanService.clean(request.text, new CleanOptions())

            return response
        }
        catch(Exception e) {
            e.printStackTrace()
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)
        }

    }

    @Schema(description="Request to tokenize text.")
    @CompileStatic
    static class CleanResponse extends ResponseBase {
        @Schema(description="Text that was cleaned up.")
        String text
    }

    @Schema(description="Request to clean text.")
    @CompileStatic
    static class CleanRequest extends RequestBase {
//        @Schema(description="If true only words will be returned, otherwise other tokens will be returned as well.", defaultValue = "true")
//        CleanOptions cleanOptions
    }
}
