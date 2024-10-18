package ua.net.nlp.api.services

import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.tools.tag.TagOptions
import ua.net.nlp.tools.tag.TagTextCore
import ua.net.nlp.tools.tag.TagTextCore.TTR
import ua.net.nlp.tools.tag.TagTextCore.TaggedSentence


@Component
@CompileStatic
class LemmatizeService {
    TagTextCore tagger = new TagTextCore()
    
    LemmatizeService() {
        tagger.setOptions(new TagOptions(disambiguate: true, singleTokenOnly: true, setLemmaForUnknown: true, tagUnknown: true, quiet: true))
    }
    
    List<TaggedSentence> tag(String text) {
        tagger.tagTextCore(text, null)
    }
    
    List<List<String>> lemmatize(String text) {
        List<TaggedSentence> tokens = tag(text)

        lemmatizeTokens(tokens)
    }
    
    List<List<String>> lemmatizeTokens(List<TaggedSentence> tokens) {
        tokens.findAll { TaggedSentence sent ->
            sent.tokens
        }
        .collect { TaggedSentence it ->
            it.tokens.findAll { TTR ttr ->
                ttr.tokens[0].tags != 'punct'
            }
            .collect { TTR ttr ->
                ttr.tokens[0].lemma
            }
        }
    }
    
}
