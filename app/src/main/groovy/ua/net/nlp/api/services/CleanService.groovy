package ua.net.nlp.api.services

import org.springframework.stereotype.Component

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import ua.net.nlp.api.CleanController.CleanResponse
import ua.net.nlp.other.CleanText
import ua.net.nlp.other.CleanText.CleanOptions


@Component
@CompileStatic
class CleanService {
    
    CleanResponse clean(String text, CleanOptions options) {
        def sb = new StringBuilder(512)
        CleanText cleanText = new CleanText(new CleanOptions()) {
            void _println(String txt) {
                sb.append(txt).append("\n")
            }
        }
        text = cleanText.cleanUp(text, new File("/dev/null"), options, new File("/dev/null"))
        
        return new CleanResponse(text: text, notes: sb.toString())
    }
    
    
}
