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
        CleanText cleanText = new CleanText(options) {
            void _println(String txt) {
                sb.append(txt).append("\n")
            }
        }

        int nlIdx = text.indexOf("\n")
        int dosNlIdx = text.indexOf("\r\n")
        boolean dosNlPresent = dosNlIdx >= 0 && dosNlIdx+1 == nlIdx

        text = cleanText.cleanText(text, new File("/dev/null"), new File("/dev/null"), dosNlPresent)
        
        return new CleanResponse(text: text, notes: sb.toString())
    }
    
}
