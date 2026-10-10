package me.aap.fermata.ui.voice;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Test;

public class TurkishVoiceGrammarTest {
    @Test public void grammarCompilesAndPreservesSeekAndSubtitleActions() throws Exception {
        Path root = Path.of(System.getProperty("user.dir"));
        if (!Files.isDirectory(root.resolve("fermata"))) root = root.getParent();
        var nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                root.resolve("fermata/src/main/res/values-tr/voice_cmd.xml").toFile())
                .getElementsByTagName("string");
        Map<String, Pattern> patterns = new HashMap<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            var node = nodes.item(i);
            String name = node.getAttributes().getNamedItem("name").getNodeValue();
            if (name.equals("vcmd_nums")) continue;
            patterns.put(name, Pattern.compile(node.getTextContent().replace("\\\\", "\\")));
        }
        var forward = patterns.get("vcmd_ff").matcher("ileri sar 10 saniye");
        assertTrue(forward.matches());
        assertEquals("10", forward.group("N"));
        assertEquals("saniye", forward.group("U"));
        var backward = patterns.get("vcmd_rw").matcher("geri sar iki dakika");
        assertTrue(backward.matches());
        assertEquals("iki", backward.group("N"));
        assertEquals("dakika", backward.group("U"));
        assertTrue(patterns.get("vcmd_action_sub_on").matcher("altyazıları aç").matches());
        assertTrue(patterns.get("vcmd_action_sub_off").matcher("altyazıları kapat").matches());
        assertTrue(patterns.get("vcmd_action_audio_change").matcher("ses kanalını değiştir").matches());
    }
}
