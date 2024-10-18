package ua.net.nlp.api.services

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.helpers.AbstractLogger
import org.slf4j.helpers.NOPLogger
import org.slf4j.helpers.NamedLoggerBase
import org.springframework.stereotype.Component

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import ua.net.nlp.api.CleanController.CleanResponse
import ua.net.nlp.other.clean.CleanTextCore
import ua.net.nlp.other.clean.OutputTrait
import ua.net.nlp.other.clean.CleanOptions
import ua.net.nlp.other.clean.CleanRequest


@Component
@CompileStatic
class CleanService {

    CleanResponse clean(String text, CleanOptions options) {
        CleanTextCore cleanText = new CleanTextCore(options)

        def request = new CleanRequest(text: text, file: null, outFile: null, dosNl: false)
        
        OutputTrait out = new OutputTrait(options: options)
        def res = cleanText.cleanText2(request, out)
        
        return new CleanResponse(text: res, notes: out.byteStream.toString(StandardCharsets.UTF_8))
    }
    
    private Logger getLogger(StringBuilder sb) {
        (Logger) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            [ Logger.class ] as Class[],
                 { proxy, Method method, Object[] methodArgs ->
                    if (method.getName().equals("info")) {
                        sb.append(methodArgs[0]).append("\n")
                    }
                    else if (method.getName().equals("debug")) {
//                        sb.append(methodArgs[0]).append("\n")
                    } else {
//                        throw new UnsupportedOperationException("Unsupported method: ${method.getName()}")
                    }
                })
    }
}
