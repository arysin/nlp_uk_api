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
import ua.net.nlp.api.services.LemmatizeService
import ua.net.nlp.api.services.TagService
import ua.net.nlp.tools.tag.TagTextCore.TaggedSentence


@Tag(name = "Tagging services",
    description = "Tagging services for Ukrainian language"
)
@RestController
@RequestMapping('')
@CompileStatic
class TagController extends BaseController {

    @Autowired
    TagService tagService

    @Operation(summary = "Tag the text"
    )
    @ApiResponses(value = [
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    ])
    @PostMapping(path="/tag")
    def tokenize(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description='Body text; e.g<br>{"text": "Сьогодні у продажі. 12-те зібрання творів 1969 р. І. П. Котляревського."}', 
            required = true)
        @RequestBody TagRequest request) {
        
        validateRequest(request)

        try {
            List<TaggedSentence> tokens = tagService.tag(request.text)

            TagResponse response = new TagResponse(tokens: tokens)

            updateNotes(request, response)

            return response
        }
        catch(Exception e) {
            e.printStackTrace()
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e)
        }

    }

    @Schema(description="Request to tag text.")
    @CompileStatic
    static class TagResponse extends ResponseBase {
        @Schema(description="List of lemmas inside the list of sentences.")
        List<TaggedSentence> tokens
    }

    @Schema(description="Request to tag text.")
    @CompileStatic
    static class TagRequest extends RequestBase {
    }
}
