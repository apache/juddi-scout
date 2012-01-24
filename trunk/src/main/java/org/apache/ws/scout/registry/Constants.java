/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.scout.registry;

/**
 * This class contains constants borrowed from files in the jUDDI project.
 * 
 * All constants are just UUID's. In theory, even if jUDDI changes their
 * values, it shouldn't affect scout.
 * 
 * Naming convention is [classname in capitals]_[Var name]
 * 
 * e.g TModel.TYPES_TMODEL_KEY from jUDDI would become:
 * 
 * TMODEL_TYPES_TMODEL_KEY
 * 
 * @author <mailto:dbhole@redhat.com>Deepak Bhole
 * 
 */

public class Constants {

	public static final String COMPLETION_STATUS_COMPLETE = "status:complete";

	public static final String COMPLETION_STATUS_TOKEY_INCOMPLETE = "status:toKey_incomplete";

	public static final String COMPLETION_STATUS_FROMKEY_INCOMPLETE = "status:fromKey_incomplete";

	/**
	 * UDDI Type Taxonomy [uddi-org:types] This taxonomy assists in general
	 * categorization of the tModels themselves.
	 */
	public static final String TMODEL_TYPES_TMODEL_KEY = "UUID:C1ACF26D-9672-4404-9D70-39B756E62AB4";

	/**
	 * Business Taxonomy: NAICS (1997 Release) [ntis-gov:naics:1997] This tModel
	 * defines the NAICS (North American Industry Classification System) 1997
	 * Release industry taxonomy.
	 */
	public static final String TMODEL_NAICS_TMODEL_KEY = "UUID:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2";

	/**
	 * Product Taxonomy: UNSPSC (Version 3.1) [unspsc-org:unspsc:3-1] This
	 * tModel defines the UNSPSC (United Nations Standard Products and Services
	 * Code System) version 3.1 product taxonomy. <B>This taxonomy has been
	 * superceeded by the Universal Standard Products and Services
	 * Classification (see {@link #TMODEL_UNSPSC_73_TMODEL_KEY}) taxonomy.
	 */
	public static final String TMODEL_UNSPSC_TMODEL_KEY = "UUID:DB77450D-9FA8-45D4-A7BC-04411D14E384";

	/**
	 * Product and Services Taxonomy:UNSPSC (Version 7.3) [unspsc-org:unspsc]
	 * This tModel defines the UNSPSC (Universal Standard Products and Services
	 * Classification) version 7.3 product and services taxonomy.
	 */
	public static final String TMODEL_UNSPSC_73_TMODEL_KEY = "UUID:CD153257-086A-4237-B336-6BDCBDCC6634";

	/**
	 * ISO 3166 Geographic Taxonomy [uddi-org:iso-ch:3166-1999] This tModel
	 * defines the ISO 3166 geographic classification taxonomy.
	 */
	public static final String TMODEL_ISO_CH_TMODEL_KEY = "UUID:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88";

	/**
	 * UDDI Other Taxonomy [uddi-org:misc-taxomony] This tModel defines an
	 * unidentified taxonomy.
	 */
	public static final String TMODEL_GENERAL_KEYWORDS_TMODEL_KEY = "UUID:A035A07C-F362-44dd-8F95-E2B134BF43B4";

	/**
	 * UDDI Owning Business [uddi-org:owningBusiness] This tModel identifies the
	 * businessEntity that published or owns the tagged information. Used with
	 * tModels to establish an 'owned' relationship with a registered
	 * businessEntity.
	 */
	public static final String TMODEL_OWNING_BUSINESS_TMODEL_KEY = "UUID:4064C064-6D14-4F35-8953-9652106476A9";

	/**
	 * UDDI businessEntity relationship [uddi-org:relationships] This tModel is
	 * used to describe business relationships. Used in the publisher assertion
	 * messages.
	 */
	public static final String TMODEL_RELATIONSHIPS_TMODEL_KEY = "UUID:807A2C6A-EE22-470D-ADC7-E0424A337C03";

	/**
	 * UDDI Operators [uddi-org:operators] This checked value set is used to
	 * identify UDDI operators.
	 */
	public static final String TMODEL_OPERATORS_TMODEL_KEY = "UUID:327A56F0-3299-4461-BC23-5CD513E95C55";

	/**
	 * D-U-N-S� Number Identifier System [dnb-com:D-U-N-S] This tModel is used
	 * for the Dun & Bradstreet D-U-N-S� Number identifier.
	 */
	public static final String TMODEL_D_U_N_S_TMODEL_KEY = "UUID:8609C81E-EE1F-4D5A-B202-3EB13AD01823";

	/**
	 * Thomas Register Supplier Identifier Code System
	 * [thomasregister-com:supplierID] This tModel is used for the Thomas
	 * Register supplier identifier codes.
	 */
	public static final String TMODEL_THOMAS_REGISTER_TMODEL_KEY = "UUID:B1B1BAF5-2329-43E6-AE13-BA8E97195039";

	/**
	 * UDDI IsReplacedBy [uddi-org:isReplacedBy] An identifier system used to
	 * point (using UDDI keys) to the tModel (or businessEntity) that is the
	 * logical replacement for the one in which isReplacedBy is used.
	 */
	public static final String TMODEL_IS_REPLACED_BY_TMODEL_KEY = "UUID:E59AE320-77A5-11D5-B898-0004AC49CC1E";

	/**
	 * Email based web service [uddi-org:smtp] This tModel is used to describe a
	 * web service that is invoked through SMTP email transmissions. These
	 * transmissions may be between people or applications.
	 */
	public static final String TMODEL_SMTP_TMODEL_KEY = "UUID:93335D49-3EFB-48A0-ACEA-EA102B60DDC6";

	/**
	 * Fax based web service [uddi-org:fax] This tModel is used to describe a
	 * web service that is invoked through fax transmissions. These
	 * transmissions may be between people or applications.
	 */
	public static final String TMODEL_FAX_TMODEL_KEY = "UUID:1A2B00BE-6E2C-42F5-875B-56F32686E0E7";

	/**
	 * FTP based web service [uddi-org:ftp] This tModel is used to describe a
	 * web service that is invoked through file transfers via the ftp protocol.
	 */
	public static final String TMODEL_FTP_TMODEL_KEY = "UUID:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674";

	/**
	 * Telephone based web service [uddi-org:telephone] This tModel is used to
	 * describe a web service that is invoked through a telephone call and
	 * interaction by voice and/or touch-tone.
	 */
	public static final String TMODEL_TELEPHONE_TMODEL_KEY = "UUID:38E12427-5536-4260-A6F9-B5B530E63A07";

	/**
	 * Web browser or HTTP based web service [uddi-org:http] This tModel is used
	 * to describe a web service that is invoked through a web browser and/or
	 * the HTTP protocol.
	 */
	public static final String TMODEL_HTTP_TMODEL_KEY = "UUID:68DE9E80-AD09-469D-8A37-088422BFBC36";

	/**
	 * HTTP Web Home Page URL [uddi-org:homepage] This tModel is used as the
	 * bindingTemplate fingerprint for a web home page reference.
	 */
	public static final String TMODEL_HOMEPAGE_TMODEL_KEY = "UUID:4CEC1CEF-1F68-4B23-8CB7-8BAA763AEB89";

}
