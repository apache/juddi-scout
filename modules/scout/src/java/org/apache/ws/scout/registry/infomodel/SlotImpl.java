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

 import javax.xml.registry.infomodel.Slot;
 import javax.xml.registry.JAXRException;
 import javax.xml.registry.LifeCycleManager;
 import java.util.Collection;

 /**
  * Implements Jaxr API
  *
  * @author <mailto:anil@apache.org>Anil Saldhana
  * @since Nov 20, 2004
  */
 public class SlotImpl implements Slot
 {
     private String slotType;
     private String name;
     private Collection values;
     private LifeCycleManager lcm;

     public SlotImpl(LifeCycleManager lifeCycleManager)
     {
         lcm = lifeCycleManager;
     }
     public String getName() throws JAXRException
     {
       return name;
     }

     public String getSlotType() throws JAXRException
     {
        return slotType;
     }

     public Collection getValues() throws JAXRException
     {
         return values;
     }

     public void setName(String s) throws JAXRException
     {
         name = s;
     }

     public void setSlotType(String s) throws JAXRException
     {
         slotType = s;
     }

     public void setValues(Collection collection) throws JAXRException
     {
         values = collection;
     }
 }


