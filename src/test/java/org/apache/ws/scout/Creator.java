package org.apache.ws.scout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.infomodel.Association;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.EmailAddress;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.PersonName;
import javax.xml.registry.infomodel.PostalAddress;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.TelephoneNumber;
import javax.xml.registry.infomodel.User;

/**
 * Creator for JAXR Registry Objects to faciliate the writing of unittests.
 * 
 * @author kstam
 *
 */
public class Creator {
    
    BusinessLifeCycleManager blm;
    public static String ORGANIZATION_NAME = "TestOrganization";
    
    public Creator(BusinessLifeCycleManager blm) {
        super();
        this.blm = blm;
    }
    /**
     * Creates a JAXR Organization.
     * 
     * @return JAXR Organization
     * @throws JAXRException
     */
    protected InternationalString getIString(String str)
    throws JAXRException
    {
        return blm.createInternationalString(str);
    }
    /**
     * Creates an dummy organization.
     * 
     * @return JAXR Organization
     * @throws JAXRException
     */
    public Organization createOrganization(String name) throws JAXRException
    {
        Organization org = blm.createOrganization(getIString(name));
        org.setDescription(getIString(name + ":description"));
        User user = blm.createUser();
        org.setPrimaryContact(user);
        PersonName personName = blm.createPersonName("John AXel Rose");
        TelephoneNumber telephoneNumber = blm.createTelephoneNumber();
        telephoneNumber.setNumber("111-222-3333");
        telephoneNumber.setType(null);
        PostalAddress address = blm.createPostalAddress("1",
            "Uddi Drive", "Apache Town","CA", "USA", "00000-1111", "");
        
        Collection<PostalAddress> postalAddresses = new ArrayList<PostalAddress>();
        postalAddresses.add(address);
        Collection<EmailAddress> emailAddresses = new ArrayList<EmailAddress>();
        EmailAddress emailAddress = blm.createEmailAddress("jaxr@apache.org");
        emailAddresses.add(emailAddress);
        
        Collection<TelephoneNumber> numbers = new ArrayList<TelephoneNumber>();
        numbers.add(telephoneNumber);
        user.setPersonName(personName);
        user.setPostalAddresses(postalAddresses);
        user.setEmailAddresses(emailAddresses);
        user.setTelephoneNumbers(numbers);
        
        return org;
    }
    /**
     * Creates a dummy Service.
     * 
     * @return JAXR Service
     * @throws JAXRException
     */
    public Service createService(String name) throws JAXRException
    {
        Service service = blm.createService(getIString(name));
        service.setDescription(getIString("Test Services of UDDI Registry"));
        return service;
    }
    /**
     * Creates a dummy ServiceBinding.
     * 
     * @return JAXR ServiceBinding
     * @throws JAXRException
     */
    public ServiceBinding createServiceBinding() throws JAXRException 
    {
        ServiceBinding serviceBinding = blm.createServiceBinding();
        serviceBinding.setName(blm.createInternationalString("JBossESB Test ServiceBinding"));
        serviceBinding.setDescription(blm.createInternationalString("Binding Description"));
        serviceBinding.setAccessURI("http://www.jboss.com/services/TestService");
        return serviceBinding;
    }
    /**
     * Creates a dummy Classification Scheme.
     * 
     * @return JAXR ClassificationScheme
     * @throws JAXRException
     */
    public ClassificationScheme createClassificationScheme(String name) throws JAXRException
    {
        ClassificationScheme cs = blm.createClassificationScheme(getIString(name),
                getIString(""));
        return cs;
    }
    /**
     * Creates a Classficiation for the default dummy classificationScheme.
     * 
     * @param classificationScheme
     * @return JAXR Classification
     * @throws JAXRException
     */
    public Classification createClassification(ClassificationScheme classificationScheme) throws JAXRException
    {
        Classification classification = blm.createClassification(classificationScheme,
                "Java Api for Xml Registries Services","1234");
        return classification;
    }
    /**
     * Creates an association.
     * @param type association type
     * @param registryObject to which the association is built
     * @throws JAXRException
     */
    public void createAssociation(Concept type, RegistryObject registryObject)
    throws JAXRException 
    {
    Association association = blm.createAssociation(registryObject, type);
    
    ArrayList<Association> associations = new ArrayList<Association>();
    associations.add(association);
    
    BulkResponse br = blm.saveAssociations(associations, true);
    if (br.getStatus() == JAXRResponse.STATUS_SUCCESS) {
        System.out.println("Association Saved");
        Collection coll = br.getCollection();
        Iterator iter = coll.iterator();
        while (iter.hasNext()) {
            System.out.println("Saved Key=" + iter.next());
        }// end while
    } else {
        System.err.println("JAXRExceptions " + "occurred during save:");
        Collection exceptions = br.getExceptions();
        Iterator iter = exceptions.iterator();
        while (iter.hasNext()) {
            Exception e = (Exception) iter.next();
            System.err.println(e.toString());
        }
    }
}
    
    

   
}
