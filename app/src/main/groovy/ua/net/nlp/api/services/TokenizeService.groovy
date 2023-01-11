package ua.net.nlp.api.services

import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.tools.tokenize.TokenizeTextCore
import ua.net.nlp.tools.tokenize.TokenizeTextCore.TokenizeOptions


@Component
@CompileStatic
class TokenizeService {
    TokenizeTextCore tokenizer = new TokenizeTextCore(new TokenizeOptions())
    
    List<List<String>> tokenize(String text, boolean wordsOnly) {
        List<List<String>> ret = tokenizer.splitWordsInternal(text, wordsOnly)
        
        return ret
    }
}
