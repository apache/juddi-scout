/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ws.scout.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;

/**
 * Helper class that deals with predefined enumerations
 *
 * @author Anil Saldhana  <anil@apache.org>
 * @author Kurt Stam <kstam@apache.org>
 * 
 */
public class EnumerationHelper
{
    private static Log log = LogFactory.getLog(EnumerationHelper.class);
    
    private final static String OBJECT_TYPE                   = "ObjectType";
    private final static String ASSOCIATION_TYPE              = "AssociationType";
    private final static String URL_TYPE                      = "URLType";
    private final static String PHONE_TYPE                    = "PhoneType";
    private final static String POSTAL_ADDRESS_ATTRIBUTES_STR = "PostalAddressAttributes";
    private final static String[] TYPES = {OBJECT_TYPE, ASSOCIATION_TYPE, URL_TYPE, PHONE_TYPE, POSTAL_ADDRESS_ATTRIBUTES_STR};
    
    private final static String[] OBJECT_TYPES = {
         "ExternalLink","Package","ExternalId","Association","Classification","Concept",
         "AuditableEvent","User","Organization","CPA","CPP","Service","ServiceBinding","Process","WSDL",
         "ExtrinsicObj","Organization","User"};
    private final static String[] ASSOCIATION_TYPES = {
         "RelatedTo","ExternallyLinks","Contains","Extends","Implements",
         "InstanceOf","Supersedes","Uses","HasMember","EquivalentTo","HasChild","HasParent","Replaces",
         "ResponsibleFor","SubmitterOf"};
    private final static String[] URL_TYPES = {
         "HTTP","HTTPS","SMTP","FAX","PHONE","OTHER"};
    private final static String[] PHONE_TYPES = {
         "Office","Home","Mobile","Beeper","FAX"};
    private final static String[] POSTAL_ADDRESS_ATTRIBUTES = {
         "StreetNumber","Street","City","State","PostalCode","Country"};
    
    private final static ArrayList<String> TYPES_LIST                     = new ArrayList<String>(Arrays.asList(TYPES));
    private final static ArrayList<String> OBJECT_TYPES_LIST              = new ArrayList<String>(Arrays.asList(OBJECT_TYPES));
    private final static ArrayList<String> ASSOCIATION_TYPES_LIST         = new ArrayList<String>(Arrays.asList(ASSOCIATION_TYPES));
    private final static ArrayList<String> URL_TYPES_LIST                 = new ArrayList<String>(Arrays.asList(URL_TYPES));
    private final static ArrayList<String> PHONE_TYPES_LIST               = new ArrayList<String>(Arrays.asList(PHONE_TYPES));
    private final static ArrayList<String> POSTAL_ADDRESS_ATTRIBUTES_LIST = new ArrayList<String>(Arrays.asList(POSTAL_ADDRESS_ATTRIBUTES));

    private static Map<String,ArrayList<String>> typesMap = new HashMap<String,ArrayList<String>>();
    static {
        typesMap.put(OBJECT_TYPE                  ,OBJECT_TYPES_LIST);
        typesMap.put(ASSOCIATION_TYPE             ,ASSOCIATION_TYPES_LIST);
        typesMap.put(URL_TYPE                     , URL_TYPES_LIST);
        typesMap.put(PHONE_TYPE                   , PHONE_TYPES_LIST);
        typesMap.put(POSTAL_ADDRESS_ATTRIBUTES_STR, POSTAL_ADDRESS_ATTRIBUTES_LIST);
    }
    
    public static Concept getConceptByPath( String path)
    throws IllegalArgumentException, JAXRException
    {
        //Lets tokenize the path
        StringTokenizer tokenizer = new StringTokenizer(path,"/");
        String firstToken = null;
        String secondToken = null;
        
        if(tokenizer.hasMoreTokens())
        {
           firstToken = tokenizer.nextToken();
           if (tokenizer.hasMoreTokens()) {
               secondToken = tokenizer.nextToken();
               if (tokenizer.hasMoreTokens()) {
                   log.warn("Looking for 2 tokens. " + tokenizer.nextToken() + " will be ignored");
               }
           } else {
               throw new IllegalArgumentException("Expected two token separated with a forward slash (/)");
           }
        } else {
            throw new IllegalArgumentException("Expected two token separated with a forward slash (/)");
        }
        return createConcept(firstToken, secondToken) ;
    }

    /**
     * 
     * @param firstToken
     * @param secondToken
     * @return Concept
     * @throws JAXRException
     */
    private static  Concept createConcept(String firstToken, String secondToken)
            throws JAXRException, IllegalArgumentException
    {
        if (!TYPES_LIST.contains(firstToken)) throw new IllegalArgumentException("Exspected the path to " +
                "start with one of " + TYPES);
        
        //get the predefined classificationscheme
        ClassificationScheme cs = new ClassificationSchemeImpl(null);
        cs.setName(new InternationalStringImpl(firstToken));
        cs.setKey(new KeyImpl(firstToken));

        ArrayList<String> conceptStrings = typesMap.get(firstToken);
        if (!conceptStrings.contains(secondToken)) throw new IllegalArgumentException("Exspected the path to " +
                "end with one of " + conceptStrings.toArray());
                
        Concept concept = new ConceptImpl(null);
        concept.setName(new InternationalStringImpl(secondToken.toLowerCase()));
        concept.setValue(secondToken);
        concept.setKey(new KeyImpl(firstToken + "/" + secondToken));
        ((ConceptImpl)concept).setScheme(((ClassificationSchemeImpl)cs));
        return concept;
    }
}
