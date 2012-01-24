package org.apache.ws.scout.registry.qa;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.apache.ws.scout.model.uddi.v2.BusinessDetail;
import org.apache.ws.scout.model.uddi.v2.BusinessEntity;

import org.apache.ws.scout.model.uddi.v2.PublisherAssertion;
import org.apache.ws.scout.model.uddi.v2.PublisherAssertions;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before; 

@Aspect
public class BusinessLifeCycleManagerImplReplacement {
	private static final String JUDDI_PROPERTIES_FILE = "juddi.properties";
	
	private static final String JDBC_DRIVER = "juddi.jdbcDriver";
	private static final String JDBC_URL = "juddi.jdbcUrl";
	private static final String JDBC_USER = "juddi.jdbcUsername";
	private static final String JDBC_PASSWORD = "juddi.jdbcPassword";	
			
	private Connection conn;

//	@Before("   call(org.apache.ws.scout.model.uddi.v2.BusinessDetail saveBusiness(String,org.apache.ws.scout.model.uddi.v2.BusinessEntity[])) "
//			+ "&& within(org.apache.ws.scout.registry.BusinessLifeCycleManagerImpl)")
	@Before("   call(boolean equalsIgnoreCase(String)) "
	+ "&& within(org.apache.ws.scout.registry.BusinessLifeCycleManagerImpl)")
	public void beforeSaveOrgCall() throws Exception {
		System.out.println("Clearing the AUTH_TOKEN table...");
		dbinit();
		clearAuthTokens();
		dbclose();
	}	
	
    public void dbinit () throws Exception {
    	URL url = this.getClass().getClassLoader().getResource(JUDDI_PROPERTIES_FILE);
    	Properties prop = new Properties();
    	prop.load(url.openStream());
    	
    	String jdbcURL = System.getProperty(JDBC_URL, prop.getProperty(JDBC_URL));
    	String jdbcDriver = System.getProperty(JDBC_DRIVER, prop.getProperty(JDBC_DRIVER)); 
    	String jdbcUser = System.getProperty(JDBC_USER, prop.getProperty(JDBC_USER));
    	String jdbcPass = System.getProperty(JDBC_PASSWORD, prop.getProperty(JDBC_PASSWORD));
    
    	Class.forName(jdbcDriver);
    	conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPass);
    }
    
    public void dbclose() throws Exception {
    	if (conn != null) {
    		conn.close();
    		conn = null;
    	}
    }
    
    public void clearAuthTokens() throws Exception {
    	String delQuery = "delete from AUTH_TOKEN";
    	Statement st = conn.createStatement();
    	st.executeUpdate(delQuery);
    }
	
/*
	@Before("   call(org.apache.ws.scout.model.uddi.v2.ServiceDetail saveService(String,org.apache.ws.scout.model.uddi.v2.BusinessService[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest)")
    public void beforeServiceCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }

	@Before("   call(org.apache.ws.scout.model.uddi.v2.BindingDetail saveBinding(String,org.apache.ws.scout.model.uddi.v2.BindingTemplate[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest)")
    public void beforeSaveServiceBindingCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }
	
	@Before("   call(org.apache.ws.scout.model.uddi.v2.TModelDetail saveTModel(String,org.apache.ws.scout.model.uddi.v2.TModel[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest..*)")
    public void beforeSaveTModelCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }
	
	@Before("   call(org.apache.ws.scout.model.uddi.v2.DispositionReport deleteBusiness(String,String[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest..*)")
    public void beforeDeleteOrgCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }

	@Before("   call(org.apache.ws.scout.model.uddi.v2.DispositionReport deleteService(String,String[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest..*)")
    public void beforeDeleteServiceCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }
	
	@Before("   call(org.apache.ws.scout.model.uddi.v2.DispositionReport deleteTModel(String,String[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest..*)")
	public void beforeDeleteTModelCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }

	@Before("   call(org.apache.ws.scout.model.uddi.v2.DispositionReport deleteBinding(String,String[]))"
			+ "&& within(org.apache.ws.scout.registry.qa.JAXR065AuthTokenCacheTest..*)")
	public void beforeDeleteServiceBindingCall() {
    	System.out.println("POINTCUT: deleteAuthTokens");
    }
    */
}