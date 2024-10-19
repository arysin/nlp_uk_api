package ua.net.nlp.api.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import groovy.transform.CompileStatic
import jakarta.annotation.PostConstruct
import ua.net.nlp.tools.tag.TagOptions
import ua.net.nlp.tools.tag.TagTextCore
import ua.net.nlp.tools.tag.TagTextCore.TTR
import ua.net.nlp.tools.tag.TagTextCore.TaggedSentence


@Component
@CompileStatic
class TagService {
    TagTextCore tagger
    
    @PostConstruct 
    void init() {
        tagger = new TagTextCore()
        tagger.setOptions(new TagOptions(disambiguate: true, singleTokenOnly: true, setLemmaForUnknown: true, tagUnknown: true, quiet: true))
    }
    
    
    List<TaggedSentence> tag(String text) {
        tagger.tagTextCore(text, null)
    }
    
}
