/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ar.com.juliospa.edu.textmining.tp3;

import org.apache.commons.io.IOUtils;
import org.apache.tika.parser.ner.NERecogniser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** intento cabeza , de invocar esto sin pasar por el archivo.
 * al final lo termno haciendo custom
 * sin exito. 
 */
public class RegexNERecogniserCustomFix implements NERecogniser {

    public static final String NER_REGEX_FILE = "ner-regex.txt";
    private static Logger LOG = LoggerFactory.getLogger(RegexNERecogniserCustomFix.class);

    public Set<String> entityTypes = new HashSet<>();
    public Map<String, Pattern> patterns;
    private boolean available = false;

    private static RegexNERecogniserCustomFix INSTANCE;

    public RegexNERecogniserCustomFix(){
        try {
            patterns = new HashMap<>();
            List<String> listPatterns =  new ArrayList<>();
            
//            listPatterns.add("PHONE_NUMBER=((\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4})");
//            listPatterns.add("LI_TEMP=(li)");
            listPatterns.add("TMP1=(\\d{2}.\\d{2}.\\d{4})");
            

            
            readLinesFromListString(listPatterns);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        available = !entityTypes.isEmpty();
    }

    public RegexNERecogniserCustomFix(List<String> lines){
    		readLinesFromListString(lines);
    }
    
    public RegexNERecogniserCustomFix(InputStream stream){
        try {
            patterns = new HashMap<>();
            List<String> lines = IOUtils.readLines(stream, StandardCharsets.UTF_8);
            IOUtils.closeQuietly(stream);
            readLinesFromListString(lines);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        available = !entityTypes.isEmpty();
    }

	private void readLinesFromListString(List<String> lines) {
		for (String line : lines) {
		    line = line.trim();
		    if (line.isEmpty() || line.startsWith("#")){ //empty or comment
		        continue;                                //skip
		    }

		    int delim = line.indexOf('=');
		    if (delim < 0) { //delim not found
		        //skip
		        LOG.error("Skipped : Invalid config : {} ", line);
		        continue;
		    }
		    String type = line.substring(0, delim).trim();
		    String patternStr = line.substring(delim+1, line.length()).trim();
		    patterns.put(type, Pattern.compile(patternStr));
		    entityTypes.add(type);
		}
	}

    public synchronized static RegexNERecogniserCustomFix getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegexNERecogniserCustomFix();
        }
        return INSTANCE;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public Set<String> getEntityTypes() {
        return entityTypes;
    }

    /**
     * finds matching sub groups in text
     * @param text text containing interesting sub strings
     * @param pattern pattern to find sub strings
     * @return set of sub strings if any found, or null if none found
     */
    public Set<String> findMatches(String text, Pattern pattern){
        Set<String> results = null;
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            results = new HashSet<>();
            results.add(matcher.group(0));
            while (matcher.find()) {
                results.add(matcher.group(0));
            }
        }
        return results;
    }

    @Override
    public Map<String, Set<String>> recognise(String text) {
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            Set<String> names = findMatches(text, entry.getValue());
            if (names != null) {
                result.put(entry.getKey(), names);
            }
        }
        return result;
    }
}