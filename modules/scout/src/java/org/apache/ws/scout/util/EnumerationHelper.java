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

import org.apache.ws.scout.registry.infomodel.ClassificationSchemeImpl;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.ConceptImpl;

import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.JAXRException;
import java.util.StringTokenizer;

/**
 * Helper class that deals with predefined enumerations
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class EnumerationHelper
{
    public static Concept getConceptByPath( String path)
    throws IllegalArgumentException, JAXRException
    {
        //Lets tokenize the path
        StringTokenizer tokenizer = new StringTokenizer(path,"/");
        //Deal with the first token
        String firstToken = "";
        if(tokenizer.hasMoreTokens())
        {
           firstToken = tokenizer.nextToken();
           if(!checkFirstToken( firstToken))
               throw new IllegalArgumentException("Wrong Path");;
        }

        String secondToken = tokenizer.nextToken();
        //TODO:Check whether the second token is also valid
        return createConcept(firstToken, secondToken) ;

    }


    private static boolean checkFirstToken(String token)
    throws IllegalArgumentException
    {
        if(token == null )
            throw new IllegalArgumentException();
        if(token.equalsIgnoreCase("AssociationType"))  return true;
        if(token.equalsIgnoreCase("URLType"))  return true;
        if(token.equalsIgnoreCase("PhoneType"))  return true;
        if(token.equalsIgnoreCase("PostalAddressAttributes"))  return true;
        if(token.equalsIgnoreCase("ObjectType"))  return true;
        return false;
    }

    /**
     *
     * @param firstToken
     * @param secondToken
     * @return
     * @throws JAXRException
     */
    private static  Concept createConcept(String firstToken, String secondToken)
            throws JAXRException
    {
        /**
         * This is a hack!!!  Need to figure out how to do this!
         */
        ClassificationScheme cs = new ClassificationSchemeImpl(null);
        cs.setName(new InternationalStringImpl(firstToken));

        Concept concept = new ConceptImpl(null);
        concept.setName(new InternationalStringImpl(secondToken.toLowerCase()));
        concept.setValue(secondToken);
        ((ConceptImpl)concept).setScheme(((ClassificationSchemeImpl)cs));
        return concept;

    }
}
