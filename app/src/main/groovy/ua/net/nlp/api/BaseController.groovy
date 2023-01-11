package ua.net.nlp.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

import groovy.transform.CompileStatic
import io.swagger.v3.oas.annotations.media.Schema
import ua.net.nlp.api.TokenizeController.TokenizeRequest
import ua.net.nlp.api.TokenizeController.TokenizeResponse


@CompileStatic
class BaseController {

    @Value('${ua.net.nlp.api.maxTextLength:1048576}')
    int TEXT_LIMIT = 1048576

    def validateRequest(RequestBase request) {
        if( ! request.text ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text is empty.")
        }

        if( request.text.size() > TEXT_LIMIT ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text size is too big.")
        }
    }

    def updateNotes(RequestBase request, ResponseBase response) {
        if( testLatCyrMix(request) ) {
            response.notes = "Text contains mix of Cyrillic and Latin characters which may produce suboptimal results."
        }
    }

    def testLatCyrMix(text) {
        return text =~ /[а-яіїєґА-ЯІЇЄҐ]['’ʼ-]?[a-zA-Z]|[a-zA-Z]['’ʼ-]?[а-яіїєґА-ЯІЇЄҐ]/
    }

    @Schema(description="Request base.")
    @CompileStatic
    static class RequestBase {
        @Schema(description="Text to process.")
        String text
    }

    @Schema(description="Response base.")
    @CompileStatic
    static class ResponseBase {
        @Schema(description="Notes about lemmatization.")
        String notes
    }

}
