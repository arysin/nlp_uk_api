package ua.net.nlp.api.services

import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.tools.tag.TagOptions
import ua.net.nlp.tools.tag.TagTextCore
import ua.net.nlp.tools.tag.TagTextCore.TTR


@Component
@CompileStatic
class LemmatizeService {
    TagTextCore tagger = new TagTextCore()
    
    LemmatizeService() {
        tagger.setOptions(new TagOptions(disambiguate: true, singleTokenOnly: true, setLemmaForUnknown: true, tagUnknown: true, quiet: true))
    }
    
    List<List<TTR>> tag(String text) {
        tagger.tagTextCore(text, null)
    }
    
    List<List<String>> lemmatize(String text) {
        List<List<TTR>> tokens = tag(text)

        lemmatizeTokens(tokens)
    }
    
    List<List<String>> lemmatizeTokens(List<List<TTR>> tokens) {
        tokens.findAll { List<TTR> tkns ->
            tkns.size() > 0
        }
        .collect { List<TTR> it ->
            it.findAll { TTR ttr ->
                ttr.tokens[0].tags != 'punct'
            }
            .collect { TTR ttr ->
                ttr.tokens[0].lemma
            }
        }
    }
    
}
