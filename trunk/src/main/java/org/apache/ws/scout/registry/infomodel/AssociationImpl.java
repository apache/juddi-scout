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
package org.apache.ws.scout.registry.infomodel;

import javax.xml.registry.JAXRException;
import javax.xml.registry.LifeCycleManager;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.RegistryObject;

/**
 * Implements JAXR Association Interface.
 * For futher details, look into the JAXR API Javadoc.
 *
 * @author Anil Saldhana  <anil@apache.org>
 */
public class AssociationImpl extends RegistryObjectImpl implements Association
{
    private Concept type = null;
    private RegistryObject source = null;
    private RegistryObject target = null;
    private boolean isConfirmed = true;
    private boolean isConfirmedBySourceOwner = true;
    private boolean isConfirmedByTargetOwner = true;
    private boolean isExtramural = true;

    public AssociationImpl(LifeCycleManager lifeCycleManager)
    {
        super(lifeCycleManager);
    }

    public AssociationImpl(LifeCycleManager lifeCycleManager, InternationalString n)
    {
        super(lifeCycleManager, n);
    }

    public Concept getAssociationType() throws JAXRException
    {
        return type;
    }

    public RegistryObject getSourceObject() throws JAXRException
    {
        return source;
    }

    public RegistryObject getTargetObject() throws JAXRException
    {
        return target;
    }

    public boolean isConfirmed() throws JAXRException
    {
        return isConfirmed;
    }

    public boolean isConfirmedBySourceOwner() throws JAXRException
    {
        return isConfirmedBySourceOwner;
    }

    public boolean isConfirmedByTargetOwner() throws JAXRException
    {
        return isConfirmedByTargetOwner;
    }

    public boolean isExtramural() throws JAXRException
    {
        return isExtramural;
    }

    public void setAssociationType(Concept concept) throws JAXRException
    {
        type = concept;
    }

    public void setSourceObject(RegistryObject ro) throws JAXRException
    {
        source = ro;
    }

    public void setTargetObject(RegistryObject ro) throws JAXRException
    {
        target = ro;
    }

   /**
    * There is an impedance mismatch as specified in the JAXR specification
    * Section D-11
    */
   public Key getKey()
   {
      String id = null;
      Key key = null;
      try
      {
         id = source.getKey().getId();
         id += "|" + target.getKey().getId();
         Key k = null;
         if(type != null ) k = type.getKey();
         if(k == null || k.getId() == "" ) id +="|NULL";
         else
          id+="|"+k.getId();
         id += "|" + "Concept";  //UDDI: KeyedReference->Key Name
         //String val = "NULL"; KS unused
         if(type!= null)  id += "|" + type.getValue();
         else  id +="|NULL";

      }
      catch (JAXRException e)
      {
        throw new RuntimeException(e);
      }

      if(id != null) key = new KeyImpl(id);
      return key;
   }

   public void setConfirmed(boolean b)
   {
      this.isConfirmed = b;
   }

   public void setConfirmedBySourceOwner(boolean b)
   {
      isConfirmedBySourceOwner = b;
   }

   public void setConfirmedByTargetOwner(boolean b)
   {
      isConfirmedByTargetOwner = b;
   }

   public void setExtramural(boolean b)
   {
      isExtramural = b;
   } 

}
