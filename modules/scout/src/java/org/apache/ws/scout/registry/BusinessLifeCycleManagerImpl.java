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
package org.apache.ws.scout.registry;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.business.*;
import org.apache.juddi.datatype.service.*;
import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.PersonName;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.ws.scout.registry.infomodel.InternationalStringImpl;
import org.apache.ws.scout.registry.infomodel.KeyImpl;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import javax.xml.registry.infomodel.RegistryObject;
import java.util.*;
import java.net.PasswordAuthentication;

/**
 * Implements JAXR BusinessLifeCycleManager Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class BusinessLifeCycleManagerImpl extends LifeCycleManagerImpl
implements BusinessLifeCycleManager {

    public BusinessLifeCycleManagerImpl(RegistryService registry) {
        super(registry);
    }

    public BulkResponse deleteAssociations(Collection associationKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteClassificationSchemes(Collection schemeKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteConcepts(Collection conceptKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteOrganizations(Collection organizationKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteServiceBindings(Collection bindingKeys) throws JAXRException {
        return null;
    }

    public BulkResponse deleteServices(Collection serviceKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveAssociations(Collection associationKeys, boolean replace) throws JAXRException {
        return null;
    }

    public BulkResponse saveClassificationSchemes(Collection schemeKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveConcepts(Collection conceptKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveOrganizations(Collection organizationKeys) throws JAXRException {
        IRegistry ireg = null;
        if(registry != null ) ireg = registry.getRegistry();
        //Now we need to convert the collection into a vector for juddi
        BulkResponseImpl bulk = new BulkResponseImpl();
        Vector entityvect = new Vector();

        Collection coll = new ArrayList();
        Collection exceptions = new ArrayList();

         try{
             ConnectionImpl connection = registry.getConnection();
             Set creds = connection.getCredentials();
             Iterator it  = creds.iterator();
             String username="",pwd="";
             while(it.hasNext())
             {
                 PasswordAuthentication pass = (PasswordAuthentication)it.next();
                 username = pass.getUserName();
                 pwd = new String(pass.getPassword());
             }
             AuthToken token = null;
             try{
                 token = ireg.getAuthToken(username,pwd);
             }catch(Exception e)
             {
                 throw new JAXRException(e);
             }
             Iterator iter = organizationKeys.iterator();
             while( iter.hasNext()){
                 BusinessEntity en =
                   getBusinessEntityFromJAXROrg( (Organization)iter.next());
                 entityvect.add(en);
             }
             System.out.println( "Method:save_business: ENlength="+entityvect.size() );
             // Save business
             BusinessDetail bd = ireg.saveBusiness(token.getAuthInfo(),   entityvect);

             entityvect = bd.getBusinessEntityVector(); System.out.println( "After Saving Business. Obtained vector size:"+                 entityvect.size());
             for( int i = 0 ; entityvect != null && i < entityvect.size(); i++){                 BusinessEntity entity = (BusinessEntity)entityvect.elementAt(i);
                 coll.add( new KeyImpl(entity.getBusinessKey() ));
             }

             bulk.setCollection( coll );
             bulk.setExceptions( exceptions);
          }catch(  Exception tran){
                 throw new JAXRException( "Apache JAXR Impl:",tran);
             }
          return bulk;
    }

    public BulkResponse saveServiceBindings(Collection bindingKeys) throws JAXRException {
        return null;
    }

    public BulkResponse saveServices(Collection serviceKeys) throws JAXRException {
        return null;
    }

    public void confirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
    }

    public void unConfirmAssociation(Association assoc) throws JAXRException, InvalidRequestException {
    }

    //Private methods
    private  BusinessEntity getBusinessEntityFromJAXROrg( Organization org )
    throws JAXRException {
        BusinessEntity biz = new BusinessEntity();
        BusinessServices bss = new BusinessServices();
        Contacts cts = new Contacts();
        Vector bvect =  new Vector();
        Vector cvect = new Vector();

        try{
            //Lets get the Organization attributes at the top level
            String language = Locale.getDefault().getLanguage();

            biz.addName(new Name(org.getName().getValue() , language));
            biz.addDescription(new Description(org.getDescription().getValue()));

            Collection s = org.getServices();
            System.out.println( "?Org has services="+s.isEmpty());
            Iterator iter = s.iterator();
            while( iter.hasNext()){
                BusinessService bs =
                  getBusinessServiceFromJAXRService( (Service)iter.next());
                bvect.add(bs);
            }

            Collection users = org.getUsers();
            System.out.println( "?Org has users="+users.isEmpty());
            Iterator it  = users.iterator();
            while( it.hasNext()){
                Contact ct =
                  getContactFromJAXRUser( (User)it.next());
                cvect.add(ct);
            }

            bss.setBusinessServiceVector( bvect );
            cts.setContactVector( cvect );
            biz.setContacts( cts );

            biz.setBusinessServices( bss );
        }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        return biz;
    }

     /**
     * Convert JAXR User Object to UDDI  Contact
     */
    private Contact getContactFromJAXRUser( User user)
    throws JAXRException {
         Contact ct = new Contact();
         Vector addvect = new Vector();
         Vector phonevect = new Vector();
         Vector emailvect = new Vector();
         try{
             ct.setPersonName( new PersonName(user.getPersonName().getFullName()) );
             //Postal Address
             Collection postc = user.getPostalAddresses();
             Iterator iterator  = postc.iterator();
              while( iterator.hasNext()){
                  PostalAddress post = (PostalAddress)iterator.next();
                  addvect.add( getAddress( post) );
              }
             //Phone Numbers
             Collection ph = user.getTelephoneNumbers(null);
             Iterator it  = ph.iterator();
              while( it.hasNext()){
                 TelephoneNumber t = (TelephoneNumber)it .next();
                 Phone phone = new Phone();
                 String str =  t.getNumber();
                 System.out.println( "Telephone="+str );
                 phone.setValue(str);
                // phone.setText( str );
                 phonevect.add(phone);
             }

             //Email Addresses
             Collection ec = user.getEmailAddresses();
             Iterator iter = ec.iterator();
             while( iter.hasNext()){
                 EmailAddress ea = (EmailAddress)iter.next();
                 Email email = new Email();
                 email.setValue(ea.getAddress());
                 //email.setText( ea.getAddress() );
                 email.setUseType(ea.getType());
                 emailvect.add(email);
             }
             ct.setAddressVector( addvect );
             ct.setPhoneVector( phonevect );
             ct.setEmailVector( emailvect );
              }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        return ct;
    }

     /**
     * Get UDDI Address given JAXR Postal Address
     */
    private Address getAddress( PostalAddress post)
    throws JAXRException {
        Address address = new Address();

        Vector addvect  = new Vector();

        String stnum = post.getStreetNumber();
        String st  = post.getStreet();
        String city = post.getCity();
        String country = post.getCountry();
        String code = post.getPostalCode();
        String state = post.getStateOrProvince();

        AddressLine stnumAL = new AddressLine();
        stnumAL.setKeyName( "STREET_NUMBER");
        stnumAL.setKeyValue(stnum );

        AddressLine stAL = new AddressLine();
        stAL.setKeyName( "STREET");
        stAL.setKeyValue(st );

        AddressLine cityAL = new AddressLine();
        cityAL.setKeyName( "CITY");
        cityAL.setKeyValue(city );

        AddressLine countryAL = new AddressLine();
        countryAL.setKeyName( "COUNTRY");
        countryAL.setKeyValue(country );

        AddressLine codeAL = new AddressLine();
        codeAL.setKeyName( "POSTALCODE");
        codeAL.setKeyValue(code );

        AddressLine stateAL = new AddressLine();
        stateAL.setKeyName( "STATE");
        stateAL.setKeyValue(state );

        //Add the AddressLine to vector
        addvect.add(stnumAL);
        addvect.add(stAL);
        addvect.add(cityAL);
        addvect.add(countryAL);
        addvect.add(codeAL);
        addvect.add(stateAL);

        address.setAddressLineVector( addvect );

        return address;
    }

    private   BusinessService getBusinessServiceFromJAXRService( Service serve )
    throws JAXRException {
        BusinessService bs = new BusinessService();
        try{
            InternationalStringImpl iname = (InternationalStringImpl)((RegistryObject) serve).getName();
            String name = iname.getValue();
            //bs.setDefaultNameString( name, Locale.getDefault().getLanguage());
            bs.addName(new Name(name, Locale.getDefault().getLanguage()));
            /**
            bs.setBusinessKey( ((RegistryObject) serve).getKey().getId() );
            **/

            bs.addDescription(new Description(((RegistryObject) serve).getDescription().getValue()));
            System.out.println( "BusinessService="+bs.toString());
        }catch(Exception ud){
                throw new JAXRException( "Apache JAXR Impl:", ud);
        }
        return bs;
    }

}
