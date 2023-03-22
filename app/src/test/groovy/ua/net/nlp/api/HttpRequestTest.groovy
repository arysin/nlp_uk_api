package ua.net.nlp.api


import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assumptions.assumeTrue

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

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
    @Autowired
    private BatchController batchController

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
        def request = new BatchRequest(texts: ["Сьогодні y про\u00ACдажi. Екс-«депутат» у XХІ столітті.", "Привіт, як справи?"])
        List<BatchResponse> resp = restTemplate.postForObject(url, request, BatchResponse[].class)

        assertEquals ("Сьогодні у продажі. Екс-«депутат» у XXI столітті.", resp[0].cleanText)
        assertEquals (["Сьогодні у продажі.", "Екс-«депутат» у XXI столітті."], resp[0].sentences)
        assertEquals ([["Сьогодні", " ", "у", " ", "продажі", "."], ["Екс-«депутат»", " ", "у", " ", "XXI", " ", "столітті", "."]], resp[0].tokens)
        assertEquals ([["сьогодні", "у", "продаж"], ["екс-депутат", "у", "XXI", "століття"]], resp[0].lemmas)

        assertEquals ("Привіт, як справи?", resp[1].cleanText)
        assertEquals (["Привіт, як справи?"], resp[1].sentences)
        assertEquals ([["Привіт", ",", " ", "як", " ", "справи", "?"]], resp[1].tokens)
        assertEquals ([["привіт", "як", "справа"]], resp[1].lemmas)
        assertEquals(resp.size(), 2)
    }

    @Test
    public void testBatchErrorResponse() throws Exception {
        def url = "http://localhost:" + port + "/batch"
        
        def request = new BatchRequest(texts: [])
        ResponseEntity<String> resp = restTemplate.postForEntity(url, request, String.class)
        assertEquals HttpStatus.BAD_REQUEST, resp.getStatusCode()
        assertTrue resp.getBody().contains('message":"Texts are empty."')
        
        List<String> tooMany = []
        int cnt = batchController.BATCH_SIZE_LIMIT+1
        (1..cnt).forEach { tooMany << "x" }
        request = new BatchRequest(texts: tooMany)
        resp = restTemplate.postForEntity(url, request, String.class)
        assertEquals HttpStatus.BAD_REQUEST, resp.getStatusCode()
        assertTrue resp.getBody().contains('message":"Too many texts."')
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
        def request = new BatchRequest(texts: [text])

        BatchResponse[] resps = restTemplate.postForObject(url, request, BatchResponse[].class)
        assertEquals 1, resps.size()
        BatchResponse resp = resps[0]
        
        assertEquals 67182, resp.sentences.size()
        assertNotNull resp.tokens
        assertEquals 67182, resp.tokens.size()
        assertEquals 9, resp.tokens[0].size()
//        assertEquals(['Машини', ' ', '-', ' ', 'не', ' ', 'розкіш', '...'. "\n"], resp.tokenized[0])

        assertNotNull resp.lemmas
        assertEquals 67182, resp.lemmas.size()
        assertEquals(['машина', 'не', 'розкіш'], resp.lemmas[0])
    }

}
