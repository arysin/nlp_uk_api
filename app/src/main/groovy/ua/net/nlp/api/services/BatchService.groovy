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
    LemmatizeService lemmatizeService
    @Autowired
    TokenizeService tokenizeService

//    TagTextCore tagger = new TagTextCore()
    
    BatchService() {
//        tagger.setOptions(new TagOptions(disambiguate: true, singleTokenOnly: true, setLemmaForUnknown: true, tagUnknown: true, quiet: true))
    }
    
    BatchResponse batch(String text) {
        def response = cleanService.clean(text, new CleanOptions())
        
        def sentences = lemmatizeService.tagger.langTool.analyzeText(response.text)

        def tokenized = sentences.collect { s ->
            def tokens = s.getTokens()
            def tokenized = tokens[1..-1].collect { at ->
                at.getToken()
            }
        }
        tokenizeService.trim(tokenized)
        
        def tagged = lemmatizeService.tagger.tagTextCore(sentences, null)
        def lemmatized = lemmatizeService.lemmatizeTokens(tagged)
        
        return new BatchResponse(tokenized: tokenized, lemmatized: lemmatized)
    }
    
}
