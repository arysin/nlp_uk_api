package ua.net.nlp.api.services

import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.tools.tag.TagOptions
import ua.net.nlp.tools.tag.TagStats
import ua.net.nlp.tools.tag.TagTextCore
import ua.net.nlp.tools.tag.TagTextCore.TTR


@Component
@CompileStatic
class LemmatizeService {
    TagTextCore tagger = new TagTextCore()
    
    LemmatizeService() {
        tagger.setOptions(new TagOptions(disambiguate: true, singleTokenOnly: true, setLemmaForUnknown: true, tagUnknown: true, quiet: true))
    }
    
    List<List<String>> lemmatize(String body) {
        List<List<TTR>> ret = tagger.tagTextCore(body, new TagStats())
        
        return ret.findAll { List<TTR> tkns ->
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
