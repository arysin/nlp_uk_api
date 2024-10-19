package ua.net.nlp.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.api.BatchController.BatchResponse
import ua.net.nlp.other.clean.CleanOptions


@Component
@CompileStatic
class BatchService {
    @Autowired
    CleanService cleanService
    @Autowired
    TagService tagService
    @Autowired
    LemmatizeService lemmatizeService
    @Autowired
    TokenizeService tokenizeService


    BatchResponse batch(String text) {
        def response = cleanService.clean(text, new CleanOptions())
        
        def sentences = tagService.tagger.langTool.analyzeText(response.text)

        def tokenized = sentences.collect { s ->
            def tokens = s.getTokens()
            def tokenized = tokens[1..-1].collect { at ->
                at.getToken()
            }
        }
        def segments = sentences.collect { s -> 
            s.getText().trim()
        }

        tokenizeService.trim(tokenized)
        
        def tagged = tagService.tagger.tagTextCore(sentences, null)
        def lemmatized = lemmatizeService.lemmatizeTokens(tagged)
        
        return new BatchResponse(tokens: tokenized, lemmas: lemmatized, cleanText: response.text, sentences: segments)
    }
    
}
