package ua.net.nlp.api.services

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.tools.tokenize.TokenizeTextCore
import ua.net.nlp.tools.tokenize.TokenizeTextCore.TokenizeOptions


@Component
@CompileStatic
class TokenizeService {
    TokenizeTextCore tokenizer = new TokenizeTextCore(new TokenizeOptions())
    
    List<List<String>> tokenize(String text, boolean wordsOnly, boolean preserveWhitespace) {
        List<List<String>> ret = tokenizer.splitWordsInternal(text, wordsOnly, preserveWhitespace)
        trim(ret)
        return ret
    }
    
    def trim(List<List<String>> ret) {
        ret.forEach{
            if( it.size() > 0 ) {
                if( StringUtils.isBlank(it[-1]) ) {
                    it.remove(it.size()-1)
                }
            }
        }
    }
}
