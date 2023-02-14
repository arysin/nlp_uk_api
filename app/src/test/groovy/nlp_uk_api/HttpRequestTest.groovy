package nlp_uk_api


import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assumptions.assumeTrue

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus

import ua.net.nlp.api.App
import ua.net.nlp.api.BatchController.BatchRequest
import ua.net.nlp.api.BatchController.BatchResponse
import ua.net.nlp.api.CleanController.CleanRequest
import ua.net.nlp.api.CleanController.CleanResponse
import ua.net.nlp.api.LemmatizeController.LemmatizeRequest
import ua.net.nlp.api.LemmatizeController.LemmatizeResponse
import ua.net.nlp.api.TokenizeController.TokenizeRequest
import ua.net.nlp.api.TokenizeController.TokenizeResponse

@SpringBootTest(classes=App.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @Value(value='${local.server.port}')
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testTokenize() throws Exception {
        def url = "http://localhost:" + port + "/tokenize"
        def request = new TokenizeRequest(text: "-20 градусів. Там-то.")
        def resp = restTemplate.postForObject(url, request, TokenizeResponse.class)
        // -20 will be split in LT 6.1 / nlp_uk 3.2
        assertEquals ([["-20", " ", "градусів", "."], ["Там", "-то", "."]], resp.tokens)
    }

    @Test
    public void testLemmatize() throws Exception {
        def url = "http://localhost:" + port + "/lemmatize"
        def request = new LemmatizeRequest(text: "своїх, слів")
        def resp = restTemplate.postForObject(url, request, LemmatizeResponse.class)
        assertEquals ([["свій", "слово"]], resp.tokens)
    }

    @Test
    public void testClean() throws Exception {
        def url = "http://localhost:" + port + "/clean"
        def request = new CleanRequest(text: "Сьогодні y про\u00ACдажi «XХІ століття».")
        def resp = restTemplate.postForObject(url, request, CleanResponse.class)
        assertEquals ("Сьогодні у продажі «XXI століття».", resp.text)
        
        def expectedNotes = 
"""\tremoving U+00AC hyphens: 
\tlatin/cyrillic mix
\tconverted 1 lat->cyr, 2 cyr->lat
"""
        assertEquals expectedNotes, resp.notes
    }
    
    
    @Test
    public void testBatch() throws Exception {
        def url = "http://localhost:" + port + "/batch"
        // лат. літери в укр словах + кирилиця в XXI 
        def request = new BatchRequest(text: "Сьогодні y про\u00ACдажi. Екс-«депутат» у XХІ столітті.")
        BatchResponse resp = restTemplate.postForObject(url, request, BatchResponse.class)
        assertEquals ([["Сьогодні", " ", "у", " ", "продажі", "."], ["Екс-«депутат»", " ", "у", " ", "XXI", " ", "столітті", "."]], resp.tokenized)
        assertEquals ([["сьогодні", "у", "продаж"], ["екс-депутат", "у", "XXI", "століття"]], resp.lemmatized)
    }
    
    @Test
    public void testBatchHugePayload() throws Exception {
        def bigFile = getClass().getClassLoader().getResource("big_file.txt")
        assumeTrue bigFile != null
        
        def url = "http://localhost:" + port + "/batch"
        def w = "десь що \n"
//        def cnt = (int)(15*1024*1024 / w.length())
//        def text = w.repeat(cnt)
        def text = bigFile.getText('UTF-8')
        def request = new BatchRequest(text: text)

        BatchResponse resp = restTemplate.postForObject(url, request, BatchResponse.class)
        
        assertNotNull resp.tokenized
        assertEquals 67182, resp.tokenized.size()
        assertEquals 9, resp.tokenized[0].size()
//        assertEquals(['Машини', ' ', '-', ' ', 'не', ' ', 'розкіш', '...'. "\n"], resp.tokenized[0])

        assertNotNull resp.lemmatized
        assertEquals 67182, resp.lemmatized.size()
        assertEquals(['машина', 'не', 'розкіш'], resp.lemmatized[0])
    }

}
