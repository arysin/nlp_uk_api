package ua.net.nlp.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import ua.net.nlp.tools.tag.TagOptions
import ua.net.nlp.tools.tag.TagTextCore
import ua.net.nlp.tools.tag.TagTextCore.TTR
import ua.net.nlp.tools.tag.TagTextCore.TaggedSentence


@Component
@CompileStatic
class LemmatizeService {
    @Autowired
    TagService tagService

    
    List<List<String>> lemmatize(String text) {
        List<TaggedSentence> tokens = tagService.tag(text)

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
